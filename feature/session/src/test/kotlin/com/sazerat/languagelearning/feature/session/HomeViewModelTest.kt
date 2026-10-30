package com.sazerat.languagelearning.feature.session

import androidx.lifecycle.SavedStateHandle
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.SessionDuration
import com.sazerat.languagelearning.feature.session.home.HomeViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HomeViewModelTest {
    @Test
    fun `default duration is five minutes`() {
        assertEquals(SessionDuration.FIVE, HomeViewModel(SavedStateHandle()).selectedDuration.value)
    }

    @Test
    fun `selected duration is restored from the saved state`() {
        // Simulates process death: the old handle's content is given to a brand new ViewModel.
        val handle = SavedStateHandle()
        HomeViewModel(handle).onDurationSelected(SessionDuration.FIFTEEN)

        val restored = HomeViewModel(SavedStateHandle(handle.keys().associateWith { handle.get<Any>(it) }))

        assertEquals(SessionDuration.FIFTEEN, restored.selectedDuration.value)
    }

    @Test
    fun `no language is selected until the user picks one`() {
        assertNull(HomeViewModel(SavedStateHandle()).selectedLanguage.value)
    }

    @Test
    fun `selected language is restored from the saved state`() {
        val handle = SavedStateHandle()
        HomeViewModel(handle).onLanguageSelected(LanguageCode("fr-FR"))

        val restored = HomeViewModel(SavedStateHandle(handle.keys().associateWith { handle.get<Any>(it) }))

        assertEquals("fr-FR", restored.selectedLanguage.value)
    }
}
