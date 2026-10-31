package com.sazerat.languagelearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.navigation.LanguageLearningNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LanguageLearningTheme {
                val onboardingDone by viewModel.onboardingDone.collectAsStateWithLifecycle()
                // Nothing to show for the instant the preferences take to load.
                onboardingDone?.let { done -> LanguageLearningNavHost(onboardingDone = done) }
            }
        }
    }
}
