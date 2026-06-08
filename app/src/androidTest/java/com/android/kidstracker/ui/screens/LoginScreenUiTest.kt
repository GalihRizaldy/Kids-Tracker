package com.android.kidstracker.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenUiTest {

    // Rule ini wajib untuk menginisiasi environment Compose di dalam testing
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_adminLogin_triggersAdminCallback() {
        var adminCalled = false

        // 1. SETUP: Render UI LoginScreen ke dalam environment testing
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToAdmin = { adminCalled = true },
                onNavigateToGuru = { },
                onNavigateToOrtu = { }
            )
        }

        // 2. ACTION: Simulasi aksi pengguna (Black Box)
        // Cari kolom input yang memiliki label "Email" dan ketik teks
        composeTestRule.onNodeWithText("Email").performTextInput("admin_tester")
        
        // Cari tombol yang memiliki teks "Masuk" dan lakukan simulasi klik
        composeTestRule.onNodeWithText("Masuk").performClick()

        // 3. ASSERT: Verifikasi hasil akhir
        assertTrue("Aksi klik tombol Masuk seharusnya memicu callback Admin", adminCalled)
    }
}
