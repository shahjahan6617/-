package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.UserProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val leaderboard by viewModel.leaderboard.collectAsState()
    var selectedFilter by remember { mutableStateOf(0) } // 0: দৈনিক, 1: সাপ্তাহিক, 2: মাসিক, 3: সর্বকালের সেরা

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "সেরা মেধা লিডারবোর্ড",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Filter Pills
            TabRow(
                selectedTabIndex = selectedFilter,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DeepNavy
            ) {
                listOf("দৈনিক", "সাপ্তাহিক", "মাসিক", "সর্বকালের").forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedFilter == idx,
                        onClick = { selectedFilter = idx },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedFilter == idx) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (leaderboard.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "কুইজ ও পরীক্ষায় অংশগ্রহণ করে লিডারবোর্ডের শীর্ষে উঠুন!",
                                    fontSize = 14.sp,
                                    color = TextSecondarySlate
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(leaderboard) { index, user ->
                        LeaderboardRow(rank = index + 1, user = user)
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    user: UserProfile
) {
    val (badgeBg, badgeColor) = when (rank) {
        1 -> GoldAccent.copy(alpha = 0.25f) to GoldDark
        2 -> NavySlate.copy(alpha = 0.2f) to NavySlate
        3 -> WarningOrange.copy(alpha = 0.2f) to WarningOrange
        else -> CardLightGray to TextSecondarySlate
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (rank <= 3) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number or Trophy
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                if (rank == 1) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Rank 1",
                        tint = GoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = BanglaUtils.toBanglaDigits(rank),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Profile Picture
            val photoUrl = user.photoUrl
            if (photoUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(RoyalPurple.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = RoyalPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name.ifBlank { "শিক্ষানবিশ" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "লেভেল ${BanglaUtils.toBanglaDigits(user.currentLevel)}",
                    fontSize = 12.sp,
                    color = TextSecondarySlate
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${BanglaUtils.toBanglaDigits((user.currentLevel * 150) + (user.totalQuizCompleted * 20))} প.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
                Text(
                    text = "${BanglaUtils.toBanglaDigits(user.totalQuizCompleted)}টি কুইজ",
                    fontSize = 11.sp,
                    color = SuccessGreen
                )
            }
        }
    }
}
