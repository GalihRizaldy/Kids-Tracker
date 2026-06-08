package com.android.kidstracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Murid(
    val id: String = "", 
    val nama: String, 
    val nomor_induk: String, 
    val jenis_kelamin: String, 
    val alamat: String, 
    val id_ortu: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruStudentManagementScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    // Real Data States
    var muridList by remember { mutableStateOf<List<Murid>>(emptyList()) }
    var ortuList by remember { mutableStateOf<List<AccountUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun fetchAccounts() {
        coroutineScope.launch {
            try {
                isLoading = true
                muridList = SupabaseClient.client.postgrest["murid"].select().decodeList<Murid>()
                ortuList = SupabaseClient.client.postgrest["profiles"]
                    .select { filter { eq("role", "ortu") } }
                    .decodeList<AccountUser>()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal memuat data: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchAccounts()
    }

    // Form States
    var formName by remember { mutableStateOf("") }
    var formNis by remember { mutableStateOf("") }
    var formGender by remember { mutableStateOf("") }
    var formAddress by remember { mutableStateOf("") }
    var expandedGenderDropdown by remember { mutableStateOf(false) }
    
    var selectedOrtuId by remember { mutableStateOf<String?>(null) }
    var selectedOrtuName by remember { mutableStateOf("") }
    var expandedOrtuDropdown by remember { mutableStateOf(false) }
    
    var editingStudentId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "ChildDev Tracker",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    formName = ""
                    formNis = ""
                    formGender = ""
                    formAddress = ""
                    selectedOrtuId = null
                    selectedOrtuName = ""
                    editingStudentId = null
                    isSheetOpen = true 
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Murid")
            }
        },
        bottomBar = {
            com.android.kidstracker.ui.components.ChildDevBottomNavigationBar(
                currentRoute = "GuruHome",
                onNavigateToHome = { /* TODO */ },
                onNavigateToGrowth = { /* TODO */ },
                onNavigateToProfile = { /* TODO */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Header Section
            PaddingValues(16.dp).let { padding ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Murid",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Student List
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (muridList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada data murid.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(muridList) { student ->
                        StudentCard(
                            student = student, 
                            onEditClick = {
                                formName = student.nama
                                formNis = student.nomor_induk
                                formGender = student.jenis_kelamin
                                formAddress = student.alamat
                                selectedOrtuId = student.id_ortu
                                selectedOrtuName = ortuList.find { it.id == student.id_ortu }?.name ?: ""
                                editingStudentId = student.id
                                isSheetOpen = true
                            },
                            onDeleteClick = {
                                coroutineScope.launch {
                                    try {
                                        SupabaseClient.client.postgrest["murid"].delete { filter { eq("id", student.id) } }
                                        Toast.makeText(context, "Murid dihapus!", Toast.LENGTH_SHORT).show()
                                        fetchAccounts()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // FAB spacing
                    }
                }
            }
        }

        // Bottom Sheet for Add/Edit Form
        if (isSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isSheetOpen = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (formName.isEmpty()) "Tambah Murid Baru" else "Detail Murid",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedTextField(
                        value = formName,
                        onValueChange = { formName = it },
                        label = { Text("Nama Lengkap") },
                        placeholder = { Text("Masukkan nama murid") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = formNis,
                        onValueChange = { formNis = it },
                        label = { Text("Nomor Induk") },
                        placeholder = { Text("Misal: 2024001") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedGenderDropdown,
                        onExpandedChange = { expandedGenderDropdown = !expandedGenderDropdown }
                    ) {
                        OutlinedTextField(
                            value = formGender.ifEmpty { "Pilih jenis kelamin..." },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jenis Kelamin") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenderDropdown) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedGenderDropdown,
                            onDismissRequest = { expandedGenderDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Laki-laki") },
                                onClick = {
                                    formGender = "Laki-laki"
                                    expandedGenderDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Perempuan") },
                                onClick = {
                                    formGender = "Perempuan"
                                    expandedGenderDropdown = false
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Parent (Orang Tua) Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedOrtuDropdown,
                        onExpandedChange = { expandedOrtuDropdown = !expandedOrtuDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedOrtuName.ifEmpty { "Pilih orang tua..." },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Orang Tua") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrtuDropdown) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedOrtuDropdown,
                            onDismissRequest = { expandedOrtuDropdown = false }
                        ) {
                            ortuList.forEach { ortu ->
                                DropdownMenuItem(
                                    text = { Text(ortu.name) },
                                    onClick = {
                                        selectedOrtuId = ortu.id
                                        selectedOrtuName = ortu.name
                                        expandedOrtuDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = formAddress,
                        onValueChange = { formAddress = it },
                        label = { Text("Alamat") },
                        placeholder = { Text("Masukkan alamat lengkap") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 3,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) isSheetOpen = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        if (editingStudentId != null) {
                                            val muridUpdate = Murid(
                                                id = editingStudentId!!,
                                                nama = formName,
                                                nomor_induk = formNis,
                                                jenis_kelamin = formGender,
                                                alamat = formAddress,
                                                id_ortu = selectedOrtuId
                                            )
                                            SupabaseClient.client.postgrest["murid"].update(muridUpdate) { filter { eq("id", editingStudentId!!) } }
                                            Toast.makeText(context, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            val newId = UUID.randomUUID().toString()
                                            val muridBaru = Murid(
                                                id = newId,
                                                nama = formName,
                                                nomor_induk = formNis,
                                                jenis_kelamin = formGender,
                                                alamat = formAddress,
                                                id_ortu = selectedOrtuId
                                            )
                                            SupabaseClient.client.postgrest["murid"].insert(muridBaru)
                                            Toast.makeText(context, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                                        }
                                        fetchAccounts()
                                        sheetState.hide()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }.invokeOnCompletion {
                                    if (!sheetState.isVisible) isSheetOpen = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: Murid, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val initials = student.nama.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.nama,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ID: ${student.nomor_induk} • ${student.jenis_kelamin}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Actions (Edit/Delete)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GuruStudentManagementPreview() {
    KidsTrackerTheme {
        GuruStudentManagementScreen(rememberNavController())
    }
}
