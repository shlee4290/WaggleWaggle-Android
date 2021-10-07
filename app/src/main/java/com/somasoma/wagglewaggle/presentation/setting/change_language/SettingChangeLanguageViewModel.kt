package com.somasoma.wagglewaggle.presentation.setting.change_language

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.somasoma.wagglewaggle.core.SingleLiveEvent
import com.somasoma.wagglewaggle.domain.usecase.SetUserLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingChangeLanguageViewModel @Inject constructor(
    application: Application,
    private val setUserLanguageUseCase: SetUserLanguageUseCase
) :
    AndroidViewModel(application) {

    val finishChangeLanguageEvent = SingleLiveEvent<Unit>()
    private var language: String = ""

    fun onLanguageChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        language = s.toString()
    }

    fun onClickSaveButton() {
        saveNewUserLanguage { finishChangeLanguageEvent.call() }
    }

    private fun saveNewUserLanguage(onSuccessCallback: () -> Unit) {
        if (language.isNotBlank()) {
            setUserLanguageUseCase.setUserLanguage(language, onSuccessCallback)
        }
    }
}