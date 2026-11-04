package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ads.AdManager
import com.example.data.ads.AdNetworkConfig
import com.example.data.auth.AuthManager
import com.example.data.model.*
import com.example.data.repository.FirestoreRepository
import com.example.data.seed.DefaultQuizData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen {
    object Home : Screen()
    object QuizCategories : Screen()
    data class QuizPlaying(val category: QuizCategory) : Screen()
    object LevelSelection : Screen()
    data class LevelPlaying(val level: QuizLevel) : Screen()
    object Exams : Screen()
    data class ExamPlaying(val exam: Exam) : Screen()
    object Wallet : Screen()
    object AdsHub : Screen()
    object Leaderboard : Screen()
    object Profile : Screen()
    object AdminPanel : Screen()
    object HelpSupport : Screen()
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val repository = FirestoreRepository(application)
    val authManager = AuthManager(application)
    val adManager = AdManager()

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen stack for BackHandler
    private val screenStack = mutableListOf<Screen>(Screen.Home)

    fun navigateTo(screen: Screen) {
        screenStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    // User Profile
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // User Wallet
    private val _userWallet = MutableStateFlow(UserWallet())
    val userWallet: StateFlow<UserWallet> = _userWallet.asStateFlow()

    // Categories
    val categories: StateFlow<List<QuizCategory>> = repository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultQuizData.categories)

    // 100 Levels
    val levels: List<QuizLevel> = DefaultQuizData.generate100Levels()

    // Exams
    val exams: StateFlow<List<Exam>> = repository.observeExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultQuizData.exams)

    // Transactions & Withdrawals
    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _withdrawals = MutableStateFlow<List<WithdrawalRequest>>(emptyList())
    val withdrawals: StateFlow<List<WithdrawalRequest>> = _withdrawals.asStateFlow()

    // Leaderboard
    val leaderboard: StateFlow<List<UserProfile>> = repository.observeLeaderboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin streams
    val allUsers: StateFlow<List<UserProfile>> = repository.observeAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWithdrawals: StateFlow<List<WithdrawalRequest>> = repository.observeAllWithdrawals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback messages
    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    fun clearSnackMessage() {
        _snackMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackMessage.value = msg
    }

    // Active Quiz Play State
    private val _activeQuizQuestions = MutableStateFlow<List<Question>>(emptyList())
    val activeQuizQuestions: StateFlow<List<Question>> = _activeQuizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)
    val selectedOption: StateFlow<Int?> = _selectedOption.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizEarnedPoints = MutableStateFlow(0)
    val quizEarnedPoints: StateFlow<Int> = _quizEarnedPoints.asStateFlow()

    private val _quizTimer = MutableStateFlow(30)
    val quizTimer: StateFlow<Int> = _quizTimer.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    private var quizTimerJob: Job? = null

    // Active Exam Play State
    private val _activeExamQuestions = MutableStateFlow<List<Question>>(emptyList())
    val activeExamQuestions: StateFlow<List<Question>> = _activeExamQuestions.asStateFlow()

    private val _examCurrentIndex = MutableStateFlow(0)
    val examCurrentIndex: StateFlow<Int> = _examCurrentIndex.asStateFlow()

    private val _examSelectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val examSelectedAnswers: StateFlow<Map<Int, Int>> = _examSelectedAnswers.asStateFlow()

    private val _examTimeRemaining = MutableStateFlow(300)
    val examTimeRemaining: StateFlow<Int> = _examTimeRemaining.asStateFlow()

    private val _isExamFinished = MutableStateFlow(false)
    val isExamFinished: StateFlow<Boolean> = _isExamFinished.asStateFlow()

    private val _examFinalScore = MutableStateFlow(0)
    val examFinalScore: StateFlow<Int> = _examFinalScore.asStateFlow()

    private val _examPassed = MutableStateFlow(false)
    val examPassed: StateFlow<Boolean> = _examPassed.asStateFlow()

    private var examTimerJob: Job? = null

    // Rewarded Ad Player state
    private val _isWatchingRewardedAd = MutableStateFlow(false)
    val isWatchingRewardedAd: StateFlow<Boolean> = _isWatchingRewardedAd.asStateFlow()

    private val _adCountdown = MutableStateFlow(10)
    val adCountdown: StateFlow<Int> = _adCountdown.asStateFlow()

    private var adCountdownJob: Job? = null

    init {
        viewModelScope.launch {
            authManager.currentUser.collectLatest { user ->
                if (user != null) {
                    loadUserData(user.uid, user.displayName ?: "ব্যবহারকারী", user.email ?: "", user.photoUrl?.toString() ?: "")
                    repository.seedInitialDataIfEmpty()
                } else {
                    _userProfile.value = null
                    _userWallet.value = UserWallet()
                }
            }
        }
    }

    fun loadUserData(uid: String, name: String, email: String, photoUrl: String) {
        viewModelScope.launch {
            val profile = repository.getOrCreateUserProfile(uid, name, email, photoUrl)
            _userProfile.value = profile

            // Observe Wallet
            launch {
                repository.observeWallet(uid).collectLatest { wallet ->
                    _userWallet.value = wallet
                }
            }

            // Observe Transactions
            launch {
                repository.observeTransactions(uid).collectLatest { list ->
                    _transactions.value = list
                }
            }

            // Observe Withdrawals
            launch {
                repository.observeUserWithdrawals(uid).collectLatest { list ->
                    _withdrawals.value = list
                }
            }
        }
    }

    // Quiz Session
    fun startCategoryQuiz(category: QuizCategory) {
        viewModelScope.launch {
            val qList = DefaultQuizData.questions.filter { it.categoryId == category.id }.shuffled()
            val questions = if (qList.isNotEmpty()) qList else DefaultQuizData.questions.shuffled().take(5)
            _activeQuizQuestions.value = questions
            _currentQuestionIndex.value = 0
            _selectedOption.value = null
            _isAnswerSubmitted.value = false
            _quizScore.value = 0
            _quizEarnedPoints.value = 0
            _isQuizFinished.value = false
            navigateTo(Screen.QuizPlaying(category))
            startQuizTimer()
        }
    }

    fun startLevelQuiz(level: QuizLevel) {
        viewModelScope.launch {
            val questions = DefaultQuizData.questions.shuffled().take(5)
            _activeQuizQuestions.value = questions
            _currentQuestionIndex.value = 0
            _selectedOption.value = null
            _isAnswerSubmitted.value = false
            _quizScore.value = 0
            _quizEarnedPoints.value = 0
            _isQuizFinished.value = false
            navigateTo(Screen.LevelPlaying(level))
            startQuizTimer()
        }
    }

    private fun startQuizTimer() {
        quizTimerJob?.cancel()
        _quizTimer.value = 30
        quizTimerJob = viewModelScope.launch {
            while (_quizTimer.value > 0 && !_isAnswerSubmitted.value && !_isQuizFinished.value) {
                delay(1000)
                _quizTimer.value -= 1
            }
            if (_quizTimer.value == 0 && !_isAnswerSubmitted.value) {
                // Time up, auto submit wrong
                submitQuizAnswer(-1)
            }
        }
    }

    fun selectQuizOption(index: Int) {
        if (!_isAnswerSubmitted.value && !_isQuizFinished.value) {
            _selectedOption.value = index
        }
    }

    fun submitQuizAnswer(chosenIndex: Int = _selectedOption.value ?: -1) {
        if (_isAnswerSubmitted.value) return
        _isAnswerSubmitted.value = true
        quizTimerJob?.cancel()

        val q = _activeQuizQuestions.value.getOrNull(_currentQuestionIndex.value) ?: return
        val isCorrect = chosenIndex == q.correctAnswer
        if (isCorrect) {
            _quizScore.value += 1
            _quizEarnedPoints.value += q.points
        }
    }

    fun nextQuizQuestion(onLevelCompleted: ((Int) -> Unit)? = null) {
        val nextIdx = _currentQuestionIndex.value + 1
        if (nextIdx < _activeQuizQuestions.value.size) {
            _currentQuestionIndex.value = nextIdx
            _selectedOption.value = null
            _isAnswerSubmitted.value = false
            startQuizTimer()
        } else {
            // Quiz Complete
            _isQuizFinished.value = true
            val earned = _quizEarnedPoints.value
            if (earned > 0) {
                viewModelScope.launch {
                    repository.creditReward(
                        type = "quiz_reward",
                        points = earned.toLong(),
                        description = "কুইজ প্রতিযোগিতা পুরস্কার",
                        referenceId = "quiz_${System.currentTimeMillis()}"
                    )
                    repository.incrementCompletedQuiz()
                }
            }
            onLevelCompleted?.invoke(_quizScore.value)
        }
    }

    fun handleLevelFinish(level: QuizLevel, correctCount: Int) {
        val total = _activeQuizQuestions.value.size
        val percentage = if (total > 0) (correctCount * 100) / total else 0
        if (percentage >= level.passingScore) {
            val nextLvl = level.levelNumber + 1
            val currentLvl = _userProfile.value?.currentLevel ?: 1
            if (nextLvl > currentLvl) {
                viewModelScope.launch {
                    repository.incrementUserLevel(nextLvl)
                    repository.creditReward(
                        type = "level_reward",
                        points = level.rewardPoints.toLong(),
                        description = "${level.title} সমাপ্তির বোনাস",
                        referenceId = "lvl_${level.levelNumber}"
                    )
                    _userProfile.value = _userProfile.value?.copy(currentLevel = nextLvl)
                }
            }
        }
    }

    // Exam Session
    fun startExam(exam: Exam) {
        val qList = DefaultQuizData.questions.shuffled().take(10)
        _activeExamQuestions.value = qList
        _examCurrentIndex.value = 0
        _examSelectedAnswers.value = emptyMap()
        _examTimeRemaining.value = exam.duration
        _isExamFinished.value = false
        _examFinalScore.value = 0
        _examPassed.value = false

        navigateTo(Screen.ExamPlaying(exam))

        examTimerJob?.cancel()
        examTimerJob = viewModelScope.launch {
            while (_examTimeRemaining.value > 0 && !_isExamFinished.value) {
                delay(1000)
                _examTimeRemaining.value -= 1
            }
            if (_examTimeRemaining.value <= 0 && !_isExamFinished.value) {
                submitExam(exam)
            }
        }
    }

    fun selectExamAnswer(questionIndex: Int, optionIndex: Int) {
        if (!_isExamFinished.value) {
            val updated = _examSelectedAnswers.value.toMutableMap()
            updated[questionIndex] = optionIndex
            _examSelectedAnswers.value = updated
        }
    }

    fun goToNextExamQuestion() {
        if (_examCurrentIndex.value < _activeExamQuestions.value.size - 1) {
            _examCurrentIndex.value += 1
        }
    }

    fun goToPreviousExamQuestion() {
        if (_examCurrentIndex.value > 0) {
            _examCurrentIndex.value -= 1
        }
    }

    fun submitExam(exam: Exam) {
        if (_isExamFinished.value) return
        _isExamFinished.value = true
        examTimerJob?.cancel()

        var correct = 0
        val questions = _activeExamQuestions.value
        val answers = _examSelectedAnswers.value

        for (i in questions.indices) {
            if (answers[i] == questions[i].correctAnswer) {
                correct++
            }
        }

        val markPerQuestion = exam.totalMarks / questions.size
        val finalScore = correct * markPerQuestion
        val isPassed = finalScore >= exam.passingMarks

        _examFinalScore.value = finalScore
        _examPassed.value = isPassed

        if (isPassed) {
            viewModelScope.launch {
                repository.creditReward(
                    type = "exam_reward",
                    points = exam.rewardPoints.toLong(),
                    description = "${exam.title} উত্তীর্ণ পুরস্কার",
                    referenceId = exam.id
                )
                repository.incrementCompletedExam()
            }
        }
    }

    // Daily Reward
    fun claimDailyReward(dayNumber: Int, points: Long) {
        viewModelScope.launch {
            val result = repository.claimDailyReward(dayNumber, points)
            if (result.isSuccess) {
                showMessage("অভিনন্দন! আপনি $points পয়েন্ট দৈনিক বোনাস পেয়েছেন!")
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "দৈনিক বোনাস গ্রহণ করা যায়নি")
            }
        }
    }

    // Submit Withdrawal
    fun submitWithdrawal(method: String, account: String, points: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            val result = repository.submitWithdrawal(method, account, points)
            if (result.isSuccess) {
                showMessage("উত্তোলনের আবেদন সফলভাবে গৃহীত হয়েছে! শীঘ্রই যাচাই করে পেমেন্ট পাঠানো হবে।")
                onDone()
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "আবেদন প্রক্রিয়া ব্যর্থ হয়েছে")
            }
        }
    }

    // Rewarded Ad Simulation
    fun startWatchingRewardedAd(onCompleted: () -> Unit) {
        if (!adManager.canWatchRewardedAd()) {
            showMessage("আজকের বিজ্ঞাপনের কোটা পূর্ণ হয়েছে! আগামীকাল পুনরায় চেষ্টা করুন।")
            return
        }

        _isWatchingRewardedAd.value = true
        _adCountdown.value = 10
        adCountdownJob?.cancel()

        adCountdownJob = viewModelScope.launch {
            while (_adCountdown.value > 0) {
                delay(1000)
                _adCountdown.value -= 1
            }
            _isWatchingRewardedAd.value = false
            adManager.recordAdWatched()
            val reward = adManager.monetagConfig.value.rewardPoints
            repository.creditReward(
                type = "ad_reward",
                points = reward,
                description = "Monetag স্পনসর বিজ্ঞাপন বোনাস",
                referenceId = "ad_${System.currentTimeMillis()}"
            )
            showMessage("অভিনন্দন! আপনি $reward পয়েন্ট বিজ্ঞাপন বোনাস পেয়েছেন!")
            onCompleted()
        }
    }

    fun dismissAdPlayer() {
        adCountdownJob?.cancel()
        _isWatchingRewardedAd.value = false
    }

    // Admin Controls
    fun adminApproveWithdrawal(id: String) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(id, "approved", "অনুমোদিত হয়েছে")
            showMessage("উত্তোলন অনুমোদিত হয়েছে")
        }
    }

    fun adminRejectWithdrawal(id: String, note: String) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(id, "rejected", note)
            showMessage("উত্তোলন বাতিল করা হয়েছে এবং পয়েন্ট ফেরত দেওয়া হয়েছে")
        }
    }

    fun adminMarkPaidWithdrawal(id: String, transactionId: String) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(id, "paid", "TRX ID: $transactionId")
            showMessage("টাকা পাঠানো সম্পন্ন এবং ট্রানজেকশন রেকর্ড আপডেট হয়েছে")
        }
    }

    fun adminToggleUserStatus(uid: String, currentStatus: String) {
        viewModelScope.launch {
            val newStatus = if (currentStatus == "active") "suspended" else "active"
            repository.toggleUserAccount(uid, newStatus)
            showMessage("ব্যবহারকারীর অ্যাকাউন্ট স্ট্যাটাস পরিবর্তন হয়েছে: $newStatus")
        }
    }
}
