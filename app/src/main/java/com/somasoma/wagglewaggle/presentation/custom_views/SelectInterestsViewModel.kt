package com.somasoma.wagglewaggle.presentation.custom_views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.somasoma.wagglewaggle.presentation.NetworkUtil
import com.somasoma.wagglewaggle.domain.usecase.member.GetInterestListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

open class SelectInterestsViewModel @Inject constructor(
    application: Application,
    private val networkUtil: NetworkUtil,
    private val getInterestListUseCase: GetInterestListUseCase
) :
    AndroidViewModel(application) {

    private val _interests = MutableStateFlow<Set<String>>(setOf())
    val interests: StateFlow<Set<String>> = _interests
    private val _selectedInterests = MutableStateFlow<MutableSet<String>>(mutableSetOf())
    var selectedInterests: StateFlow<MutableSet<String>> = _selectedInterests

    init {
        getInterestList()
        resetSelectedInterests(mutableSetOf())
    }

    fun resetSelectedInterests(newSelectedInterests: Set<String>) {
        _selectedInterests.value = newSelectedInterests.toMutableSet()
    }

    private fun getInterestList() {
        networkUtil.publicRestApiCall(getInterestListUseCase::getInterestList, Unit, viewModelScope) {
            onSuccessCallback = {
                it?.interests?.let {
                    val tmpInterestSet = mutableSetOf<String>()
                    for (interest in it) {
                        if (!interest.isNullOrBlank()) {
                            tmpInterestSet.add(interest)
                        }
                    }
                    _interests.value = tmpInterestSet
                }
            }

            onErrorCallback = {

            }
        }
    }
}