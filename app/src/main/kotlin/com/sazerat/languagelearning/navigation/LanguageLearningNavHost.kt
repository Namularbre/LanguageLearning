package com.sazerat.languagelearning.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sazerat.languagelearning.R
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.feature.dashboard.navigation.Progress
import com.sazerat.languagelearning.feature.dashboard.navigation.progressScreen
import com.sazerat.languagelearning.feature.grammar.navigation.LessonDetail
import com.sazerat.languagelearning.feature.grammar.navigation.grammarScreens
import com.sazerat.languagelearning.feature.onboarding.navigation.Onboarding
import com.sazerat.languagelearning.feature.onboarding.navigation.onboardingScreen
import com.sazerat.languagelearning.feature.pronunciation.navigation.PronunciationDetail
import com.sazerat.languagelearning.feature.pronunciation.navigation.pronunciationScreens
import com.sazerat.languagelearning.feature.reading.navigation.Library
import com.sazerat.languagelearning.feature.reading.navigation.navigateToReader
import com.sazerat.languagelearning.feature.reading.navigation.readingScreens
import com.sazerat.languagelearning.feature.session.navigation.Today
import com.sazerat.languagelearning.feature.session.navigation.todayScreen
import com.sazerat.languagelearning.feature.settings.navigation.navigateToSettings
import com.sazerat.languagelearning.feature.settings.navigation.settingsScreen
import com.sazerat.languagelearning.feature.srs.navigation.Review
import com.sazerat.languagelearning.feature.srs.navigation.reviewScreen
import kotlin.reflect.KClass

/** The four tabs of the bottom bar (spec §4). */
private enum class Tab(val route: Any, val routeClass: KClass<*>, @StringRes val label: Int, val icon: ImageVector) {
    TODAY(Today, Today::class, R.string.tab_today, Icons.Default.Home),
    LIBRARY(Library, Library::class, R.string.tab_library, Icons.AutoMirrored.Filled.List),
    REVIEW(Review, Review::class, R.string.tab_review, Icons.Default.CheckCircle),
    PROGRESS(Progress, Progress::class, R.string.tab_progress, Icons.Default.DateRange),
}

/**
 * The whole navigation graph. Each feature contributes its screens; only the app connects them.
 * rememberNavController() saves the back stack, so it is restored after process death.
 */
@Composable
fun LanguageLearningNavHost(onboardingDone: Boolean) {
    val navController = rememberNavController()
    // Chosen once: finishing the onboarding navigates to Today rather than changing the graph.
    val startDestination = remember { if (onboardingDone) Today else Onboarding }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    Scaffold(
        bottomBar = {
            // Only on the tabs themselves: the Reader, a lesson or the settings get the whole screen.
            if (Tab.entries.any { destination.isTab(it) }) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = destination.isTab(tab),
                            onClick = { navController.navigateToTab(tab) },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.label)) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()).consumeWindowInsets(padding),
        ) {
            onboardingScreen(languages = studiedLanguages) {
                navController.navigate(Today) { popUpTo(Onboarding) { inclusive = true } }
            }
            todayScreen(
                onOpenReview = { navController.navigateToTab(Tab.REVIEW) },
                onReadText = { id -> navController.navigateToReader(id) },
            )
            readingScreens(
                navController = navController,
                onLessonClick = { id -> navController.navigate(LessonDetail(lessonId = id.value)) },
                onExerciseClick = { id -> navController.navigate(PronunciationDetail(exerciseId = id.value)) },
            )
            reviewScreen(onReadText = { id -> navController.navigateToReader(id) })
            progressScreen(onOpenSettings = { navController.navigateToSettings() })
            settingsScreen(navController, languages = studiedLanguages)
            pronunciationScreens(navController)
            grammarScreens(navController)
        }
    }
}

private fun NavDestination?.isTab(tab: Tab): Boolean = this?.hierarchy?.any { it.hasRoute(tab.routeClass) } == true

/**
 * Standard tab behaviour: one copy of each tab, each keeps its own back stack. Review starts a
 * new session each time, so the cards due since the last one are included.
 */
private fun NavController.navigateToTab(tab: Tab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = tab != Tab.REVIEW
    }
}

// Languages that have content. M2 will derive them from the downloaded content packs.
private val studiedLanguages = listOf(LanguageCode("en-GB"), LanguageCode("ro-RO"), LanguageCode("fr-FR"))
