package com.android.kidstracker.ui.screens

import org.junit.Assert.assertTrue
import org.junit.Test

class LoginLogicTest {

    @Test
    fun `when email contains admin, onNavigateToAdmin is called`() {
        var adminCalled = false
        processLoginNavigation(
            email = "admin_test",
            onNavigateToAdmin = { adminCalled = true },
            onNavigateToGuru = { },
            onNavigateToOrtu = { },
            onAccountNotFound = { }
        )
        assertTrue("Callback onNavigateToAdmin seharusnya terpanggil untuk email 'admin_test'", adminCalled)
    }

    @Test
    fun `when email contains guru, onNavigateToGuru is called`() {
        var guruCalled = false
        processLoginNavigation(
            email = "guru_tk",
            onNavigateToAdmin = { },
            onNavigateToGuru = { guruCalled = true },
            onNavigateToOrtu = { },
            onAccountNotFound = { }
        )
        assertTrue("Callback onNavigateToGuru seharusnya terpanggil untuk email 'guru_tk'", guruCalled)
    }

    @Test
    fun `when email contains ortu, onNavigateToOrtu is called`() {
        var ortuCalled = false
        processLoginNavigation(
            email = "ortu_budi",
            onNavigateToAdmin = { },
            onNavigateToGuru = { },
            onNavigateToOrtu = { ortuCalled = true },
            onAccountNotFound = { }
        )
        assertTrue("Callback onNavigateToOrtu seharusnya terpanggil untuk email 'ortu_budi'", ortuCalled)
    }

    @Test
    fun `when email is unknown, onAccountNotFound is called`() {
        var notFoundCalled = false
        processLoginNavigation(
            email = "tidak_diketahui",
            onNavigateToAdmin = { },
            onNavigateToGuru = { },
            onNavigateToOrtu = { },
            onAccountNotFound = { notFoundCalled = true }
        )
        assertTrue("Callback onAccountNotFound seharusnya terpanggil untuk email acak", notFoundCalled)
    }
}
