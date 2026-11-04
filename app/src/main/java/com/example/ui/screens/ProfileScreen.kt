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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ব্যবহারকারী প্রোফাইল",
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
            // User Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
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
                                    .size(72.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(DeepNavy.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userProfile?.name?.ifBlank { "ব্যবহারকারী" } ?: "ব্যবহারকারী",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = userProfile?.email ?: "",
                            fontSize = 13.sp,
                            color = TextSecondarySlate
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SuccessGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (userProfile?.accountStatus == "active") "সক্রিয় অ্যাকাউন্ট" else "স্থগিত",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }

                            if (userProfile?.isAdmin == true) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(RoyalPurple.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "অ্যাডমিন এক্সেস",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalPurple
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Admin Panel Shortcut (Always accessible if user is admin, or can be unlocked)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.AdminPanel) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavy)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "অ্যাডমিন কন্ট্রোল প্যানেল",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "ব্যবহারকারী, কুইজ, উত্তোলন ও বিজ্ঞাপন ব্যবস্থাপনা",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Settings & Legal List
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.HeadsetMic,
                            title = "সাহায্য ও সাপোর্ট",
                            subtitle = "হেল্পলাইন: 01617866617",
                            onClick = { showHelpDialog = true }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = CardLightGray)
                        ProfileMenuItem(
                            icon = Icons.Default.Policy,
                            title = "গোপনীয়তা নীতি (Privacy Policy)",
                            subtitle = "তথ্য সুরক্ষার নিয়মাবলী",
                            onClick = { showPrivacyDialog = true }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = CardLightGray)
                        ProfileMenuItem(
                            icon = Icons.Default.Description,
                            title = "ব্যবহারের শর্তাবলী (Terms & Conditions)",
                            subtitle = "পয়েন্ট ও পেমেন্ট নীতিমালা",
                            onClick = { showTermsDialog = true }
                        )
                    }
                }
            }

            // Sign Out Button
            item {
                OutlinedButton(
                    onClick = {
                        viewModel.authManager.signOut(coroutineScope) {
                            onSignOut()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "অ্যাকাউন্ট থেকে লগআউট করুন",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                }
            }
        }

        // Help & Support Dialog
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = { Text("সাহায্য ও সাপোর্ট", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("যেকোনো জিজ্ঞাসা বা পেমেন্ট সংক্রান্ত সহায়তার জন্য আমাদের সাথে যোগাযোগ করুন:")
                        Text("• হেল্পলাইন: 01617866617", fontWeight = FontWeight.Bold, color = DeepNavy)
                        Text("• অফিসিয়াল বিকাশ/নগদ মার্চেন্ট: 01617866617")
                        Text("• ইমেইল: sarbanunesa66@gmail.com")
                        Text("• কার্যসময়: প্রতিদিন সকাল ৯টা থেকে রাত ১০টা")
                    }
                },
                confirmButton = {
                    Button(onClick = { showHelpDialog = false }) {
                        Text("ঠিক আছে")
                    }
                }
            )
        }

        // Terms Dialog
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = { Text("ব্যবহারের শর্তাবলী", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("১. পয়েন্ট রূপান্তর হার: ১,০০০ পয়েন্ট = ৫০ টাকা (বিডিটি)।")
                        Text("২. সর্বনিম্ন উত্তোলন সীমা: ১,০০০ পয়েন্ট।")
                        Text("৩. বিকাশ, নগদ ও রকেটের মাধ্যমে ২৪ থেকে ৭২ ঘণ্টার মধ্যে পেমেন্ট সম্পন্ন করা হয়।")
                        Text("৪. কোনো প্রকার অসদুপায়, অটো-ক্লিকার বা স্প্যামিং করলে অ্যাকাউন্ট চিরতরে স্থগিত করা হবে।")
                    }
                },
                confirmButton = {
                    Button(onClick = { showTermsDialog = false }) {
                        Text("সম্মত আছি")
                    }
                }
            )
        }

        // Privacy Dialog
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = { Text("গোপনীয়তা নীতি", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("আমরা গুগল সাইন-ইন এর মাধ্যমে শুধুমাত্র আপনার নাম, ইমেইল ও প্রোফাইল ছবি সংগ্রহ করি যা অ্যাপের প্রোফাইল ও লিডারবোর্ডের জন্য ব্যবহৃত হয়।")
                        Text("আপনার পেমেন্ট নম্বর বা ব্যক্তিগত তথ্য তৃতীয় কোনো পক্ষের কাছে বিক্রি বা শেয়ার করা হয় না।")
                    }
                },
                confirmButton = {
                    Button(onClick = { showPrivacyDialog = false }) {
                        Text("বন্ধ করুন")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DeepNavy,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondarySlate
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondarySlate,
            modifier = Modifier.size(20.dp)
        )
    }
}
