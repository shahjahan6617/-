package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.BanglaUtils

@Composable
fun DailyCheckInDialog(
    onClaim: (day: Int, points: Long) -> Unit,
    onDismiss: () -> Unit
) {
    val dailyRewards = listOf(
        1 to 20L,
        2 to 30L,
        3 to 40L,
        4 to 50L,
        5 to 60L,
        6 to 80L,
        7 to 100L
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = GoldDark,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "দৈনিক হাজিরা বোনাস",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "প্রতিদিন লগইন করে রিওয়ার্ড পয়েন্ট সংগ্রহ করুন! টানা ৭ দিন হাজির হলে পাবেন মেগা বোনাস।",
                    fontSize = 13.sp,
                    color = TextSecondarySlate,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 7 Days Grid/Row
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 0..3) {
                            val (day, pts) = dailyRewards[i]
                            DayPill(day = day, points = pts, isToday = day == 1, modifier = Modifier.weight(1f))
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 4..6) {
                            val (day, pts) = dailyRewards[i]
                            DayPill(day = day, points = pts, isToday = false, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onClaim(1, 20L)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "আজকের ২০ পয়েন্ট গ্রহণ করুন",
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন")
            }
        }
    )
}

@Composable
private fun DayPill(
    day: Int,
    points: Long,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) RoyalPurple.copy(alpha = 0.15f) else CardLightGray
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "দিন ${BanglaUtils.toBanglaDigits(day)}",
                fontSize = 11.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                color = if (isToday) RoyalPurple else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "+${BanglaUtils.toBanglaDigits(points)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isToday) GoldDark else SuccessGreen
            )
        }
    }
}
