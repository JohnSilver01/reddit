package com.example.reddit.domain.repository

import com.example.reddit.domain.models.friends.UserFriends
import com.example.reddit.domain.models.response.Response
import com.example.reddit.domain.models.userinfo.UserInfo
import com.example.reddit.domain.models.userprofile.UserProfile

interface RedditRepository {
    suspend fun getNewSubreddits(afterKey: String): Response
    suspend fun getPopularSubreddits(afterKey: String): Response
    suspend fun searchSubreddits(query: String, afterKey: String): Response
    suspend fun getComments(postId: String): List<Response>
    suspend fun vote(dir: Int, id: String)
    suspend fun subscribeUnsubscribe(action: String, displayName: String)
    suspend fun getUserInfo(userName: String): UserInfo
    suspend fun loadSubredditPosts(subredditName: String, afterKey: String): Response
    suspend fun loadFavoriteSubreddits(afterKey: String): Response
    suspend fun loadFavoritePosts(afterKey: String, userName: String, type: String): Response
    suspend fun getUserProfile(): UserProfile
    suspend fun makeFriends(userName: String)
    suspend fun doNotMakeFriends(userName: String)
    suspend fun getUserFriends(): UserFriends
}