package com.ramazanaksoy.games.models

data class GameDetails (
    val background_image: String,
    val id: Int,
    val name: String,
    val description: String,
    val metacritic: Int,
    val released: String,
)