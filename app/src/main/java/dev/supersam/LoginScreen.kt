package dev.supersam


import LocalLoginInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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

class LoginScreen : Screen {
    @Composable
    override fun Content() {

        val localState = LocalLoginInfo.current
        val navigator = LocalNavigator.currentOrThrow

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = localState.mutableValue,
                fontSize = 30.sp,
                color = Color.Blue
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {

                Button(
                    onClick = { navigator.pop() }
                ) {
                    Text("Back")
                }

                Button(
                    onClick = {
                        localState.mutableValue = Random.nextInt().toString()
                    }
                ) {
                    Text("Randomize Return value")
                }


            }
        }

    }

}