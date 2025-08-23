package com.elm.recipebox.presentation.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elm.recipebox.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            background = Color(0xFF305FDE),
            images = listOf(R.drawable.dish1, R.drawable.dish2, R.drawable.dish3),
            text = "Your personal guide to be a chef"
        ),
        OnboardingPage(
            background = Color(0xFFFF7043),
            images = listOf(R.drawable.dish4, R.drawable.dish5, R.drawable.dish6),
            text = "Share the Love,\nShare the Recipe"
        ),
        OnboardingPage(
            background = Color(0xFFFBC02D),
            images = listOf(R.drawable.dish7, R.drawable.dish8, R.drawable.dish9),
            text = "Foodify Your Global Kitchen"
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
    val scope = rememberCoroutineScope()

    // Animated progress for the circular indicator (0f..1f)
    val pageProgress = remember { Animatable(0f) }

    val isLastPage by derivedStateOf { pagerState.currentPage == pages.lastIndex }

    // Auto-advance with animated arc per page, but stop on last page
    LaunchedEffect(pagerState.currentPage) {
        // Reset progress for the new page
        pageProgress.snapTo(0f)

        if (!isLastPage) {
            // Animate progress over N milliseconds, then move to next page if still on same page
            val myPage = pagerState.currentPage
            pageProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing)
            )
            // If user didn't manually swipe away during the animation, advance
            if (pagerState.currentPage == myPage) {
                scope.launch {
                    pagerState.animateScrollToPage(myPage + 1)
                }
            }
        } else {
            // On the last page, keep the ring full
            pageProgress.snapTo(1f)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        OnboardingPageUI(
            page = pages[page],
            pageIndex = page,
            totalPages = pages.size,
            progress = pageProgress.value,
            onIndicatorClick = {
                if (page == pages.lastIndex) {
                    onFinish()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(page + 1)
                    }
                }
            }
        )
    }
}

data class OnboardingPage(
    val background: Color,
    val images: List<Int>,
    val text: String
)

@Composable
fun OnboardingPageUI(
    page: OnboardingPage,
    pageIndex: Int,
    totalPages: Int,
    progress: Float, // 0f..1f animated
    onIndicatorClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(page.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Images block
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                page.images.forEach { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
            }

            // Text + indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = page.text,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.Black, shape = MaterialTheme.shapes.medium)
                        .padding(35.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                val isLast = pageIndex == totalPages - 1

                // Circular progress button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .clickable { onIndicatorClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLast) {
                        Text(
                            text = "Go",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        // Animated circular timer/progress
                        Canvas(modifier = Modifier.size(60.dp)) {
                            val strokeWidth = 12f
                            // Background ring
                            drawArc(
                                color = Color.White.copy(alpha = 0.25f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            // Foreground ring (progress)
                            val sweep = 360f * progress.coerceIn(0f, 1f)
                            drawArc(
                                color = Color.White,
                                startAngle = -90f,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }
        }
    }
}