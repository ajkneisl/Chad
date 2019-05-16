package org.woahoverflow.chad.framework.handle.coroutine

import sx.blah.discord.util.RequestBuffer

/**
 * The result to a request
 */
data class RequestResult internal constructor(val result: Any)

/**
 * If the [RequestResult.result] is a Unit. If this is true, there is no result to grab.
 */
fun RequestResult.isUnit(): Boolean = result is Unit

/**
 * If the [RequestResult.result] is a Unit. If this is true, there is no result to grab.
 * Also has a Unit if it's true.
 */
fun RequestResult.isUnit(onError: () -> Unit) {
    if (result is Unit) onError.invoke()
}

/**
 * RequestBuffer, for coroutines.
 */
fun request(req: () -> Any): RequestResult = RequestResult(RequestBuffer.request(req).get())