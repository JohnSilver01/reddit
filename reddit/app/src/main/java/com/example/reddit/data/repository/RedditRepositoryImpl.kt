package com.example.reddit.data.repository

import com.example.reddit.data.auth.TokenStorage
import com.example.reddit.domain.models.friends.UserFriends
import com.example.reddit.domain.models.response.Response
import com.example.reddit.domain.models.userinfo.UserInfo
import com.example.reddit.domain.models.userprofile.UserProfile
import com.example.reddit.domain.repository.RedditRepository
import com.example.reddit.data.api.RedditApi

class RedditRepositoryImpl : RedditRepository {

    companion object {
        var accessToken = "Bearer ${TokenStorage.accessToken}"
    }

    override suspend fun getNewSubreddits(afterKey: String): Response {
        return RedditApi.create().getNewSubreddits(token = accessToken, afterKey = afterKey)
    }

    override suspend fun getPopularSubreddits(afterKey: String): Response {
        return RedditApi.create().getPopularSubreddits(token = accessToken, afterKey = afterKey)
    }

    override suspend fun searchSubreddits(query: String, afterKey: String): Response {
        return RedditApi.create()
            .searchSubreddits(token = accessToken, query = query, afterKey = afterKey)
    }

    override suspend fun getComments(postId: String): List<Response> {
        return RedditApi.create().getComments(token = accessToken, postId = postId)
    }

    override suspend fun vote(dir: Int, id: String) {
        RedditApi.create().vote(token = accessToken, direction = dir, id = id)
    }

    override suspend fun subscribeUnsubscribe(action: String, displayName: String) {
        RedditApi.create().subscribeUnsubscribe(token = accessToken, action, displayName)
    }

    override suspend fun getUserInfo(userName: String): UserInfo {
        return RedditApi.create().getUserInfo(token = accessToken, userName = userName)
    }

    override suspend fun loadSubredditPosts(subredditName: String, afterKey: String): Response {
        return RedditApi.create()
            .loadSubredditPosts(token = accessToken, subredditName, afterKey = afterKey)
    }

    override suspend fun loadFavoriteSubreddits(afterKey: String): Response {
        return RedditApi.create().loadFavoriteSubreddits(token = accessToken, afterKey = afterKey)
    }

    override suspend fun loadFavoritePosts(
        afterKey: String,
        userName: String,
        type: String
    ): Response {
        return RedditApi.create()
            .loadFavoritePosts(token = accessToken, userName = userName, after = afterKey)
    }

    override suspend fun getUserProfile(): UserProfile {
        return RedditApi.create().getUserProfile(token = accessToken)
    }

    override suspend fun makeFriends(userName: String) {
        RedditApi.create().makeFriends(token = accessToken, userName = userName)
    }

    override suspend fun doNotMakeFriends(userName: String) {
        RedditApi.create().doNotMakeFriends(token = accessToken, userName = userName)
    }

    override suspend fun getUserFriends(): UserFriends {
        return RedditApi.create().getUserFriends(token = accessToken)
    }
}