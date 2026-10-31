package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.AuraViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aura AI", appName)
    }

    @Test
    fun `viewModel initial state has conversation stream and widgets`() {
        val viewModel = AuraViewModel()
        val state = viewModel.uiState.value

        assertEquals("Aura-Reason-v2.5", state.currentModel)
        assertEquals("chat", state.currentModule)
        assertTrue(state.messages.isNotEmpty())

        val firstMsg = state.messages.first()
        assertEquals("user", firstMsg.sender)
        assertTrue(firstMsg.content.contains("summarize my schedule"))

        val secondMsg = state.messages[1]
        assertEquals("assistant", secondMsg.sender)
        assertEquals(2, secondMsg.widgets.size)
    }

    @Test
    fun `viewModel toggles task status`() {
        val viewModel = AuraViewModel()
        val initialStatus = viewModel.uiState.value.tasks.first().isCompleted

        viewModel.toggleTask("t1")
        val updatedStatus = viewModel.uiState.value.tasks.first().isCompleted
        assertEquals(!initialStatus, updatedStatus)
    }
}
