package com.example.reddit.domain.models.friends


import com.google.gson.annotations.SerializedName

data class Children(
    @SerializedName("date")
    val date: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("rel_id")
    val relId: String
)