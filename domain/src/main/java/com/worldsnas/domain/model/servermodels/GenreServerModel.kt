package com.worldsnas.domain.model.servermodels

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class GenreServerModel(
    @Json(name = "id")
    val id: Long = 0,
    @Json(name = "name")
    val name: String = ""
)