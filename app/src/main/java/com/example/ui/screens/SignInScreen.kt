package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.auth.AuthManager
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyLight
import com.example.ui.theme.RoyalPurple

@Composable
fun SignInScreen(
    authManager: AuthManager,
    onSignInSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        DeepNavy,
                        NavyLight,
                        RoyalPurple.copy(alpha = 0.8f)
                    )
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_launcher_icon_1793735739046),
                    contentDescription = "Bangla Quiz App Logo",
                    modifier = Modifier
                        .size(94.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "বাংলা কুইজ ও আয়",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Bangla Quiz & Earn",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "জ্ঞান অর্জন করুন এবং বিকাশ, নগদ ও রকেটের মাধ্যমে পুরস্কার জিতে নিন!",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Features Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FeatureItem(
                        icon = Icons.Default.School,
                        title = "১২টি আকর্ষণীয় কুইজ বিষয়",
                        subtitle = "সাধারণ জ্ঞান, বাংলাদেশ, বিজ্ঞান, ইসলামিক ও গণিত"
                    )
                    FeatureItem(
                        icon = Icons.Default.Stars,
                        title = "১০০ লেভেল প্রগ্রেশন সিস্টেম",
                        subtitle = "ধাপে ধাপে লেভেল আনলক করে জিতে নিন পয়েন্ট"
                    )
                    FeatureItem(
                        icon = Icons.Default.EmojiEvents,
                        title = "দৈনিক ও সাপ্তাহিক মেধা পরীক্ষা",
                        subtitle = "নির্দিষ্ট সময়ের মধ্যে পরীক্ষা দিয়ে পান মেগা রিওয়ার্ড"
                    )
                    FeatureItem(
                        icon = Icons.Default.CardGiftcard,
                        title = "১০০০ পয়েন্ট = ৫০ ৳ বিকাশ/নগদ",
                        subtitle = "বিকাশ, নগদ ও রকেটে দ্রুত উত্তোলন"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error display
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Google Sign-In Button
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    val activity = context as? Activity
                    if (activity != null) {
                        authManager.signInWithGoogle(
                            activity = activity,
                            scope = coroutineScope,
                            onSuccess = {
                                isLoading = false
                                onSignInSuccess()
                            },
                            onError = { err ->
                                isLoading = false
                                errorMessage = err
                            },
                            onCancelled = {
                                isLoading = false
                            }
                        )
                    } else {
                        isLoading = false
                        errorMessage = "প্রয়োজনীয় অ্যাক্টিভিটি কনটেক্সট পাওয়া যায়নি"
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = DeepNavy
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DeepNavy,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Google Icon",
                            tint = RoyalPurple,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "গুগল দিয়ে সাইন ইন করুন",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "সাইন ইন করার মাধ্যমে আপনি আমাদের শর্তাবলী ও নীতি মেনে নিচ্ছেন",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(GoldAccent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}
