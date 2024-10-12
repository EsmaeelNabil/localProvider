package com.supersam.dev

import LocalLoginInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlin.random.Random

class ParentScreen : Screen {
    @Composable
    override fun Content() {

        val localState = LocalLoginInfo.current
        val navigator = LocalNavigator.currentOrThrow

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier.padding(24.dp),
                text = localState.mutableValue,
                fontSize = 30.sp,
                color = Color.Blue
            )

            Row {
                Button(onClick = {
                    navigator.push(LoginScreen())
                }) {
                    Text("Navigate ->")
                }

                Button(onClick = {
                    localState.mutableValue = Random.nextInt(0, 1000000).toString()
                }) {
                    Text("Randomize")
                }
            }


        }
    }
}