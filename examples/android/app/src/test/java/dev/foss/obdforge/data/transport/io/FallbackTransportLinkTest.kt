package dev.foss.obdforge.data.transport.io

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.test.runTest

class FallbackTransportLinkTest {
    @Test
    fun open_prefersFirstLinkInOrder() = runTest {
        val first = FakeTransportLink(openResult = Result.success(Unit))
        val second = FakeTransportLink(openResult = Result.success(Unit))
        val fallback = FallbackTransportLink(listOf(first, second))

        fallback.open()

        assertTrue(first.isOpen)
        assertFalse(second.isOpen)
    }

    @Test
    fun open_triesNextLinkAfterFirstFails() = runTest {
        val failing = FakeTransportLink(openResult = Result.failure(IllegalStateException("classic failed")))
        val succeeding = FakeTransportLink(openResult = Result.success(Unit))
        val fallback = FallbackTransportLink(listOf(failing, succeeding))

        val result = fallback.open()

        assertTrue(result.isSuccess)
        assertTrue(succeeding.isOpen)
        assertFalse(failing.isOpen)
    }

    @Test
    fun open_failsWhenAllLinksFail() = runTest {
        val first = FakeTransportLink(openResult = Result.failure(IllegalStateException("first")))
        val second = FakeTransportLink(openResult = Result.failure(IllegalStateException("second")))
        val fallback = FallbackTransportLink(listOf(first, second))

        val result = fallback.open()

        assertTrue(result.isFailure)
        assertFalse(fallback.isOpen)
    }

    private class FakeTransportLink(
        private val openResult: Result<Unit>,
    ) : TransportLink {
        private var open = false
        override val isOpen: Boolean get() = open

        override suspend fun open(): Result<Unit> {
            return openResult.onSuccess { open = true }
        }

        override suspend fun close() {
            open = false
        }

        override suspend fun write(bytes: ByteArray): Result<Unit> = Result.success(Unit)

        override suspend fun readAvailable(timeoutMs: Long): Result<ByteArray> = Result.success(ByteArray(0))
    }
}
