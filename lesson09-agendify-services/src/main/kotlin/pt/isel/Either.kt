package pt.isel

/**
 * Yuml class diagram:
[Either\<L,R\>|+Left(value: L);+Right(value: R)]
[Either\<L,R\>]^-.-[Left|+value: L]
[Either\<L,R\>]^-.-[Right|+value: R]
 */
sealed class Either<out L, out R> {
    data class Left<out L>(
        val value: L,
    ) : Either<L, Nothing>()

    data class Right<out R>(
        val value: R,
    ) : Either<Nothing, R>()
}

// Functions for when using Either to represent success or failure
fun <R> success(value: R) = Either.Right(value)

fun <L> failure(error: L) = Either.Left(error)

typealias Success<S> = Either.Right<S>
typealias Failure<F> = Either.Left<F>
