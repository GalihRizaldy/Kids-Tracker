package com.android.kidstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.android.kidstracker.data.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val role: String,
    val name: String = "User"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToGuru: () -> Unit = {},
    onNavigateToOrtu: () -> Unit = {}
) {
    val context = LocalContext.current
    var inputEmail by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(0) } // 0: Orang Tua, 1: Guru, 2: Admin
    val roles = listOf("Orang Tua", "Guru", "Admin")
    
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    // Menggunakan background utama
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Kontainer kartu / sheet yang menutupi bagian bawah layar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Warna abu-abu muda menyerupai desain
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ikon Logo (Lingkaran biru dengan ikon senyum)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person, // Menggunakan ikon Person standar sebagai placeholder
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Judul
                Text(
                    text = "ChildDev Tracker",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subjudul
                Text(
                    text = "Selamat datang, silakan masuk ke akun Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Segmented Control Custom untuk peran
                RoleSegmentedControl(
                    roles = roles,
                    selectedIndex = selectedRole,
                    onRoleSelected = { selectedRole = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input Email
                OutlinedTextField(
                    value = inputEmail,
                    onValueChange = { inputEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Kata Sandi
                OutlinedTextField(
                    value = inputPassword,
                    onValueChange = { inputPassword = it },
                    label = { Text("Kata Sandi") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // Tombol Masuk
                Button(
                    onClick = { 
                        if (inputEmail.isBlank() || inputPassword.isBlank()) {
                            Toast.makeText(context, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                SupabaseClient.client.auth.signInWith(Email) { 
                                    email = inputEmail.trim()
                                    password = inputPassword
                                }
                                
                                val currentUser = SupabaseClient.client.auth.currentUserOrNull()
                                if (currentUser != null) {
                                    val profile = SupabaseClient.client.postgrest["profiles"]
                                        .select {
                                            filter {
                                                eq("id", currentUser.id)
                                            }
                                        }.decodeSingle<UserProfile>()
                                        
                                    isLoading = false
                                    
                                    val expectedRole = when (selectedRole) {
                                        0 -> "ortu"
                                        1 -> "guru"
                                        2 -> "admin"
                                        else -> ""
                                    }
                                    
                                    if (profile.role.lowercase() == expectedRole) {
                                        when (expectedRole) {
                                            "admin" -> onNavigateToAdmin()
                                            "guru" -> onNavigateToGuru()
                                            "ortu" -> onNavigateToOrtu()
                                        }
                                    } else {
                                        // Logout jika tab yang dipilih tidak sesuai dengan role di database
                                        SupabaseClient.client.auth.signOut()
                                        Toast.makeText(context, "Gagal: Akun tidak di temukan", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Sesi tidak ditemukan setelah login", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Masuk",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSegmentedControl(roles: List<String>, selectedIndex: Int, onRoleSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        roles.forEachIndexed { index, role ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onRoleSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = role,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}


