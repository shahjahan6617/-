package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.*
import com.example.data.seed.DefaultQuizData
import com.example.util.FirestoreErrorHandler
import com.example.util.OperationType
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirestoreRepository(
    private val db: FirebaseFirestore,
    val context: Context
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        context.applicationContext
    )

    private val auth = Firebase.auth

    fun getCurrentUid(): String? = auth.currentUser?.uid

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    suspend fun getOrCreateUserProfile(
        uid: String,
        name: String,
        email: String,
        photoUrl: String
    ): UserProfile {
        val docRef = db.collection("users").document(uid)
        return try {
            val snapshot = docRef.get().await()
            val isAdminEmail = email.equals("sarbanunesa66@gmail.com", ignoreCase = true)

            if (snapshot.exists()) {
                val existing = snapshot.toObject(UserProfile::class.java) ?: UserProfile(uid = uid)
                // Update last login & admin status if matching
                val updates = mutableMapOf<String, Any>(
                    "lastLoginAt" to FieldValue.serverTimestamp()
                )
                if (isAdminEmail && !existing.isAdmin) {
                    updates["isAdmin"] = true
                }
                docRef.update(updates).await()
                existing.copy(isAdmin = existing.isAdmin || isAdminEmail)
            } else {
                val newProfile = UserProfile(
                    uid = uid,
                    name = name.ifBlank { "ব্যবহারকারী" },
                    email = email,
                    photoUrl = photoUrl,
                    accountStatus = "active",
                    currentLevel = 1,
                    totalQuizCompleted = 0,
                    totalExamCompleted = 0,
                    isAdmin = isAdminEmail
                )
                docRef.set(newProfile).await()
                // Initialize user wallet
                initializeWallet(uid)
                newProfile
            }
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.READ, docRef.path)
            UserProfile(uid = uid, name = name, email = email)
        }
    }

    private suspend fun initializeWallet(uid: String) {
        val walletRef = db.collection("wallets").document(uid)
        try {
            val snapshot = walletRef.get().await()
            if (!snapshot.exists()) {
                val wallet = UserWallet(
                    uid = uid,
                    availablePoints = 50L, // 50 Welcome bonus points
                    reservedPoints = 0L,
                    lifetimeEarnedPoints = 50L
                )
                walletRef.set(wallet).await()

                // Welcome transaction
                val txRef = db.collection("transactions").document()
                val tx = WalletTransaction(
                    id = txRef.id,
                    uid = uid,
                    type = "quiz_reward",
                    points = 50L,
                    description = "স্বাগতম বোনাস"
                )
                txRef.set(tx).await()
            }
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, walletRef.path)
        }
    }

    fun observeWallet(uid: String): Flow<UserWallet> {
        val docRef = db.collection("wallets").document(uid)
        return docRef.snapshots().map { snapshot ->
            snapshot.toObject(UserWallet::class.java) ?: UserWallet(uid = uid)
        }.catch { e ->
            if (e is Exception) FirestoreErrorHandler.handleFirestoreError(e, OperationType.READ, docRef.path)
            emit(UserWallet(uid = uid))
        }
    }

    fun observeTransactions(uid: String): Flow<List<WalletTransaction>> {
        val query = db.collection("transactions")
            .whereEqualTo("uid", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)

        return query.snapshots().map { snapshot ->
            snapshot.toObjects(WalletTransaction::class.java)
        }.catch { e ->
            if (e is Exception) FirestoreErrorHandler.handleFirestoreError(e, OperationType.LIST, "transactions")
            emit(emptyList())
        }
    }

    fun observeUserWithdrawals(uid: String): Flow<List<WithdrawalRequest>> {
        val query = db.collection("withdrawals")
            .whereEqualTo("uid", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        return query.snapshots().map { snapshot ->
            snapshot.toObjects(WithdrawalRequest::class.java)
        }.catch { e ->
            if (e is Exception) FirestoreErrorHandler.handleFirestoreError(e, OperationType.LIST, "withdrawals")
            emit(emptyList())
        }
    }

    suspend fun submitWithdrawal(
        paymentMethod: String,
        accountNumber: String,
        requestedPoints: Long
    ): Result<Unit> {
        val uid = requireUserId()
        val walletRef = db.collection("wallets").document(uid)
        val withdrawalRef = db.collection("withdrawals").document()
        val txRef = db.collection("transactions").document()

        return try {
            db.runTransaction { tx ->
                val walletSnap = tx.get(walletRef)
                val currentWallet = walletSnap.toObject(UserWallet::class.java)
                    ?: throw IllegalStateException("ওয়ালেট পাওয়া যায়নি")

                if (currentWallet.availablePoints < requestedPoints) {
                    throw IllegalStateException("পর্যাপ্ত পয়েন্ট নেই")
                }
                if (requestedPoints < 1000) {
                    throw IllegalStateException("সর্বনিম্ন উত্তোলন ১,০০০ পয়েন্ট (৫০ ৳)")
                }

                val amountBDT = requestedPoints / 20 // 1000 points = 50 BDT

                // Atomic wallet points reservation
                tx.update(walletRef, mapOf(
                    "availablePoints" to currentWallet.availablePoints - requestedPoints,
                    "reservedPoints" to currentWallet.reservedPoints + requestedPoints,
                    "updatedAt" to FieldValue.serverTimestamp()
                ))

                // Create withdrawal record
                val withdrawal = WithdrawalRequest(
                    id = withdrawalRef.id,
                    uid = uid,
                    paymentMethod = paymentMethod,
                    accountNumber = accountNumber,
                    requestedPoints = requestedPoints,
                    amountBDT = amountBDT,
                    status = "pending",
                    adminNote = "অপেক্ষমাণ"
                )
                tx.set(withdrawalRef, withdrawal)

                // Audit transaction
                val transaction = WalletTransaction(
                    id = txRef.id,
                    uid = uid,
                    type = "withdrawal_reserve",
                    points = requestedPoints,
                    description = "$paymentMethod ($accountNumber) এ উত্তোলনের আবেদন",
                    referenceId = withdrawalRef.id
                )
                tx.set(txRef, transaction)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, walletRef.path)
            Result.failure(e)
        }
    }

    suspend fun claimDailyReward(dayNumber: Int, points: Long): Result<Unit> {
        val uid = requireUserId()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val claimDocId = "${uid}_$dateStr"
        val claimRef = db.collection("daily_rewards").document(claimDocId)
        val walletRef = db.collection("wallets").document(uid)
        val txRef = db.collection("transactions").document()

        return try {
            db.runTransaction { tx ->
                val claimSnap = tx.get(claimRef)
                if (claimSnap.exists()) {
                    throw IllegalStateException("আজকের দৈনিক বোনাস ইতোমধ্যে গ্রহণ করা হয়েছে")
                }

                // Add points to wallet
                val walletSnap = tx.get(walletRef)
                val currentWallet = walletSnap.toObject(UserWallet::class.java) ?: UserWallet(uid = uid)

                tx.update(walletRef, mapOf(
                    "availablePoints" to currentWallet.availablePoints + points,
                    "lifetimeEarnedPoints" to currentWallet.lifetimeEarnedPoints + points,
                    "updatedAt" to FieldValue.serverTimestamp()
                ))

                val claim = DailyRewardClaim(
                    id = claimDocId,
                    uid = uid,
                    date = dateStr,
                    dayNumber = dayNumber,
                    rewardPoints = points
                )
                tx.set(claimRef, claim)

                val transaction = WalletTransaction(
                    id = txRef.id,
                    uid = uid,
                    type = "daily_checkin",
                    points = points,
                    description = "দৈনিক হাজিরা বোনাস (দিন $dayNumber)",
                    referenceId = claimDocId
                )
                tx.set(txRef, transaction)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, claimRef.path)
            Result.failure(e)
        }
    }

    suspend fun creditReward(
        type: String,
        points: Long,
        description: String,
        referenceId: String
    ): Result<Unit> {
        val uid = requireUserId()
        val walletRef = db.collection("wallets").document(uid)
        val txRef = db.collection("transactions").document()

        return try {
            db.runTransaction { tx ->
                val walletSnap = tx.get(walletRef)
                val currentWallet = walletSnap.toObject(UserWallet::class.java) ?: UserWallet(uid = uid)

                tx.update(walletRef, mapOf(
                    "availablePoints" to currentWallet.availablePoints + points,
                    "lifetimeEarnedPoints" to currentWallet.lifetimeEarnedPoints + points,
                    "updatedAt" to FieldValue.serverTimestamp()
                ))

                val transaction = WalletTransaction(
                    id = txRef.id,
                    uid = uid,
                    type = type,
                    points = points,
                    description = description,
                    referenceId = referenceId
                )
                tx.set(txRef, transaction)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, walletRef.path)
            Result.failure(e)
        }
    }

    suspend fun incrementUserLevel(newLevel: Int): Result<Unit> {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        return try {
            userRef.update(mapOf(
                "currentLevel" to newLevel
            )).await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, userRef.path)
            Result.failure(e)
        }
    }

    suspend fun incrementCompletedQuiz(): Result<Unit> {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        return try {
            userRef.update(mapOf(
                "totalQuizCompleted" to FieldValue.increment(1)
            )).await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, userRef.path)
            Result.failure(e)
        }
    }

    suspend fun incrementCompletedExam(): Result<Unit> {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        return try {
            userRef.update(mapOf(
                "totalExamCompleted" to FieldValue.increment(1)
            )).await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, userRef.path)
            Result.failure(e)
        }
    }

    fun observeCategories(): Flow<List<QuizCategory>> {
        return db.collection("quiz_categories")
            .orderBy("order")
            .snapshots()
            .map { it.toObjects(QuizCategory::class.java) }
            .catch { emit(DefaultQuizData.categories) }
    }

    fun observeQuestions(categoryId: String): Flow<List<Question>> {
        return db.collection("questions")
            .whereEqualTo("categoryId", categoryId)
            .snapshots()
            .map { snap ->
                val list = snap.toObjects(Question::class.java)
                if (list.isNotEmpty()) list
                else DefaultQuizData.questions.filter { it.categoryId == categoryId }
            }
            .catch {
                emit(DefaultQuizData.questions.filter { it.categoryId == categoryId })
            }
    }

    fun observeExams(): Flow<List<Exam>> {
        return db.collection("exams")
            .snapshots()
            .map { snap ->
                val list = snap.toObjects(Exam::class.java)
                if (list.isNotEmpty()) list else DefaultQuizData.exams
            }
            .catch { emit(DefaultQuizData.exams) }
    }

    fun observeLeaderboard(): Flow<List<UserProfile>> {
        return db.collection("users")
            .orderBy("currentLevel", Query.Direction.DESCENDING)
            .limit(25)
            .snapshots()
            .map { it.toObjects(UserProfile::class.java) }
            .catch { emit(emptyList()) }
    }

    // Admin Operations
    fun observeAllUsers(): Flow<List<UserProfile>> {
        return db.collection("users")
            .limit(100)
            .snapshots()
            .map { it.toObjects(UserProfile::class.java) }
            .catch { emit(emptyList()) }
    }

    fun observeAllWithdrawals(): Flow<List<WithdrawalRequest>> {
        return db.collection("withdrawals")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .snapshots()
            .map { it.toObjects(WithdrawalRequest::class.java) }
            .catch { emit(emptyList()) }
    }

    suspend fun updateWithdrawalStatus(
        withdrawalId: String,
        newStatus: String,
        adminNote: String
    ): Result<Unit> {
        val docRef = db.collection("withdrawals").document(withdrawalId)
        return try {
            val snap = docRef.get().await()
            val req = snap.toObject(WithdrawalRequest::class.java)
                ?: throw IllegalStateException("আবেদন পাওয়া যায়নি")

            if (newStatus == "rejected" && req.status == "pending") {
                // Refund reserved points back to available
                val walletRef = db.collection("wallets").document(req.uid)
                db.runTransaction { tx ->
                    val wallet = tx.get(walletRef).toObject(UserWallet::class.java) ?: UserWallet(uid = req.uid)
                    tx.update(walletRef, mapOf(
                        "availablePoints" to wallet.availablePoints + req.requestedPoints,
                        "reservedPoints" to (wallet.reservedPoints - req.requestedPoints).coerceAtLeast(0L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    ))
                    tx.update(docRef, mapOf(
                        "status" to "rejected",
                        "adminNote" to adminNote,
                        "processedAt" to FieldValue.serverTimestamp()
                    ))
                    val txRef = db.collection("transactions").document()
                    tx.set(txRef, WalletTransaction(
                        id = txRef.id,
                        uid = req.uid,
                        type = "withdrawal_refund",
                        points = req.requestedPoints,
                        description = "উত্তোলন বাতিল: পয়েন্ট ফেরত",
                        referenceId = withdrawalId
                    ))
                }.await()
            } else if (newStatus == "paid" && (req.status == "pending" || req.status == "approved")) {
                // Confirm payout: clear reserved points
                val walletRef = db.collection("wallets").document(req.uid)
                db.runTransaction { tx ->
                    val wallet = tx.get(walletRef).toObject(UserWallet::class.java) ?: UserWallet(uid = req.uid)
                    tx.update(walletRef, mapOf(
                        "reservedPoints" to (wallet.reservedPoints - req.requestedPoints).coerceAtLeast(0L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    ))
                    tx.update(docRef, mapOf(
                        "status" to "paid",
                        "adminNote" to adminNote,
                        "processedAt" to FieldValue.serverTimestamp()
                    ))
                    val txRef = db.collection("transactions").document()
                    tx.set(txRef, WalletTransaction(
                        id = txRef.id,
                        uid = req.uid,
                        type = "withdrawal_paid",
                        points = req.requestedPoints,
                        description = "উত্তোলন সফল: টাকা পাঠানো হয়েছে ($adminNote)",
                        referenceId = withdrawalId
                    ))
                }.await()
            } else {
                docRef.update(mapOf(
                    "status" to newStatus,
                    "adminNote" to adminNote,
                    "processedAt" to FieldValue.serverTimestamp()
                )).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun toggleUserAccount(targetUid: String, newStatus: String): Result<Unit> {
        val docRef = db.collection("users").document(targetUid)
        return try {
            docRef.update(mapOf("accountStatus" to newStatus)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        try {
            val catSnap = db.collection("quiz_categories").limit(1).get().await()
            if (catSnap.isEmpty) {
                // Seed categories
                for (cat in DefaultQuizData.categories) {
                    db.collection("quiz_categories").document(cat.id).set(cat).await()
                }
                // Seed questions
                for (q in DefaultQuizData.questions) {
                    db.collection("questions").document(q.id).set(q).await()
                }
                // Seed exams
                for (exam in DefaultQuizData.exams) {
                    db.collection("exams").document(exam.id).set(exam).await()
                }
                // Seed app settings
                db.collection("app_settings").document("general").set(AppSettings()).await()
            }
        } catch (e: Exception) {
            FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, "quiz_categories")
        }
    }
}
