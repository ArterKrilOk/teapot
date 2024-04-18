package utils

import feature.Feature
import kotlinx.coroutines.delay
import message.Message

suspend fun <M : Message> Feature<*, M, *>.testDispatch(message: M, delay: Long = 80) {
    dispatch(message)
    delay(delay)
}