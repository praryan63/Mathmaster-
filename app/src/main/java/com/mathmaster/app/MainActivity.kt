package com.mathmaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mathmaster.app.data.preferences.SettingsState
import com.mathmaster.app.data.preferences.ThemeSetting
import com.mathmaster.app.di.ServiceLocator
import com.mathmaster.app.ui.navigation.MathMasterNavHost
import com.mathmaster.app.ui.theme.MathMasterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by ServiceLocator.settings.settingsFlow.collectAsStateWithLifecycle(
                initialValue = SettingsState()
            )
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (settings.theme) {
                ThemeSetting.SYSTEM -> systemDark
                ThemeSetting.LIGHT -> false
                ThemeSetting.DARK -> true
            }
            MathMasterTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    MathMasterNavHost(navController = navController, settings = settings)
                }
            }
        }
    }
}
