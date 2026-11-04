package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.UserProfile
import com.example.data.model.WithdrawalRequest
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.util.BanglaUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Withdrawals, 2: Users, 3: Ads, 4: Settings

    val allUsers by viewModel.allUsers.collectAsState()
    val allWithdrawals by viewModel.allWithdrawals.collectAsState()
    val monetagConfig by viewModel.adManager.monetagConfig.collectAsState()
    val adverticaConfig by viewModel.adManager.adverticaConfig.collectAsState()

    var trxIdInput by remember { mutableStateOf("") }
    var selectedWithdrawalForPayout by remember { mutableStateOf<WithdrawalRequest?>(null) }
    var rejectReasonInput by remember { mutableStateOf("") }
    var selectedWithdrawalForReject by remember { mutableStateOf<WithdrawalRequest?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "অ্যাডমিন কন্ট্রোল প্যানেল",
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
                    containerColor = DeepNavy,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DeepNavy,
                edgePadding = 12.dp
            ) {
                listOf("ওভারভিউ", "উত্তোলন (${allWithdrawals.count { it.status == "pending" }})", "ব্যবহারকারী (${allUsers.size})", "বিজ্ঞাপন", "অ্যাপ সেটিংস").forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tab 0: Overview
                if (selectedTab == 0) {
                    val pendingCount = allWithdrawals.count { it.status == "pending" }
                    val paidCount = allWithdrawals.count { it.status == "paid" }
                    val totalPointsPaid = allWithdrawals.filter { it.status == "paid" }.sumOf { it.requestedPoints }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "সিস্টেম সারসংক্ষেপ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    AdminMetricBox("মোট ব্যবহারকারী", BanglaUtils.toBanglaDigits(allUsers.size), Icons.Default.People, DeepNavy)
                                    AdminMetricBox("পেন্ডিং উত্তোলন", BanglaUtils.toBanglaDigits(pendingCount), Icons.Default.HourglassTop, WarningOrange)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    AdminMetricBox("পরিশোধিত উত্তোলন", BanglaUtils.toBanglaDigits(paidCount), Icons.Default.CheckCircle, SuccessGreen)
                                    AdminMetricBox("পরিশোধিত পয়েন্ট", "${BanglaUtils.toBanglaDigits(totalPointsPaid)} প.", Icons.Default.Payments, RoyalPurple)
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "অ্যাডমিনিস্ট্রেটর পরিচিতি",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ইমেইল: sarbanunesa66@gmail.com (Super Admin)\nঅফিসিয়াল মার্চেন্ট নম্বর: 01617866617 (বিকাশ / নগদ)",
                                    fontSize = 12.sp,
                                    color = TextSecondarySlate,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                // Tab 1: Withdrawals
                if (selectedTab == 1) {
                    if (allWithdrawals.isEmpty()) {
                        item {
                            Text(
                                text = "কোনো উত্তোলনের আবেদন পাওয়া যায়নি।",
                                fontSize = 14.sp,
                                color = TextSecondarySlate,
                                modifier = Modifier.padding(top = 20.dp)
                            )
                        }
                    } else {
                        items(allWithdrawals) { wd ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${wd.paymentMethod}: ${wd.accountNumber}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when (wd.status) {
                                                        "pending" -> WarningOrange.copy(alpha = 0.15f)
                                                        "paid" -> SuccessGreen.copy(alpha = 0.15f)
                                                        "approved" -> RoyalPurple.copy(alpha = 0.15f)
                                                        else -> ErrorRed.copy(alpha = 0.15f)
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = wd.status.uppercase(),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (wd.status) {
                                                    "pending" -> WarningOrange
                                                    "paid" -> SuccessGreen
                                                    "approved" -> RoyalPurple
                                                    else -> ErrorRed
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "${BanglaUtils.toBanglaDigits(wd.requestedPoints)} পয়েন্ট (${BanglaUtils.formatBDT(wd.amountBDT)})",
                                        fontSize = 14.sp,
                                        color = DeepNavy,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "ইউজার আইডি: ${wd.uid}",
                                        fontSize = 11.sp,
                                        color = TextSecondarySlate
                                    )

                                    if (wd.adminNote.isNotBlank()) {
                                        Text(
                                            text = "নোট: ${wd.adminNote}",
                                            fontSize = 11.sp,
                                            color = RoyalPurple
                                        )
                                    }

                                    if (wd.status == "pending" || wd.status == "approved") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (wd.status == "pending") {
                                                Button(
                                                    onClick = { viewModel.adminApproveWithdrawal(wd.id) },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("অনুমোদন", fontSize = 12.sp)
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    selectedWithdrawalForPayout = wd
                                                    trxIdInput = "TRX${(100000..999999).random()}"
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("টাকা পাঠানো সম্পন্ন", fontSize = 12.sp)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    selectedWithdrawalForReject = wd
                                                    rejectReasonInput = "ভুল একাউন্ট নম্বর"
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("বাতিল ও রিফান্ড", fontSize = 11.sp, color = ErrorRed)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 2: Users
                if (selectedTab == 2) {
                    items(allUsers) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.name.ifBlank { "ব্যবহারকারী" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = user.email,
                                        fontSize = 12.sp,
                                        color = TextSecondarySlate
                                    )
                                    Text(
                                        text = "লেভেল: ${BanglaUtils.toBanglaDigits(user.currentLevel)} | কুইজ: ${BanglaUtils.toBanglaDigits(user.totalQuizCompleted)}",
                                        fontSize = 11.sp,
                                        color = RoyalPurple
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.adminToggleUserStatus(user.uid, user.accountStatus) }
                                ) {
                                    Text(
                                        text = if (user.accountStatus == "active") "সক্রিয়" else "স্থগিত",
                                        color = if (user.accountStatus == "active") SuccessGreen else ErrorRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab 3: Ads
                if (selectedTab == 3) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Monetag কনফিগারেশন",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("পাবলিশার আইডি: ${monetagConfig.publisherId}")
                                Text("জোন আইডি: ${monetagConfig.zoneId}")
                                Text("প্রতি ভিডিও পয়েন্ট: ${BanglaUtils.toBanglaDigits(monetagConfig.rewardPoints)}")
                                Text("দৈনিক সীমা: ${BanglaUtils.toBanglaDigits(monetagConfig.dailyLimit)}টি")
                                Text("স্ট্যাটাস: সক্রিয় (Server Verified)")
                            }
                        }
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Advertica কনফিগারেশন",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("পার্টনার আইডি: ${adverticaConfig.publisherId}")
                                Text("জোন আইডি: ${adverticaConfig.zoneId}")
                                Text("ফরম্যাট: Rewarded Direct Link")
                                Text("স্ট্যাটাস: সক্রিয় (Fallback Direct)")
                            }
                        }
                    }
                }

                // Tab 4: Settings
                if (selectedTab == 4) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "গ্লোবাল অ্যাপ নিয়মাবলী",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("• পয়েন্ট রূপান্তর রেট: ১,০০০ পয়েন্ট = ৫০ ৳ (২০ পয়েন্ট = ১ ৳)")
                                Text("• সর্বনিম্ন উত্তোলন: ১,০০০ পয়েন্ট (৫০ ৳)")
                                Text("• সাপোর্ট মার্চেন্ট নম্বর: 01617866617")
                                Text("• সাপোর্ট ইমেইল: sarbanunesa66@gmail.com")
                                Text("• অ্যাপ ভার্সন: ১.০.০")
                            }
                        }
                    }
                }
            }
        }

        // Payout Confirmation Dialog
        selectedWithdrawalForPayout?.let { wd ->
            AlertDialog(
                onDismissRequest = { selectedWithdrawalForPayout = null },
                title = { Text("পেমেন্ট নিশ্চিতকরণ", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${wd.paymentMethod} নম্বর: ${wd.accountNumber}")
                        Text("প্রদেয় টাকা: ${BanglaUtils.formatBDT(wd.amountBDT)} (${BanglaUtils.toBanglaDigits(wd.requestedPoints)} পয়েন্ট)")
                        OutlinedTextField(
                            value = trxIdInput,
                            onValueChange = { trxIdInput = it },
                            label = { Text("ট্রানজেকশন আইডি (TRX ID)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.adminMarkPaidWithdrawal(wd.id, trxIdInput)
                            selectedWithdrawalForPayout = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("পেইড মার্ক করুন")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedWithdrawalForPayout = null }) {
                        Text("বাতিল")
                    }
                }
            )
        }

        // Reject Confirmation Dialog
        selectedWithdrawalForReject?.let { wd ->
            AlertDialog(
                onDismissRequest = { selectedWithdrawalForReject = null },
                title = { Text("উত্তোলন বাতিল ও পয়েন্ট রিফান্ড", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("বাতিল করলে ব্যবহারকারীর ${BanglaUtils.toBanglaDigits(wd.requestedPoints)} পয়েন্ট তার মূল ওয়ালেটে ফেরত দেওয়া হবে।")
                        OutlinedTextField(
                            value = rejectReasonInput,
                            onValueChange = { rejectReasonInput = it },
                            label = { Text("বাতিলের কারণ") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.adminRejectWithdrawal(wd.id, rejectReasonInput)
                            selectedWithdrawalForReject = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) {
                        Text("নিশ্চিত বাতিল করুন")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedWithdrawalForReject = null }) {
                        Text("ফিরে যান")
                    }
                }
            )
        }
    }
}

@Composable
private fun AdminMetricBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextSecondarySlate
            )
        }
    }
}
