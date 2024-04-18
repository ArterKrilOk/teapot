package utils

import teapot.feature.Feature
import kotlinx.coroutines.delay
import teapot.message.Message

suspend fun <M : Message> Feature<*, M, *>.testDispatch(message: M, delay: Long = 80) {
    dispatch(message)
    delay(delay)
}