package com.jetbrains.kmpapp.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class ServerData(
    val tabs: List<String> = emptyList(),
    val type: String = "",
    val tables: Int = 0,
    val version: String = ""
)
