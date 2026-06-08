package com.android.kidstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.android.kidstracker.ui.navigation.AppNavigation
import com.android.kidstracker.ui.theme.KidsTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KidsTrackerTheme {
                AppNavigation()
            }
        }
    }
}