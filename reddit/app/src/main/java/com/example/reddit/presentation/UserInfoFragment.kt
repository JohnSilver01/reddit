package com.example.reddit.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.reddit.domain.models.ApiResult
import com.example.reddit.domain.models.userinfo.UserInfo
import com.example.reddit.presentation.utils.hideAppbar
import com.example.reddit.presentation.utils.launchAndCollectIn
import com.example.reddit.presentation.utils.toastString
import com.bumptech.glide.Glide
import com.example.reddit.R
import com.example.reddit.databinding.FragmentUserInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class UserInfoFragment : Fragment(R.layout.fragment_user_info) {

    private val binding by viewBinding(FragmentUserInfoBinding::bind)
    private val viewModel: UserInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userName = arguments?.getString(USER_NAME)
        Toast.makeText(requireContext(), "$userName", Toast.LENGTH_SHORT).show()
        hideAppbar(requireActivity())

        userName?.let { viewModel.getUserInfo(it) }

        viewModel.userInfoStateFlow.launchAndCollectIn(viewLifecycleOwner) { userInfo ->
            userInfo?.let {
                showUserInfo(userInfo)

                binding.buttonBeFriends.setOnClickListener {
                    beFriends(userInfo)
                }
            }
        }

        viewModel.makeFriendsChannel.launchAndCollectIn(viewLifecycleOwner) {
            if (it is ApiResult.Error) {
                toastString(it.data)
            }
        }

        viewModel.doNotMakeFriendsChannel.launchAndCollectIn(viewLifecycleOwner) {
            if (it is ApiResult.Error) {
                toastString(it.data)
            }
        }
    }

    private fun beFriends(userInfo: UserInfo) {
        if (userInfo.data.isFriend) {
            userInfo.data.isFriend = false
            viewModel.doNotMakeFriends(userInfo.data.name)
            showUserInfo(userInfo)
        } else {
            userInfo.data.isFriend = true
            viewModel.makeFriends(userInfo.data.name)
            showUserInfo(userInfo)
        }
    }

    private fun showUserInfo(userInfo: UserInfo) {
        with(binding) {
            Glide.with(imageViewUserInfoAvatar.context)
                .load(userInfo.data.snoovatarImg)
                .placeholder(R.drawable.reddit_placeholder)
                .optionalCircleCrop()
                .into(imageViewUserInfoAvatar)
            textViewUserInfoName.text = userInfo.data.name
            textViewUserInfoKarma.text = userInfo.data.totalKarma.toString()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            textViewUserInfoCreated.text = dateFormat.format((userInfo.data.createdUtc * 1000L))
            if (userInfo.data.isFriend) {
                setupButton(
                    getString(R.string.don_t_be_friends),
                    R.color.orange,
                    R.drawable.subscribed_white
                )
            } else {
                setupButton(
                    getString(R.string.be_friends),
                    R.color.blue_main,
                    R.drawable.subscribe_white
                )
            }
        }
    }

    private fun setupButton(buttonText: String, buttonColor: Int, buttonIcon: Int) {
        binding.buttonBeFriends.text = buttonText
        binding.buttonBeFriends.setBackgroundColor(
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
        binding.buttonBeFriends.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }
}