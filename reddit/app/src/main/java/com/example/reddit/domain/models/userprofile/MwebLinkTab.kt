package com.example.reddit.domain.models.userprofile


import com.google.gson.annotations.SerializedName

data class MwebLinkTab(
    @SerializedName("experiment_id")
    val experimentId: Int,
    @SerializedName("owner")
    val owner: String,
    @SerializedName("variant")
    val variant: String
)