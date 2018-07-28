/**
 * This file is inlined from https://github.com/michaelbull/kotlin-result
 */
package au.com.dius.pact.com.github.michaelbull.result

/**
 * Accumulates value starting with [initial] value and applying [operation] from left to right to current accumulator value and each element.
 */
inline fun <T, R, E> Iterable<T>.fold(
  initial: R,
  operation: (acc: R, T) -> Result<R, E>
): Result<R, E> {
    var accumulator = initial

    forEach { element ->
        val operationResult = operation(accumulator, element)

        when (operationResult) {
            is Ok -> {
                accumulator = operationResult.value
            }
            is Err -> return Err(operationResult.error)
        }
    }

    return Ok(accumulator)
}

/**
 * Accumulates value starting with [initial] value and applying [operation] from right to left to each element and current accumulator value.
 */
inline fun <T, R, E> List<T>.foldRight(
  initial: R,
  operation: (T, acc: R) -> Result<R, E>
): Result<R, E> {
    var accumulator = initial

    if (!isEmpty()) {
        val iterator = listIterator(size)
        while (iterator.hasPrevious()) {
            val operationResult = operation(iterator.previous(), accumulator)

            when (operationResult) {
                is Ok -> {
                    accumulator = operationResult.value
                }
                is Err -> return Err(operationResult.error)
            }
        }
    }

    return Ok(accumulator)
}

/**
 * Combines a vararg of [Results][Result] into a single [Result] (holding a [List]).
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#combine)
 */
fun <V, E> combine(vararg results: Result<V, E>) = results.asIterable().combine()

/**
 * Combines an [Iterable] of [Results][Result] into a single [Result] (holding a [List]).
 *
 * - Elm: [Result.Extra.combine](http://package.elm-lang.org/packages/circuithub/elm-result-extra/1.4.0/Result-Extra#combine)
 */
fun <V, E> Iterable<Result<V, E>>.combine(): Result<List<V>, E> {
    return Ok(map {
        when (it) {
            is Ok -> it.value
            is Err -> return it
        }
    })
}

/**
 * Extracts from a vararg of [Results][Result] all the [Ok] elements. All the [Ok] elements are
 * extracted in order.
 *
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
fun <V, E> getAll(vararg results: Result<V, E>) = results.asIterable().getAll()

/**
 * Extracts from an [Iterable] of [Results][Result] all the [Ok] elements. All the [Ok] elements
 * are extracted in order.
 *
 * - Haskell: [Data.Either.lefts](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:lefts)
 */
fun <V, E> Iterable<Result<V, E>>.getAll(): List<V> {
    return filterIsInstance<Ok<V>>().map { it.value }
}

/**
 * Extracts from a vararg of [Results][Result] all the [Err] elements. All the [Err] elements
 * are extracted in order.
 *
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
fun <V, E> getAllErrors(vararg results: Result<V, E>) = results.asIterable().getAllErrors()

/**
 * Extracts from an [Iterable] of [Results][Result] all the [Err] elements. All the [Err]
 * elements are extracted in order.
 *
 * - Haskell: [Data.Either.rights](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:rights)
 */
fun <V, E> Iterable<Result<V, E>>.getAllErrors(): List<E> {
    return filterIsInstance<Err<E>>().map { it.error }
}

/**
 * Partitions a vararg of [Results][Result] into a [Pair] of [Lists][List]. All the [Ok] elements
 * are extracted, in order, to the [first][Pair.first] value. Similarly the [Err] elements are
 * extracted to the [Pair.second] value.
 *
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
fun <V, E> partition(vararg results: Result<V, E>) = results.asIterable().partition()

/**
 * Partitions an [Iterable] of [Results][Result] into a [Pair] of  [Lists][List]. All the [Ok]
 * elements are extracted, in order, to the [first][Pair.first] value. Similarly the [Err]
 * elements are extracted to the [Pair.second] value.
 *
 * - Haskell: [Data.Either.partitionEithers](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html#v:partitionEithers)
 */
fun <V, E> Iterable<Result<V, E>>.partition(): Pair<List<V>, List<E>> {
    val values = mutableListOf<V>()
    val errors = mutableListOf<E>()

    forEach { result ->
        when (result) {
            is Ok -> values.add(result.value)
            is Err -> errors.add(result.error)
        }
    }

    return Pair(values, errors)
}
