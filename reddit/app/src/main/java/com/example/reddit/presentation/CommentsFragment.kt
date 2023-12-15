package com.example.reddit.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.reddit.data.CommentsPagingSource
import com.example.reddit.domain.ExoPlayerInstance
import com.example.reddit.presentation.adapters.CommentsAdapter
import com.example.reddit.presentation.adapters.CommonLoadStateAdapter
import com.example.reddit.presentation.utils.hideAppbarAndBottomView
import com.example.reddit.presentation.utils.toastString
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.reddit.R
import com.example.reddit.databinding.FragmentCommentsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@UnstableApi
@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {

    var player: ExoPlayer? = null

    private val binding by viewBinding(FragmentCommentsBinding::bind)
    private val viewModel: CommentsViewModel by viewModels()
    private val commentsAdapter = CommentsAdapter(
        onAuthorClick = { authorName: String -> onAuthorClick(authorName) },
        onVoteClick = { dir, id, position -> onVoteClick(dir, id, position) }
    )

    private fun onAuthorClick(authorName: String) {
        // TODO: Show author info
    }

    private fun onVoteClick(dir: Int, id: String, position: Int) {
        var voteMessage = ""
        voteMessage = if (dir == 1) {
            "+1"
        } else "-1"
        toastString(buildString {
            append(resources.getString(R.string.vote))
            append(voteMessage)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getString(POST_ID)
        val postTitle = arguments?.getString(POST_TITLE)
        val postContentPictureUrl = arguments?.getString(POST_CONTENT_PICTURE)
        val isVideo = arguments?.getBoolean(IS_VIDEO)
        val videoUrl = arguments?.getString(VIDEO_URL)

        player = ExoPlayerInstance.get(requireContext())
        player?.release()

        CommentsPagingSource.postId = postId.toString()
        binding.textViewPostTitle.text = postTitle
        if (isVideo == true) {
            player = ExoPlayerInstance.get(requireContext())
            loadVideo(isVideo, videoUrl)
        } else {
            loadPicture(postContentPictureUrl)
        }

        setupCommentsAdapter()
        hideAppbarAndBottomView(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.stop()
        player?.release()
        player = null
    }

    private fun loadPicture(postContentPictureUrl: String?) {
        with(binding) {
            val pictureSize = (resources.displayMetrics.widthPixels / 1.3).toInt()
            val requestOptions = RequestOptions()
                .override(pictureSize, pictureSize)
                .optionalFitCenter()

            if (!postContentPictureUrl.isNullOrEmpty()) {
                playerView.visibility = View.GONE
                imageViewPostContent.visibility = View.VISIBLE
                Glide.with(imageViewPostContent.context)
                    .load(postContentPictureUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(requestOptions)
                    .into(imageViewPostContent)
            }
        }
    }

    @UnstableApi
    private fun loadVideo(isVideo: Boolean?, videoUrl: String?) {
        with(binding) {
            if (isVideo == true) {
                playerView.visibility = View.VISIBLE
                imageViewPostContent.visibility = View.GONE
                val videoUri = Uri.parse(videoUrl)
                player?.playWhenReady = true
                player?.repeatMode = Player.REPEAT_MODE_ONE
                playerView.player = player
                playerView.controllerAutoShow = false
                val mediaItem = MediaItem.fromUri(videoUri)
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.play()
            } else {
                playerView.visibility = View.GONE
            }
        }
    }

    private fun setupCommentsAdapter() {
        binding.recyclerViewComments.adapter =
            commentsAdapter.withLoadStateFooter(CommonLoadStateAdapter())
        binding.swipeRefresh.setOnRefreshListener { commentsAdapter.refresh() }
        commentsAdapter.loadStateFlow.onEach {
            binding.swipeRefresh.isRefreshing = it.refresh == LoadState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.pageComments.onEach {
            commentsAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}