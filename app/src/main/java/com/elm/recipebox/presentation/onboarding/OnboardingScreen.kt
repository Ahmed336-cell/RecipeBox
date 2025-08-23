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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
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

    // Animated progress for the circular indicator (0f..1f)
    val pageProgress = remember { Animatable(0f) }
    val isLastPage by derivedStateOf { pagerState.currentPage == pages.lastIndex }

    // Auto-advance with animated arc per page, but stop on last page
    LaunchedEffect(pagerState.currentPage) {
        pageProgress.snapTo(0f)
        if (!isLastPage) {
            val myPage = pagerState.currentPage
            pageProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing)
            )
            if (pagerState.currentPage == myPage) {
                scope.launch { pagerState.animateScrollToPage(myPage + 1) }
            }
        } else {
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
                    scope.launch { pagerState.animateScrollToPage(page + 1) }
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
    progress: Float,
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
            verticalArrangement = Arrangement.Center,
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

            MessagePanelWithDockedIndicator(
                text = page.text,
                panelColor = Color.Black,
                backgroundColor = page.background,
                progress = progress,
                isLast = pageIndex == totalPages - 1,
                onClick = onIndicatorClick,
                modifier = Modifier.size(350.dp)
            )
        }
    }
}


@Composable
private fun MessagePanelWithDockedIndicator(
    text: String,
    panelColor: Color,
    backgroundColor: Color,
    progress: Float,
    isLast: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    notchRadius: Dp = 44.dp,
    indicatorSize: Dp = 92.dp,
    indicatorRingWidth: Dp = 3.dp,
    indicatorProgressWidth: Dp = 8.dp
) {
    Box(
        modifier = modifier.padding(top = 60.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = indicatorSize / 2)
                .background(
                    color = panelColor,
                    shape = NotchedRoundedRectShape(
                        cornerRadius = cornerRadius,
                        notchRadius = notchRadius
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }

        // Docked circular progress button
        Box(
            modifier = Modifier
                .size(indicatorSize)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(indicatorSize)) {
                val ring = indicatorRingWidth.toPx()
                val prog = indicatorProgressWidth.toPx()

                // Outer thin ring
                drawCircle(
                    color = Color.White,
                    style = Stroke(width = ring)
                )

                // Progress arc
                val inset = prog / 2 + ring // spacing from outer ring
                val diameter = size.minDimension - inset * 2f

                translate(left = inset, top = inset) {
                    drawArc(
                        color = Color.White,
                        startAngle = -90f,
                        sweepAngle = 360f * progress.coerceIn(0f, 1f),
                        useCenter = false,
                        style = Stroke(width = prog, cap = StrokeCap.Round),
                        size = androidx.compose.ui.geometry.Size(diameter, diameter)
                    )
                }
            }

            if (isLast) {
                Text(
                    text = "Go",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


private class NotchedRoundedRectShape(
    private val cornerRadius: Dp,
    private val notchRadius: Dp,
    private val notchCenterXFraction: Float = 0.5f
) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline {
        val cr = with(density) { cornerRadius.toPx() }
        val nr = with(density) { notchRadius.toPx() }
        val centerX = size.width * notchCenterXFraction

        val rect = Rect(0f, 0f, size.width, size.height)
        val round = RoundRect(rect, CornerRadius(cr, cr))

        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            addRoundRect(round)

            val oval = Rect(
                left = centerX - nr,
                top = size.height - nr,
                right = centerX + nr,
                bottom = size.height + nr
            )
            addOval(oval)
        }
        return Outline.Generic(path)
    }
}