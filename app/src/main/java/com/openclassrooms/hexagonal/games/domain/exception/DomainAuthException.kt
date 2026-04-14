package com.openclassrooms.hexagonal.games.domain.exception

sealed class DomainAuthException : Exception() {
    class NeedsReauth : DomainAuthException()
    class NetworkError : DomainAuthException()
    data class UnknownError(override val message: String) : DomainAuthException()
}