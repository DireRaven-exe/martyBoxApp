package com.jetbrains.kmpapp.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class ServerData(
    val tabs: List<String>,
    val type: String,
    val tables: Int
)
