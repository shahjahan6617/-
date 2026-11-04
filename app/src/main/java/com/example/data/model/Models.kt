package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val lastLoginAt: Timestamp? = null,
    val accountStatus: String = "active", // "active", "suspended"
    val currentLevel: Int = 1,
    val totalQuizCompleted: Int = 0,
    val totalExamCompleted: Int = 0,
    val isAdmin: Boolean = false
)

@IgnoreExtraProperties
data class QuizCategory(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String = "quiz",
    val isActive: Boolean = true,
    val order: Int = 0
)

@IgnoreExtraProperties
data class Question(
    @DocumentId val id: String = "",
    val categoryId: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0, // 0, 1, 2, 3
    val explanation: String = "",
    val difficulty: String = "medium", // easy, medium, hard
    val points: Int = 10,
    val isActive: Boolean = true
)

@IgnoreExtraProperties
data class QuizLevel(
    @DocumentId val id: String = "",
    val levelNumber: Int = 1,
    val title: String = "",
    val questionIds: List<String> = emptyList(),
    val timeLimit: Int = 60, // seconds
    val passingScore: Int = 70, // percentage e.g. 70%
    val rewardPoints: Int = 50,
    val isActive: Boolean = true
)

@IgnoreExtraProperties
data class Exam(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val examType: String = "daily", // "daily", "weekly", "special"
    val questionIds: List<String> = emptyList(),
    val duration: Int = 300, // seconds (e.g. 5 mins)
    val totalMarks: Int = 100,
    val passingMarks: Int = 60,
    val rewardPoints: Int = 100,
    val isActive: Boolean = true
)

@IgnoreExtraProperties
data class UserWallet(
    @DocumentId val id: String = "",
    val uid: String = "",
    val availablePoints: Long = 0L,
    val reservedPoints: Long = 0L,
    val lifetimeEarnedPoints: Long = 0L,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class WalletTransaction(
    @DocumentId val id: String = "",
    val uid: String = "",
    val type: String = "quiz_reward", // quiz_reward, level_reward, exam_reward, daily_checkin, ad_reward, withdrawal_reserve, withdrawal_refund, withdrawal_paid
    val points: Long = 0L,
    val description: String = "",
    val referenceId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class WithdrawalRequest(
    @DocumentId val id: String = "",
    val uid: String = "",
    val paymentMethod: String = "bKash", // bKash, Nagad, Rocket
    val accountNumber: String = "",
    val requestedPoints: Long = 0L,
    val amountBDT: Long = 0L,
    val status: String = "pending", // pending, approved, rejected, paid
    val adminNote: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val processedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class DailyRewardClaim(
    @DocumentId val id: String = "",
    val uid: String = "",
    val date: String = "", // YYYY-MM-DD
    val dayNumber: Int = 1, // 1 to 7
    val rewardPoints: Long = 20L,
    @ServerTimestamp val claimedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AdSettings(
    @DocumentId val id: String = "",
    val provider: String = "monetag", // monetag, advertica
    val enabled: Boolean = true,
    val placementSettings: String = "zone_banner,zone_rewarded",
    val frequencySettings: String = "1_per_5_min",
    val rewardAmount: Int = 20,
    val dailyLimit: Int = 10,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AppSettings(
    @DocumentId val id: String = "general",
    val pointsPerBDT: Long = 20L, // 1000 pts = 50 BDT -> 20 pts = 1 BDT
    val minimumWithdrawalPoints: Long = 1000L, // 50 BDT
    val dailyRewardPoints: Long = 20L,
    val appVersion: String = "1.0.0",
    val noticeBangla: String = "বাংলা কুইজ ও আয় অ্যাপে স্বাগতম! সঠিক উত্তর দিয়ে পয়েন্ট অর্জন করুন এবং বিকাশ, নগদ ও রকেটের মাধ্যমে টাকা উত্তোলন করুন।",
    val supportPhone: String = "01617866617",
    val supportEmail: String = "support@banglaquiz.com"
)
