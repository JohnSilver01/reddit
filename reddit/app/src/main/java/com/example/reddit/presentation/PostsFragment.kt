package com.example.reddit.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.reddit.data.CommonPagingSource
import com.example.reddit.domain.models.ApiResult
import com.example.reddit.domain.models.response.Children
import com.example.reddit.presentation.adapters.CommonLoadStateAdapter
import com.example.reddit.presentation.adapters.PostsAdapter
import com.example.reddit.presentation.utils.hideAppbarAndBottomView
import com.example.reddit.presentation.utils.launchAndCollectIn
import com.example.reddit.presentation.utils.toast
import com.example.reddit.presentation.utils.toastString
import com.bumptech.glide.Glide
import com.example.reddit.R
import com.example.reddit.databinding.FragmentPostsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

const val USER_NAME = "user_name"
const val POST_ID = "post_id"
const val POST_TITLE = "post_title"
const val POST_CONTENT_PICTURE = "post_content_picture"
const val IS_VIDEO = "is_video"
const val VIDEO_URL = "fallback_url"

@UnstableApi
@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.fragment_posts) {

    var voteDirection = 0
    private var userIsSubscriber: Boolean? = null
    private var icon: String? = null
    private var bannerImage: String? = null
    private var displayName: String? = null

    private val binding by viewBinding(FragmentPostsBinding::bind)
    private val viewModel: PostsViewModel by viewModels()
    private val postsAdapter = PostsAdapter(
        onClick = { children: Children -> onItemClick(children) },
        onClickShare = { url: String -> onShareClick(url) },
        onAuthorClick = { authorName: String -> onAuthorClick(authorName) },
        onVoteClick = { dir, id, position -> onVoteClick(dir, id, position) },
        onOpenCommentsClick = { children: Children -> onClickOpenComments(children) }
    )

    private fun onClickOpenComments(children: Children) {
        val bundle = Bundle()
        bundle.putString(POST_ID, children.data.id)
        bundle.putString(POST_TITLE, children.data.title)
        bundle.putString(POST_CONTENT_PICTURE, children.data.url)
        bundle.putBoolean(IS_VIDEO, children.data.isVideo)
        if (children.data.isVideo){
            bundle.putString(VIDEO_URL, children.data.media.redditVideo.dashUrl)
        }
//        if (player?.isPlaying == true){
//            player!!.stop()
//            player!!.release()
//            player = null
//        }
        findNavController().navigate(R.id.action_postsFragment_to_commentsFragment, bundle)
    }

    private fun onVoteClick(dir: Int, id: String, position: Int) {
        voteDirection = dir
        viewModel.vote(dir, id, position)
        toastString("voted $dir")
    }

    private fun onAuthorClick(authorName: String) {
        val bundle = Bundle()
        bundle.putString(USER_NAME, authorName)
        findNavController().navigate(R.id.action_postsFragment_to_userInfoFragment, bundle)
    }

    private fun onShareClick(url: String) {
        val intent = Intent(Intent.ACTION_SEND).also {
            it.putExtra(Intent.EXTRA_TEXT, url)
            it.type = "text/plain"
        }
        try {
            requireContext().startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toastString(e.message)
        }
    }

    private fun onItemClick(children: Children) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayName = arguments?.getString(DISPLAY_NAME_KEY)
        bannerImage = arguments?.getString(BANNER_IMAGE_KEY)
        icon = arguments?.getString(ICON_KEY)
        userIsSubscriber = arguments?.getBoolean(IS_SUBSCRIBER_KEY)
        CommonPagingSource.subredditName = displayName.toString()
        hideAppbarAndBottomView(requireActivity())
        binding.recyclerViewPosts.adapter =
            postsAdapter.withLoadStateFooter(CommonLoadStateAdapter())

        binding.swipeRefresh.setOnRefreshListener { postsAdapter.refresh() }

        postsAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.pagePosts.launchAndCollectIn(viewLifecycleOwner) {
            postsAdapter.submitData(it)
        }

        setupTopBanner()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.subscribeChannel.launchAndCollectIn(viewLifecycleOwner) {
            if (it is ApiResult.Error) {
                toast(R.string.something_went_wrong)
            } else {
                userIsSubscriber = !userIsSubscriber!!
                setupTopBanner()
            }
        }

        viewModel.voteChannel.launchAndCollectIn(viewLifecycleOwner) {
            if (it is ApiResult.Error) {
                toast(R.string.something_went_wrong)
            } else {
                postsAdapter.updatePostScore(it, voteDirection)
            }
        }
    }

    private fun setupTopBanner() {
        with(binding) {
            Glide
                .with(imageViewSubredditImage.context)
                .load(bannerImage)
                .placeholder(R.drawable.reddit_red_background)
                .into(imageViewSubredditImage)
            Glide
                .with(imageViewAvatar.context)
                .load(icon)
                .placeholder(R.drawable.reddit_placeholder)
                .circleCrop()
                .into(imageViewAvatar)
            textViewSubredditName.text = displayName
            if (userIsSubscriber == true) {
                setupButton(
                    getString(R.string.unsubscribe),
                    R.color.orange,
                    R.drawable.subscribed_white
                )
            } else {
                setupButton(
                    getString(R.string.subscribe),
                    R.color.blue_main,
                    R.drawable.subscribe_white
                )
            }
            buttonSubscribe.setOnClickListener {
                viewModel.subscribeUnsubscribe(displayName.toString(), userIsSubscriber == true)
            }
        }
    }

    private fun setupButton(buttonText: String, buttonColor: Int, buttonIcon: Int) {
        binding.buttonSubscribe.text = buttonText
        binding.buttonSubscribe.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                buttonColor,
                context?.theme
            )
        )
        val drawable = ResourcesCompat.getDrawable(
            resources,
            buttonIcon,
            context?.theme
        )
        binding.buttonSubscribe.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }
}