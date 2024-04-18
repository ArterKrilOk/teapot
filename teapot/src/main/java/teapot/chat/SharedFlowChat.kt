package teapot.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import teapot.message.Message

open class SharedFlowChat : Chat {

    private val messages = MutableSharedFlow<Message>(replay = 0, extraBufferCapacity = 1024)
    override val messageFlow: Flow<Message>
        get() = messages

    override suspend fun send(message: Message) {
        messages.emit(message)
    }
}