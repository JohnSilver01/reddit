package com.example.reddit.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.reddit.domain.models.friends.Children
import com.example.reddit.domain.repository.RedditRepository
import javax.inject.Inject

class FriendsPagingSource @Inject constructor(
    private val repository: RedditRepository
) : PagingSource<String, Children>(){
    override fun getRefreshKey(state: PagingState<String, Children>): String = ""

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Children> {
        val after = params.key ?: ""
        return kotlin.runCatching {
            repository.getUserFriends()
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.data.children,
                    prevKey = null,
                    nextKey = null
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }
}