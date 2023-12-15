package com.example.reddit.domain.models.response


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("kind")
    val kind: String
)