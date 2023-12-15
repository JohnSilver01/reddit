package com.example.reddit.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.reddit.domain.models.friends.Children
import com.bumptech.glide.Glide
import com.example.reddit.R
import com.example.reddit.databinding.ItemFriendBinding
import java.text.SimpleDateFormat
import java.util.Locale

class UserFriendsAdapter(
    private val onDoNotBeFriendsClick: (Children, position: Int) -> Unit,
) : PagingDataAdapter<Children, FriendsViewHolder>(DiffUtilCallbackFriends()) {

    fun unfriendUser(position: Int) {
//        snapshot()[position]?.let {
//            it.name
//        }
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            item?.let {
                Glide
                    .with(imageViewUserFriendAvatar.context)
                    .load(it.id)
                    .placeholder(R.drawable.reddit_placeholder)
                    .into(imageViewUserFriendAvatar)
                textViewUserFriendName.text = it.name
                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                textViewUserFriendDateTime.text = dateFormat.format((it.date * 1000L))
                buttonUserFriendsDoNotBeFriend.setOnClickListener {
                    onDoNotBeFriendsClick.invoke(item, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        return FriendsViewHolder(
            ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}

class DiffUtilCallbackFriends : DiffUtil.ItemCallback<Children>() {
    override fun areItemsTheSame(oldItem: Children, newItem: Children): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Children, newItem: Children): Boolean =
        oldItem == newItem
}

class FriendsViewHolder(val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root)