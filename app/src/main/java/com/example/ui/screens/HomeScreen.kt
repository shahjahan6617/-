package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.BanglaUtils

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onOpenDailyBonus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val userWallet by viewModel.userWallet.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val photoUrl = userProfile?.photoUrl ?: ""
                    if (photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(NavySlate.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = DeepNavy,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProfile?.name?.ifBlank { "ব্যবহারকারী" } ?: "ব্যবহারকারী",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RoyalPurple.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "লেভেল ${BanglaUtils.toBanglaDigits(userProfile?.currentLevel ?: 1)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = RoyalPurple
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "শিক্ষানবিশ",
                                fontSize = 12.sp,
                                color = TextSecondarySlate
                            )
                        }
                    }

                    // Notification / Admin indicator
                    if (userProfile?.isAdmin == true) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.AdminPanel) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Panel",
                                tint = DeepNavy
                            )
                        }
                    }
                }
            }
        }

        // Points Wallet Card (Gradient Hero)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(DeepNavy, NavyLight, RoyalPurple)
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
                            Text(
                                text = "মোট ব্যালেন্স",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "১০০০ পয়েন্ট = ৫০ ৳",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GoldAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = BanglaUtils.toBanglaDigits(userWallet.availablePoints),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "পয়েন্ট",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "≈ ${BanglaUtils.formatBDT(BanglaUtils.pointsToBDT(userWallet.availablePoints))}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.navigateTo(Screen.Wallet) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "উত্তোলন করুন",
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onOpenDailyBonus,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(listOf(Color.White, GoldLight))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "দৈনিক বোনাস",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Category & Games Section Title
        item {
            Text(
                text = "প্রধান সেকশনসমূহ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Grid of 6 Shortcuts
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureShortcutCard(
                        title = "১২টি কুইজ বিভাগ",
                        subtitle = "বিষয়ভিত্তিক প্রস্তুতি",
                        icon = Icons.Default.School,
                        iconColor = RoyalPurple,
                        badge = "জনপ্রিয়",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.QuizCategories) }
                    )
                    FeatureShortcutCard(
                        title = "১০০ লেভেল খেলা",
                        subtitle = "চ্যালেঞ্জ ও পয়েন্ট",
                        icon = Icons.Default.Stars,
                        iconColor = GoldDark,
                        badge = "১০০ স্তর",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.LevelSelection) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureShortcutCard(
                        title = "মেধা পরীক্ষা",
                        subtitle = "দৈনিক ও সাপ্তাহিক",
                        icon = Icons.Default.Timer,
                        iconColor = SuccessGreen,
                        badge = "লাইভ",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Exams) }
                    )
                    FeatureShortcutCard(
                        title = "বোনাস বিজ্ঞাপন",
                        subtitle = "দেখে পয়েন্ট আয়",
                        icon = Icons.Default.OndemandVideo,
                        iconColor = WarningOrange,
                        badge = "বোনাস",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.AdsHub) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureShortcutCard(
                        title = "লিডারবোর্ড",
                        subtitle = "শীর্ষ সেরাদের তালিকা",
                        icon = Icons.Default.EmojiEvents,
                        iconColor = GoldAccent,
                        badge = "র্যাঙ্কিং",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Leaderboard) }
                    )
                    FeatureShortcutCard(
                        title = "ওয়ালেট ও পেমেন্ট",
                        subtitle = "বিকাশ, নগদ ও রকেট",
                        icon = Icons.Default.Payments,
                        iconColor = BkashPink,
                        badge = "উত্তোলন",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Wallet) }
                    )
                }
            }
        }

        // User Achievement Stats
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "আপনার অগ্রগতি ও পরিসংখ্যান",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(
                            label = "সম্পন্ন কুইজ",
                            value = BanglaUtils.toBanglaDigits(userProfile?.totalQuizCompleted ?: 0),
                            icon = Icons.Default.CheckCircle,
                            tint = SuccessGreen
                        )
                        Divider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = CardLightGray
                        )
                        StatItem(
                            label = "সম্পন্ন পরীক্ষা",
                            value = BanglaUtils.toBanglaDigits(userProfile?.totalExamCompleted ?: 0),
                            icon = Icons.Default.AssignmentTurnedIn,
                            tint = RoyalPurple
                        )
                        Divider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = CardLightGray
                        )
                        StatItem(
                            label = "পেন্ডিং পয়েন্ট",
                            value = BanglaUtils.toBanglaDigits(userWallet.reservedPoints),
                            icon = Icons.Default.HourglassTop,
                            tint = WarningOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    badge: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondarySlate
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondarySlate
        )
    }
}
