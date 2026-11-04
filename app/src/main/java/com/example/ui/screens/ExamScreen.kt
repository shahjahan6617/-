package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exam
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val exams by viewModel.exams.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "অনলাইন মেধা পরীক্ষা",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(RoyalPurple.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = RoyalPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "পরীক্ষার নিয়মাবলী",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "নির্দিষ্ট সময়ের মধ্যে সকল প্রশ্নের উত্তর দিন। পাস নম্বরের বেশি পেলে নিশ্চিত রিওয়ার্ড পয়েন্ট জমা হবে।",
                                fontSize = 12.sp,
                                color = TextSecondarySlate,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            items(exams) { exam ->
                ExamCard(
                    exam = exam,
                    onStart = { viewModel.startExam(exam) }
                )
            }
        }
    }
}

@Composable
private fun ExamCard(
    exam: Exam,
    onStart: () -> Unit
) {
    val (headerGradient, badgeColor) = when (exam.examType) {
        "daily" -> listOf(DeepNavy, NavyLight) to SuccessGreen
        "weekly" -> listOf(RoyalPurple, PurpleAccent) to GoldAccent
        else -> listOf(NavySlate, DeepNavy) to WarningOrange
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(headerGradient))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exam.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (exam.examType) {
                                "daily" -> "দৈনিক"
                                "weekly" -> "সাপ্তাহিক"
                                else -> "বিশেষ"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = exam.description,
                    fontSize = 13.sp,
                    color = TextSecondarySlate,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ExamStatPill(
                        icon = Icons.Default.Timer,
                        label = "সময়",
                        value = "${BanglaUtils.toBanglaDigits(exam.duration / 60)} মিনিট"
                    )
                    ExamStatPill(
                        icon = Icons.Default.Grade,
                        label = "মোট নম্বর",
                        value = "${BanglaUtils.toBanglaDigits(exam.totalMarks)}"
                    )
                    ExamStatPill(
                        icon = Icons.Default.CheckCircle,
                        label = "পাস নম্বর",
                        value = "${BanglaUtils.toBanglaDigits(exam.passingMarks)}"
                    )
                    ExamStatPill(
                        icon = Icons.Default.Stars,
                        label = "পুরস্কার",
                        value = "+${BanglaUtils.toBanglaDigits(exam.rewardPoints)} প."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "পরীক্ষায় অংশগ্রহণ করুন",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ExamStatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RoyalPurple,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondarySlate
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveExamScreen(
    exam: Exam,
    viewModel: AppViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onFinish()
    }

    val questions by viewModel.activeExamQuestions.collectAsState()
    val currentIndex by viewModel.examCurrentIndex.collectAsState()
    val selectedAnswers by viewModel.examSelectedAnswers.collectAsState()
    val timeRemaining by viewModel.examTimeRemaining.collectAsState()
    val isFinished by viewModel.isExamFinished.collectAsState()
    val finalScore by viewModel.examFinalScore.collectAsState()
    val isPassed by viewModel.examPassed.collectAsState()

    val currentQ = questions.getOrNull(currentIndex)
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = exam.title,
                        fontSize = 16.sp,
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (timeRemaining <= 60) ErrorRed.copy(alpha = 0.15f)
                                else RoyalPurple.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = if (timeRemaining <= 60) ErrorRed else RoyalPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${BanglaUtils.toBanglaDigits(minutes)}:${if (seconds < 10) "০" else ""}${BanglaUtils.toBanglaDigits(seconds)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timeRemaining <= 60) ErrorRed else RoyalPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
                // Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "প্রশ্ন ${BanglaUtils.toBanglaDigits(currentIndex + 1)} / ${BanglaUtils.toBanglaDigits(questions.size)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondarySlate
                    )
                    Text(
                        text = "উত্তর দেওয়া হয়েছে: ${BanglaUtils.toBanglaDigits(selectedAnswers.size)}/${BanglaUtils.toBanglaDigits(questions.size)}",
                        fontSize = 12.sp,
                        color = RoyalPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = currentQ.question,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(18.dp),
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options
                val prefixes = listOf("ক", "খ", "গ", "ঘ")
                currentQ.options.forEachIndexed { optIdx, optText ->
                    val isSelected = selectedAnswers[currentIndex] == optIdx
                    val border = if (isSelected) RoyalPurple else CardLightGray
                    val bg = if (isSelected) RoyalPurple.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, border, RoundedCornerShape(14.dp))
                            .clickable { viewModel.selectExamAnswer(currentIndex, optIdx) },
                        colors = CardDefaults.cardColors(containerColor = bg),
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
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(border.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prefixes.getOrElse(optIdx) { "${optIdx + 1}" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) RoyalPurple else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = optText,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Nav Buttons (Previous, Next, Submit)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.goToPreviousExamQuestion() },
                        enabled = currentIndex > 0,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("পূর্ববর্তী")
                    }

                    if (currentIndex < questions.size - 1) {
                        Button(
                            onClick = { viewModel.goToNextExamQuestion() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("পরবর্তী")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.submitExam(exam) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("জমা দিন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Exam Results Modal
        if (isFinished) {
            AlertDialog(
                onDismissRequest = onFinish,
                icon = {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isPassed) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isPassed) SuccessGreen else ErrorRed,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = if (isPassed) "অভিনন্দন! আপনি উত্তীর্ণ হয়েছেন!" else "দুঃখিত, আপনি পাস করতে পারেননি",
                        fontSize = 18.sp,
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
                            text = "প্রাপ্ত নম্বর: ${BanglaUtils.toBanglaDigits(finalScore)} / ${BanglaUtils.toBanglaDigits(exam.totalMarks)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "পাস নম্বর ছিল: ${BanglaUtils.toBanglaDigits(exam.passingMarks)}",
                            fontSize = 13.sp,
                            color = TextSecondarySlate
                        )
                        if (isPassed) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SuccessGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "পুরস্কার: +${BanglaUtils.toBanglaDigits(exam.rewardPoints)} পয়েন্ট যোগ হয়েছে!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
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
