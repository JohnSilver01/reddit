package com.example.reddit.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reddit.domain.models.userprofile.UserProfile
import com.example.reddit.domain.repository.RedditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: RedditRepository
) : ViewModel() {
    private var userProfile: UserProfile? = null
    private val _userProfileStateFlow = MutableStateFlow(userProfile)
    val userProfileStateFlow = _userProfileStateFlow

    fun getCurrentUserProfile(){
        viewModelScope.launch {
            runCatching {
                userProfile = repository.getUserProfile()
            }.onSuccess {
                _userProfileStateFlow.value = userProfile
            }.onFailure {
                Log.d("data_test", "Error in VM: ${it.message.toString()}")
            }
        }
    }
}