package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletTransaction
import com.example.data.model.WithdrawalRequest
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.userWallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()

    var showWithdrawDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Transactions, 1: Withdrawals

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "পয়েন্ট ওয়ালেট ও উত্তোলন",
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
            // Wallet Card Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(DeepNavy, NavyLight)
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
                                    text = "উত্তোলনযোগ্য ব্যালেন্স",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GoldAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "১০০০ পয়েন্ট = ৫০ ৳",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
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
                                    text = BanglaUtils.toBanglaDigits(wallet.availablePoints),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldAccent
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "পয়েন্ট",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(bottom = 5.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "মূল্য: ${BanglaUtils.formatBDT(BanglaUtils.pointsToBDT(wallet.availablePoints))}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stats breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "পেন্ডিং উত্তোলন",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${BanglaUtils.toBanglaDigits(wallet.reservedPoints)} পয়েন্ট (${BanglaUtils.formatBDT(BanglaUtils.pointsToBDT(wallet.reservedPoints))})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = WarningOrange
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "সর্বমোট আয়",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${BanglaUtils.toBanglaDigits(wallet.lifetimeEarnedPoints)} পয়েন্ট",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SuccessGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { showWithdrawDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "টাকা উত্তোলন করুন (বিকাশ/নগদ/রকেট)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                            }
                        }
                    }
                }
            }

            // Tab Switcher
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = DeepNavy
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "লেনদেনের ইতিহাস (${BanglaUtils.toBanglaDigits(transactions.size)})",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "উত্তোলন হিস্ট্রি (${BanglaUtils.toBanglaDigits(withdrawals.size)})",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            if (selectedTab == 0) {
                if (transactions.isEmpty()) {
                    item {
                        EmptyStateCard(message = "এখনও কোনো লেনদেনের তথ্য নেই। কুইজ খেলে পয়েন্ট অর্জন করুন!")
                    }
                } else {
                    items(transactions) { tx ->
                        TransactionItemCard(tx = tx)
                    }
                }
            } else {
                if (withdrawals.isEmpty()) {
                    item {
                        EmptyStateCard(message = "আপনার কোনো উত্তোলনের আবেদন পাওয়া যায়নি।")
                    }
                } else {
                    items(withdrawals) { wd ->
                        WithdrawalItemCard(wd = wd)
                    }
                }
            }
        }

        // Withdrawal Request Dialog
        if (showWithdrawDialog) {
            WithdrawalModalDialog(
                availablePoints = wallet.availablePoints,
                onSubmit = { method, account, points ->
                    viewModel.submitWithdrawal(method, account, points) {
                        showWithdrawDialog = false
                    }
                },
                onDismiss = { showWithdrawDialog = false }
            )
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = TextSecondarySlate,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = TextSecondarySlate,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TransactionItemCard(tx: WalletTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (icon, tint, isDebit) = when (tx.type) {
                "withdrawal_reserve" -> Triple(Icons.Default.HourglassTop, WarningOrange, true)
                "withdrawal_refund" -> Triple(Icons.Default.Replay, RoyalPurple, false)
                "withdrawal_paid" -> Triple(Icons.Default.CheckCircle, SuccessGreen, true)
                else -> Triple(Icons.Default.Stars, SuccessGreen, false)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when (tx.type) {
                        "quiz_reward" -> "কুইজ রিওয়ার্ড"
                        "level_reward" -> "লেভেল রিওয়ার্ড"
                        "exam_reward" -> "পরীক্ষা রিওয়ার্ড"
                        "daily_checkin" -> "দৈনিক হাজিরা"
                        "ad_reward" -> "বিজ্ঞাপন বোনাস"
                        "withdrawal_reserve" -> "উত্তোলন রিজার্ভ"
                        "withdrawal_refund" -> "উত্তোলন ফেরত"
                        "withdrawal_paid" -> "পেমেন্ট সম্পন্ন"
                        else -> "অন্যান্য"
                    },
                    fontSize = 11.sp,
                    color = TextSecondarySlate
                )
            }

            Text(
                text = "${if (isDebit) "-" else "+"}${BanglaUtils.toBanglaDigits(tx.points)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDebit) ErrorRed else SuccessGreen
            )
        }
    }
}

@Composable
private fun WithdrawalItemCard(wd: WithdrawalRequest) {
    val (statusLabel, statusColor, statusBg) = when (wd.status) {
        "pending" -> Triple("পেন্ডিং", WarningOrange, WarningOrange.copy(alpha = 0.12f))
        "approved" -> Triple("অনুমোদিত", RoyalPurple, RoyalPurple.copy(alpha = 0.12f))
        "paid" -> Triple("পরিশোধিত", SuccessGreen, SuccessGreen.copy(alpha = 0.12f))
        "rejected" -> Triple("বাতিল", ErrorRed, ErrorRed.copy(alpha = 0.12f))
        else -> Triple(wd.status, TextSecondarySlate, CardLightGray)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (wd.paymentMethod) {
                                    "bKash" -> BkashPink
                                    "Nagad" -> NagadOrange
                                    else -> RocketPurple
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${wd.paymentMethod} (${wd.accountNumber})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${BanglaUtils.toBanglaDigits(wd.requestedPoints)} পয়েন্ট",
                    fontSize = 13.sp,
                    color = TextSecondarySlate
                )
                Text(
                    text = BanglaUtils.formatBDT(wd.amountBDT),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy
                )
            }

            if (wd.adminNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "নোট: ${wd.adminNote}",
                    fontSize = 11.sp,
                    color = TextSecondarySlate
                )
            }
        }
    }
}

@Composable
fun WithdrawalModalDialog(
    availablePoints: Long,
    onSubmit: (method: String, account: String, points: Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("bKash") }
    var accountNumber by remember { mutableStateOf("01617866617") }
    var pointsText by remember { mutableStateOf("1000") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val pointsValue = pointsText.toLongOrNull() ?: 0L
    val bdtEquivalent = pointsValue / 20

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "টাকা উত্তোলন করুন",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "পেমেন্ট মাধ্যম বেছে নিন:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("bKash" to BkashPink, "Nagad" to NagadOrange, "Rocket" to RocketPurple).forEach { (m, col) ->
                        val isSel = selectedMethod == m
                        OutlinedButton(
                            onClick = { selectedMethod = m },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) col.copy(alpha = 0.15f) else Color.Transparent
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(if (isSel) col else CardLightGray, if (isSel) col else CardLightGray))
                            )
                        ) {
                            Text(
                                text = when (m) {
                                    "bKash" -> "বিকাশ"
                                    "Nagad" -> "নগদ"
                                    else -> "রকেট"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) col else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("একাউন্ট নম্বর (১১ ডিজিট)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { pointsText = it },
                    label = { Text("উত্তোলন পয়েন্ট (সর্বনিম্ন ১,০০০)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Equivalent Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "আপনি পাবেন:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = BanglaUtils.formatBDT(bdtEquivalent),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    }
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        fontSize = 12.sp,
                        color = ErrorRed
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (accountNumber.length < 11) {
                        errorMsg = "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন"
                        return@Button
                    }
                    if (pointsValue < 1000) {
                        errorMsg = "সর্বনিম্ন উত্তোলন ১,০০০ পয়েন্ট (৫০ ৳)"
                        return@Button
                    }
                    if (pointsValue > availablePoints) {
                        errorMsg = "আপনার ওয়ালেটে পর্যাপ্ত পয়েন্ট নেই"
                        return@Button
                    }
                    errorMsg = null
                    onSubmit(selectedMethod, accountNumber, pointsValue)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("অনুরোধ পাঠান")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
