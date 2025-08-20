package com.elm.recipebox.presentation.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elm.recipebox.R
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

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        OnboardingPageUI(
            page = pages[page],
            pageIndex = page,
            totalPages = pages.size,
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

                if (pageIndex == totalPages - 1) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable { onIndicatorClick() },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Go",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable { onIndicatorClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(60.dp)) {
                            drawArc(
                                color = Color.White.copy(alpha = 0.3f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 12f)
                            )

                            val progress = (pageIndex + 1) / totalPages.toFloat()
                            val sweep = 360f * progress

                            drawArc(
                                color = Color.White,
                                startAngle = -90f,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = 12f)
                            )
                        }
                    }
                }
            }
        }
    }
}
