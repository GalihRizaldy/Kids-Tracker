package com.android.kidstracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.kidstracker.data.network.SupabaseClient
import com.android.kidstracker.ui.theme.KidsTrackerTheme
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.functions.functions
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

import android.util.Log

@Serializable
data class CreateUserRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String
)

@Serializable
data class AccountUser(
    val id: String,
    val name: String = "Tanpa Nama",
    val email: String = "Tanpa Email",
    val role: String,
    @SerialName("is_active")
    val isActive: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccountManagementScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    // State Management
    var guruList by remember { mutableStateOf<List<AccountUser>>(emptyList()) }
    var ortuList by remember { mutableStateOf<List<AccountUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedUserToEdit by remember { mutableStateOf<AccountUser?>(null) }
    
    // Fungsi Read
    fun fetchAccounts() {
        coroutineScope.launch {
            isLoading = true
            try {
                val profiles = SupabaseClient.client.postgrest["profiles"]
                    .select().decodeList<AccountUser>()
                guruList = profiles.filter { it.role.lowercase() == "guru" }
                ortuList = profiles.filter { it.role.lowercase() == "ortu" }
            } catch (e: Exception) {
                Log.e("KidsTracker", "Fetch Accounts Error", e)
                Toast.makeText(context, "Gagal memuat data: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchAccounts()
    }

    val currentList = if (selectedTabIndex == 0) guruList else ortuList
    val tabs = listOf("Guru", "Orang Tua")

    // Filter by search query
    val filteredList = currentList.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ChildDev Tracker",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifikasi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = { Icon(Icons.Default.Add, contentDescription = "Tambah") },
                text = { Text("Tambah Akun", fontWeight = FontWeight.Medium) },
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
        ) {
            // Page Title
            Text(
                text = "Manajemen Akun",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama atau email...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(50), // Rounded full
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // List of Accounts
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada data", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredList) { user ->
                            AccountCard(
                                user = user,
                                onEdit = { selectedUserToEdit = user },
                                onDelete = {
                                    coroutineScope.launch {
                                        try {
                                            SupabaseClient.client.postgrest["profiles"].delete {
                                                filter { eq("id", user.id) }
                                            }
                                            Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                                            fetchAccounts()
                                        } catch (e: Exception) {
                                            Log.e("KidsTracker", "Delete Account Error", e)
                                            Toast.makeText(context, "Gagal menghapus: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB
                        }
                    }
                }
            }
        }
    }

    // Dialog Tambah Akun
    if (showAddDialog) {
        AddAccountDialog(
            defaultRole = if (selectedTabIndex == 0) "guru" else "ortu",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, email, password, role ->
                coroutineScope.launch {
                    val requestBody = CreateUserRequest(email, password, name, role)
                    try {
                        SupabaseClient.client.functions.invoke("create-user") {
                            contentType(ContentType.Application.Json)
                            setBody(requestBody)
                        }
                        showAddDialog = false
                        fetchAccounts()
                        Toast.makeText(context, "Akun berhasil dibuat via Edge Function!", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.e("KidsTracker", "Add Account Error", e)
                        Toast.makeText(context, "Gagal menambahkan: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    // Dialog Edit Akun
    selectedUserToEdit?.let { user ->
        EditAccountDialog(
            user = user,
            onDismiss = { selectedUserToEdit = null },
            onConfirm = { newName ->
                coroutineScope.launch {
                    try {
                        // Menggunakan Map untuk partial update
                        val updateData = mapOf("name" to newName)
                        SupabaseClient.client.postgrest["profiles"].update(updateData) {
                            filter { eq("id", user.id) }
                        }
                        Toast.makeText(context, "Berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        fetchAccounts()
                        selectedUserToEdit = null
                    } catch (e: Exception) {
                        Log.e("KidsTracker", "Update Account Error", e)
                        Toast.makeText(context, "Gagal memperbarui: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@Composable
fun AccountCard(user: AccountUser, onEdit: () -> Unit, onDelete: () -> Unit) {
    val initials = user.name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
    val isInactive = !user.isActive

    val containerColor = if (isInactive) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.surfaceContainerLow
    val borderModifier = if (isInactive) Modifier.border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)) else Modifier
    val alpha = if (isInactive) 0.75f else 1f
    
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(borderModifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = alpha)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if(isInactive) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Handle card click */ }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isInactive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = if (isInactive) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name & Email
                Column {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                    )
                }
            }

            // Status & Action
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status Badge
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isInactive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isInactive) "Nonaktif" else "Aktif",
                        color = if (isInactive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // More Options Icon
                Box {
                    IconButton(onClick = { expanded = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Nama") },
                            onClick = {
                                expanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                expanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddAccountDialog(
    defaultRole: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(defaultRole) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Akun Baru", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password Sementara") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Column {
                    Text("Role Akses:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = role == "guru", onClick = { role = "guru" })
                        Text("Guru", modifier = Modifier.clickable { role = "guru" })
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = role == "ortu", onClick = { role = "ortu" })
                        Text("Orang Tua", modifier = Modifier.clickable { role = "ortu" })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, email, password, role) },
                enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EditAccountDialog(
    user: AccountUser,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Nama Akun") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminAccountManagementPreview() {
    KidsTrackerTheme {
        AdminAccountManagementScreen(rememberNavController())
    }
}
