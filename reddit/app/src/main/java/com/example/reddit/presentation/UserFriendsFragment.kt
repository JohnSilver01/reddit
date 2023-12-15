package com.example.reddit.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.reddit.R
import com.example.reddit.databinding.FragmentUserFriendsBinding
import com.example.reddit.domain.models.friends.Children
import com.example.reddit.presentation.adapters.CommonLoadStateAdapter
import com.example.reddit.presentation.adapters.UserFriendsAdapter
import com.example.reddit.presentation.utils.hideAppbarAndBottomView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UserFriendsFragment : Fragment(R.layout.fragment_user_friends) {

    private val binding by viewBinding(FragmentUserFriendsBinding::bind)
    private val viewModel: UserFriendsViewModel by viewModels()
    private val userFriendsAdapter = UserFriendsAdapter(
        onDoNotBeFriendsClick = { children: Children, position: Int ->
            onClickDoNotBeFriends(
                children,
                position
            )
        }
    )

    private fun onClickDoNotBeFriends(children: Children, position: Int) {
        viewModel.doNotMakeFriends(children.name)
        userFriendsAdapter.unfriendUser(position)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideAppbarAndBottomView(requireActivity())
        binding.recyclerViewUserFriends.adapter =
            userFriendsAdapter.withLoadStateFooter(CommonLoadStateAdapter())

        binding.swipeRefresh.setOnRefreshListener { userFriendsAdapter.refresh() }

        userFriendsAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.pageFriends.onEach {
            userFriendsAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}