import features.Chats
import features.CounterFeature
import features.MessagesFeature
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import utils.testDispatch

class TeapotTest {
    @Test
    fun testFeatureCreation() {
        val gChat = Chats.getGlobalChat()
        val feature = CounterFeature.provider(gChat).createFeature()

        assertEquals(0, feature.currentState.value)
    }

    @Test
    fun testFeatureDispatch() = runBlocking {
        val gChat = Chats.getGlobalChat()
        val feature = CounterFeature.provider(gChat).createFeature()

        assertEquals(0, feature.currentState.value)
        feature.testDispatch(CounterFeature.Msg.Increment)
        assertEquals(1, feature.currentState.value)
        feature.testDispatch(CounterFeature.Msg.Clear)
        assertEquals(0, feature.currentState.value)
    }

    @Test
    fun testFeatureSimpleEffect() = runBlocking {
        val gChat = Chats.getGlobalChat()
        val feature = MessagesFeature.provider(gChat).createFeature()

        assert(feature.currentState.messages.isEmpty())
        assert(feature.currentState.isLoading.not())

        feature.testDispatch(MessagesFeature.Msg.SendMessage("test message"))
        assert(feature.currentState.isLoading.not())
        assert(feature.currentState.messages.isNotEmpty())
        assertEquals(feature.currentState.messages.last(), "test message")

        feature.testDispatch(MessagesFeature.Msg.SendMessage("test message 2"))
        assert(feature.currentState.isLoading.not())
        assert(feature.currentState.messages.isNotEmpty())
        assertEquals(feature.currentState.messages.last(), "test message 2")

        assertEquals(2, feature.currentState.messages.size)
        assertEquals(feature.currentState.messages, listOf("test message", "test message 2"))

        feature.testDispatch(MessagesFeature.Msg.Clear)
        assert(feature.currentState.messages.isEmpty())
    }

    @Test
    fun testChatCommunicationSelf() = runBlocking {
        val gChat = Chats.getGlobalChat()

        val feature = MessagesFeature.provider(gChat).createFeature()

        assert(feature.currentState.messages.isEmpty())
        assert(feature.currentState.isLoading.not())

        feature.testDispatch(MessagesFeature.Msg.SendChatMessage(MessagesFeature.Msg.SendMessage("test message")))
        assert(feature.currentState.isLoading.not())
        assert(feature.currentState.messages.isNotEmpty())
        assertEquals(feature.currentState.messages.last(), "test message")
    }

    @Test
    fun testChatCommunication() = runBlocking {
        val gChat = Chats.getGlobalChat()

        val counterFeature = CounterFeature.provider(gChat).createFeature()
        val messagesFeature = MessagesFeature.provider(gChat).createFeature()

        assertEquals(0, counterFeature.currentState.value)
        assert(messagesFeature.currentState.messages.isEmpty())
        assert(messagesFeature.currentState.isLoading.not())

        messagesFeature.testDispatch(MessagesFeature.Msg.SendChatMessage(CounterFeature.Msg.Increment))
        assertEquals(1, counterFeature.currentState.value)
    }

    @Test
    fun testChatCommunicationCross() = runBlocking {
        val gChat = Chats.getGlobalChat()

        val counterFeature = CounterFeature.provider(gChat).createFeature()
        val messagesFeature = MessagesFeature.provider(gChat).createFeature()

        assertEquals(0, counterFeature.currentState.value)
        assert(messagesFeature.currentState.messages.isEmpty())
        assert(messagesFeature.currentState.isLoading.not())

        messagesFeature.testDispatch(MessagesFeature.Msg.SendChatMessage(CounterFeature.Msg.Increment))
        assertEquals(1, counterFeature.currentState.value)

        counterFeature.testDispatch(CounterFeature.Msg.SendMessageToMessagesFeature("test message"))
        assertEquals(1, counterFeature.currentState.value)
        assert(messagesFeature.currentState.isLoading.not())
        assert(messagesFeature.currentState.messages.isNotEmpty())
        assertEquals(messagesFeature.currentState.messages.last(), "test message")
    }
}