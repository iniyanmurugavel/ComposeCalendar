package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyCalendar(
                    today = LocalDate.of(2024, 2, 17),
                    selected = LocalDate.of(2024, 2, 17),
                    ui = CalendarUI(
                        availableSessions = listOf(
                            LocalDate.of(2024, 2, 17),
                            LocalDate.of(2024, 2, 21),
                            LocalDate.of(2024, 2, 28),
                            LocalDate.of(2024, 6, 17),
                            LocalDate.of(2024, 6, 21),
                            LocalDate.of(2024, 6, 28),
                            LocalDate.of(2025, 1, 17),
                            LocalDate.of(2025, 1, 21),
                            LocalDate.of(2025, 1, 28)
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}