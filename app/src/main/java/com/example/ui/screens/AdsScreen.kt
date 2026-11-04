package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val monetagConfig by viewModel.adManager.monetagConfig.collectAsState()
    val adverticaConfig by viewModel.adManager.adverticaConfig.collectAsState()
    val watchedToday by viewModel.adManager.adsWatchedToday.collectAsState()
    val isWatchingAd by viewModel.isWatchingRewardedAd.collectAsState()
    val countdown by viewModel.adCountdown.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "বিজ্ঞাপন ও স্পন্সর বোনাস",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rewarded Video Ad Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(RoyalPurple, DeepNavy)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GoldAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "রিওয়ার্ডেড ভিডিও",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }
                                Text(
                                    text = "দৈনিক কোটা: ${BanglaUtils.toBanglaDigits(watchedToday)}/${BanglaUtils.toBanglaDigits(monetagConfig.dailyLimit)}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "বিজ্ঞাপন দেখে বোনাস পয়েন্ট অর্জন করুন",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "সম্পূর্ণ ১০ সেকেন্ডের স্পন্সর ভিডিও দেখে প্রতিটি ভিউতে অর্জন করুন +${BanglaUtils.toBanglaDigits(monetagConfig.rewardPoints)} রিওয়ার্ড পয়েন্ট।",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    viewModel.startWatchingRewardedAd {
                                        // Completed callback
                                    }
                                },
                                enabled = !isWatchingAd && watchedToday < monetagConfig.dailyLimit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (watchedToday >= monetagConfig.dailyLimit) "আজকের কোটা পূর্ণ"
                                    else "ভিডিও বিজ্ঞাপন দেখুন (+${BanglaUtils.toBanglaDigits(monetagConfig.rewardPoints)} পয়েন্ট)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                            }
                        }
                    }
                }
            }

            // Monetag Ad Network Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(RoyalPurple.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = RoyalPurple,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Monetag অ্যাড নেটওয়ার্ক",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "স্ট্যাটাস: সক্রিয় (Verified Publisher)",
                                    fontSize = 11.sp,
                                    color = SuccessGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "প্লেসমেন্ট: In-Page Push & Rewarded Interstitial\nপাবলিশার আইডি: ${monetagConfig.publisherId}\nজোন আইডি: ${monetagConfig.zoneId}",
                            fontSize = 12.sp,
                            color = TextSecondarySlate,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Advertica Ad Network Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DeepNavy.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FeaturedVideo,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Advertica অ্যাড নেটওয়ার্ক",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "স্ট্যাটাস: সক্রিয় (Direct Fallback)",
                                    fontSize = 11.sp,
                                    color = SuccessGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "প্লেসমেন্ট: Native Display & Native Video Ads\nপার্টনার কোড: ${adverticaConfig.publisherId}\nজোন: ${adverticaConfig.zoneId}",
                            fontSize = 12.sp,
                            color = TextSecondarySlate,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Notice
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardLightGray)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = RoyalPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "বিজ্ঞাপন সম্পূর্ণ দেখা শেষ না করে বন্ধ করলে পয়েন্ট যুক্ত হবে না। ভুয়া ক্লিক বা স্কিপিং কঠোরভাবে নিষিদ্ধ।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Rewarded Video Player Dialog
        if (isWatchingAd) {
            AlertDialog(
                onDismissRequest = { /* Block dismiss until completed */ },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(RoyalPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { (10 - countdown) / 10f },
                            color = GoldAccent,
                            strokeWidth = 4.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = "${BanglaUtils.toBanglaDigits(countdown)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple
                        )
                    }
                },
                title = {
                    Text(
                        text = "স্পন্সর ভিডিও প্রদর্শিত হচ্ছে...",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(DeepNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Monetag Verified Sponsor Video",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "পুরস্কার পেতে অপেক্ষা করুন: ${BanglaUtils.toBanglaDigits(countdown)} সেকেন্ড",
                                    color = GoldLight,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.dismissAdPlayer() }
                    ) {
                        Text("বিজ্ঞাপন বাতিল (পয়েন্ট পাবেন না)", color = ErrorRed)
                    }
                }
            )
        }
    }
}
