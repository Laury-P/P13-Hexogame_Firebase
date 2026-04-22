package com.openclassrooms.hexagonal.games.ui.util

sealed class IsPublishing {
    object Idle : IsPublishing()
    object Publishing : IsPublishing()
    object Published : IsPublishing()
}