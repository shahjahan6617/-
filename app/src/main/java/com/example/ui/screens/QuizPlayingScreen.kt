package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayingScreen(
    title: String,
    viewModel: AppViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onFinish()
    }

    val questions by viewModel.activeQuizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val timer by viewModel.quizTimer.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val earnedPoints by viewModel.quizEarnedPoints.collectAsState()
    val isFinished by viewModel.isQuizFinished.collectAsState()

    val currentQ = questions.getOrNull(currentIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit"
                        )
                    }
                },
                actions = {
                    // Timer indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (timer <= 5) ErrorRed.copy(alpha = 0.15f)
                                else RoyalPurple.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = if (timer <= 5) ErrorRed else RoyalPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${BanglaUtils.toBanglaDigits(timer)} সে.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timer <= 5) ErrorRed else RoyalPurple
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
        if (currentQ != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GoldAccent,
                    trackColor = CardLightGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "প্রশ্ন ${BanglaUtils.toBanglaDigits(currentIndex + 1)} / ${BanglaUtils.toBanglaDigits(questions.size)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondarySlate
                    )
                    Text(
                        text = "+${BanglaUtils.toBanglaDigits(currentQ.points)} পয়েন্ট",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = currentQ.question,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4 Options
                val optionPrefixes = listOf("ক", "খ", "গ", "ঘ")
                currentQ.options.forEachIndexed { idx, optText ->
                    val isSelected = selectedOption == idx
                    val isCorrect = idx == currentQ.correctAnswer

                    val (bgColor, borderColor, textColor) = when {
                        isSubmitted && isCorrect -> Triple(
                            SuccessGreen.copy(alpha = 0.15f),
                            SuccessGreen,
                            SuccessGreen
                        )
                        isSubmitted && isSelected && !isCorrect -> Triple(
                            ErrorRed.copy(alpha = 0.15f),
                            ErrorRed,
                            ErrorRed
                        )
                        isSelected -> Triple(
                            RoyalPurple.copy(alpha = 0.1f),
                            RoyalPurple,
                            RoyalPurple
                        )
                        else -> Triple(
                            MaterialTheme.colorScheme.surface,
                            CardLightGray,
                            MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isSubmitted) {
                                viewModel.selectQuizOption(idx)
                                viewModel.submitQuizAnswer(idx)
                            },
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(borderColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionPrefixes.getOrElse(idx) { "${idx + 1}" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optText,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected || (isSubmitted && isCorrect)) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSubmitted) {
                                if (isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Wrong",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Explanation Box
                AnimatedVisibility(visible = isSubmitted && currentQ.explanation.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = NavySlate.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ব্যাখ্যা:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentQ.explanation,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Next Button
                if (isSubmitted) {
                    Button(
                        onClick = { viewModel.nextQuizQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (currentIndex + 1 < questions.size) "পরবর্তী প্রশ্ন" else "ফলাফল দেখুন",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Quiz Completed Dialog
        if (isFinished) {
            AlertDialog(
                onDismissRequest = onFinish,
                icon = {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "কুইজ সম্পন্ন হয়েছে!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "আপনার সঠিক উত্তর: ${BanglaUtils.toBanglaDigits(score)} / ${BanglaUtils.toBanglaDigits(questions.size)} টি",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SuccessGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "অর্জিত পয়েন্ট: +${BanglaUtils.toBanglaDigits(earnedPoints)} পয়েন্ট",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = onFinish,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("হোমে ফিরে যান")
                    }
                }
            )
        }
    }
}
