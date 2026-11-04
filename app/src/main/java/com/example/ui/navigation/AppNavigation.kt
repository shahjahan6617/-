package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoyalPurple
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

data class BottomNavItem(
    val route: Screen,
    val title: String,
    val icon: ImageVector
)

@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    var showDailyBonusDialog by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "হোম", Icons.Default.Home),
        BottomNavItem(Screen.AdsHub, "বিজ্ঞাপন", Icons.Default.OndemandVideo),
        BottomNavItem(Screen.Exams, "পরীক্ষা", Icons.Default.Timer),
        BottomNavItem(Screen.Wallet, "ওয়ালেট", Icons.Default.AccountBalanceWallet),
        BottomNavItem(Screen.Profile, "প্রোফাইল", Icons.Default.Person)
    )

    val showBottomBar = currentScreen is Screen.Home ||
            currentScreen is Screen.AdsHub ||
            currentScreen is Screen.Exams ||
            currentScreen is Screen.Wallet ||
            currentScreen is Screen.Profile

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = when (item.route) {
                            Screen.Home -> currentScreen is Screen.Home
                            Screen.AdsHub -> currentScreen is Screen.AdsHub
                            Screen.Exams -> currentScreen is Screen.Exams
                            Screen.Wallet -> currentScreen is Screen.Wallet
                            Screen.Profile -> currentScreen is Screen.Profile
                            else -> false
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                viewModel.navigateTo(item.route)
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) DeepNavy else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    color = if (isSelected) DeepNavy else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = GoldAccent.copy(alpha = 0.35f)
                            )
                        )
                    }
                }
            }
        },
        snackbarHost = {
            val snackMessage by viewModel.snackMessage.collectAsState()
            snackMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearSnackMessage() }) {
                            Text("ঠিক আছে", color = GoldAccent)
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onOpenDailyBonus = { showDailyBonusDialog = true }
                    )
                }
                is Screen.QuizCategories -> {
                    QuizCategoriesScreen(viewModel = viewModel)
                }
                is Screen.QuizPlaying -> {
                    QuizPlayingScreen(
                        title = screen.category.name,
                        viewModel = viewModel,
                        onFinish = { viewModel.navigateBack() }
                    )
                }
                is Screen.LevelSelection -> {
                    LevelScreen(viewModel = viewModel)
                }
                is Screen.LevelPlaying -> {
                    QuizPlayingScreen(
                        title = screen.level.title,
                        viewModel = viewModel,
                        onFinish = {
                            viewModel.handleLevelFinish(screen.level, viewModel.quizScore.value)
                            viewModel.navigateBack()
                        }
                    )
                }
                is Screen.Exams -> {
                    ExamScreen(viewModel = viewModel)
                }
                is Screen.ExamPlaying -> {
                    LiveExamScreen(
                        exam = screen.exam,
                        viewModel = viewModel,
                        onFinish = { viewModel.navigateBack() }
                    )
                }
                is Screen.Wallet -> {
                    WalletScreen(viewModel = viewModel)
                }
                is Screen.AdsHub -> {
                    AdsScreen(viewModel = viewModel)
                }
                is Screen.Leaderboard -> {
                    LeaderboardScreen(viewModel = viewModel)
                }
                is Screen.Profile -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onSignOut = onSignOut
                    )
                }
                is Screen.AdminPanel -> {
                    AdminPanelScreen(viewModel = viewModel)
                }
                is Screen.HelpSupport -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onSignOut = onSignOut
                    )
                }
            }
        }

        // Daily Check-in Dialog
        if (showDailyBonusDialog) {
            DailyCheckInDialog(
                onClaim = { day, pts ->
                    viewModel.claimDailyReward(day, pts)
                },
                onDismiss = { showDailyBonusDialog = false }
            )
        }
    }
}
