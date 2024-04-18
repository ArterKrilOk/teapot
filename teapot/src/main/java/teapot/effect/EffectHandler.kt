package teapot.effect

import kotlinx.coroutines.CoroutineScope
import teapot.message.Message

interface EffectHandler<in E : Effect, out M : Message> {
    fun handle(scope: CoroutineScope, effect: E, onCompletion: (M) -> Unit)
}