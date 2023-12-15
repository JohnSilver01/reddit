package com.example.reddit.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reddit.domain.models.ApiResult
import com.example.reddit.domain.models.UiText
import com.example.reddit.domain.models.userinfo.UserInfo
import com.example.reddit.domain.repository.RedditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val repository: RedditRepository
) : ViewModel() {
    private var userInfo: UserInfo? = null
    private val _userInfoStateFlow = MutableStateFlow(userInfo)
    val userInfoStateFlow = _userInfoStateFlow

    private val _makeFriendsChannel = Channel<ApiResult<String>>()
    val makeFriendsChannel = _makeFriendsChannel.receiveAsFlow()

    private val _doNotMakeFriendsChannel = Channel<ApiResult<String>>()
    val doNotMakeFriendsChannel = _doNotMakeFriendsChannel.receiveAsFlow()

    fun getUserInfo(userName: String) {
        viewModelScope.launch {
            runCatching {
                userInfo = repository.getUserInfo(userName)
            }.onSuccess {
                _userInfoStateFlow.value = userInfo
            }.onFailure {
                Log.d("data_test", "Error in VM: ${it.message.toString()}")
            }
        }
    }

    fun makeFriends(userName: String) {
        viewModelScope.launch {
            runCatching {
                repository.makeFriends(userName)
            }.onSuccess {
                _makeFriendsChannel.send(ApiResult.Success("Success"))
            }.onFailure {
                _makeFriendsChannel.send(ApiResult.Error(UiText.DynamicString(it.message.toString())))
            }
        }
    }

    fun doNotMakeFriends(userName: String) {
        viewModelScope.launch {
            runCatching {
                repository.doNotMakeFriends(userName)
            }.onSuccess {
                _doNotMakeFriendsChannel.send(ApiResult.Success("Success"))
            }.onFailure {
                _doNotMakeFriendsChannel.send(ApiResult.Error(UiText.DynamicString(it.message.toString())))
            }
        }
    }
}