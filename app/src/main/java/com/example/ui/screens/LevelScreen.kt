package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizLevel
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val userProfile by viewModel.userProfile.collectAsState()
    val currentUnlockedLevel = userProfile?.currentLevel ?: 1
    val levels = viewModel.levels

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "১০০ লেভেল প্রগ্রেশন",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "বর্তমান লেভেল: ${BanglaUtils.toBanglaDigits(currentUnlockedLevel)}",
                            fontSize = 12.sp,
                            color = TextSecondarySlate
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(levels) { lvl ->
                val isUnlocked = lvl.levelNumber <= currentUnlockedLevel
                val isCompleted = lvl.levelNumber < currentUnlockedLevel
                val isCurrent = lvl.levelNumber == currentUnlockedLevel

                LevelGridItem(
                    level = lvl,
                    isUnlocked = isUnlocked,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    onClick = {
                        if (isUnlocked) {
                            viewModel.startLevelQuiz(lvl)
                        } else {
                            viewModel.showMessage("লেভেল ${BanglaUtils.toBanglaDigits(lvl.levelNumber)} আনলক করতে পূর্ববর্তী লেভেল সম্পন্ন করুন!")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelGridItem(
    level: QuizLevel,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val (bgColor, contentColor) = when {
        isCompleted -> SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        isCurrent -> GoldAccent.copy(alpha = 0.2f) to DeepNavy
        isUnlocked -> RoyalPurple.copy(alpha = 0.15f) to RoyalPurple
        else -> CardLightGray to TextSecondarySlate.copy(alpha = 0.6f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.95f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextSecondarySlate,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = BanglaUtils.toBanglaDigits(level.levelNumber),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "লেভেল ${BanglaUtils.toBanglaDigits(level.levelNumber)}",
                fontSize = 12.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else TextSecondarySlate
            )

            Text(
                text = "+${BanglaUtils.toBanglaDigits(level.rewardPoints)} প.",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isUnlocked) GoldDark else TextSecondarySlate
            )
        }
    }
}
