package com.example.reddit.domain.models.response


import com.google.gson.annotations.SerializedName

data class CommentContributionSettings(
    @SerializedName("allowed_media_types")
    val allowedMediaTypes: List<String>
)