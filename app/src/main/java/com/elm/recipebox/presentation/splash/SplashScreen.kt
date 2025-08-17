package com.elm.recipebox.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elm.recipebox.R

@Composable
fun SplashScreen(){

        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4058A0)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.loading),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
            )
        }



}


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun SplashPreview(){
    SplashScreen()
}