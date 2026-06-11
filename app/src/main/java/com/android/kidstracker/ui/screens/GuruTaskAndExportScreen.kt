package com.android.kidstracker.ui.screens

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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.kidstracker.ui.theme.KidsTrackerTheme
import com.android.kidstracker.data.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class Tugas(
    val id: String = "",
    val judul: String,
    val deskripsi: String? = null,
    val tanggal_mulai: String? = null,
    val waktu_mulai: String? = null,
    val tenggat_waktu: String? = null,
    val waktu_tenggat: String? = null,
    val id_guru: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruTaskAndExportScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var tugasList by remember { mutableStateOf<List<Tugas>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val fetchTugas: () -> Unit = {
        coroutineScope.launch {
            try {
                isLoading = true
                val currentUser = SupabaseClient.client.auth.currentUserOrNull()
                val result = SupabaseClient.client.postgrest["tugas"]
                    .select { filter { eq("id_guru", currentUser?.id ?: "") } }
                    .decodeList<Tugas>()
                tugasList = result
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal memuat tugas: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchTugas()
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /*TODO*/ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /*TODO*/ },
                    icon = { Icon(Icons.Default.MonitorHeart, contentDescription = "Growth") },
                    label = { Text("Growth") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /*TODO*/ },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // "Tambah Tugas Baru" Button at the top
            item {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.AddTask, contentDescription = "Add Task")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tambah Tugas Baru", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Task List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Tugas Aktif",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${tugasList.size} Tugas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Tasks List
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (tugasList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Belum ada tugas yang diberikan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(tugasList) { task ->
                    TaskCard(
                        task = task,
                        onDelete = {
                            coroutineScope.launch {
                                try {
                                    SupabaseClient.client.postgrest["tugas"].delete { filter { eq("id", task.id) } }
                                    Toast.makeText(context, "Tugas dihapus", Toast.LENGTH_SHORT).show()
                                    fetchTugas()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }

            // Export Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Ekspor Data Tugas",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unduh atau cetak daftar tugas untuk keperluan laporan administratif.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.TableView, contentDescription = "Excel", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Excel", maxLines = 1)
                            }
                            Button(
                                onClick = { /*TODO*/ },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cetak", maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // Add Task Bottom Sheet Form
        if (showAddDialog) {
            ModalBottomSheet(
                onDismissRequest = { showAddDialog = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                var taskTitle by remember { mutableStateOf("") }
                var taskDesc by remember { mutableStateOf("") }
                var inputTanggalMulai by remember { mutableStateOf("") }
                var inputWaktuMulai by remember { mutableStateOf("") }
                var inputTanggalTenggat by remember { mutableStateOf("") }
                var inputWaktuTenggat by remember { mutableStateOf("") }

                var showStartDatePicker by remember { mutableStateOf(false) }
                var showDueDatePicker by remember { mutableStateOf(false) }
                var showStartTimePicker by remember { mutableStateOf(false) }
                var showDueTimePicker by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                        Icon(Icons.Default.AddTask, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Tugas Baru", style = MaterialTheme.typography.titleLarge)
                    }

                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Judul Tugas") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = taskDesc,
                        onValueChange = { taskDesc = it },
                        label = { Text("Deskripsi") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Tanggal & Waktu Mulai
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val startDateSource = remember { MutableInteractionSource() }
                        if (startDateSource.collectIsPressedAsState().value) showStartDatePicker = true
                        OutlinedTextField(
                            value = inputTanggalMulai,
                            onValueChange = {},
                            label = { Text("Tgl Mulai") },
                            readOnly = true,
                            interactionSource = startDateSource,
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )

                        val startTimeSource = remember { MutableInteractionSource() }
                        if (startTimeSource.collectIsPressedAsState().value) showStartTimePicker = true
                        OutlinedTextField(
                            value = inputWaktuMulai,
                            onValueChange = {},
                            label = { Text("Jam Mulai") },
                            readOnly = true,
                            interactionSource = startTimeSource,
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Tanggal & Waktu Tenggat
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val dueDateSource = remember { MutableInteractionSource() }
                        if (dueDateSource.collectIsPressedAsState().value) showDueDatePicker = true
                        OutlinedTextField(
                            value = inputTanggalTenggat,
                            onValueChange = {},
                            label = { Text("Tgl Tenggat") },
                            readOnly = true,
                            interactionSource = dueDateSource,
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )

                        val dueTimeSource = remember { MutableInteractionSource() }
                        if (dueTimeSource.collectIsPressedAsState().value) showDueTimePicker = true
                        OutlinedTextField(
                            value = inputWaktuTenggat,
                            onValueChange = {},
                            label = { Text("Jam Tenggat") },
                            readOnly = true,
                            interactionSource = dueTimeSource,
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val currentUser = SupabaseClient.client.auth.currentUserOrNull()
                                    val newTask = Tugas(
                                        judul = taskTitle,
                                        deskripsi = taskDesc.ifBlank { null },
                                        tanggal_mulai = inputTanggalMulai.ifBlank { null },
                                        waktu_mulai = inputWaktuMulai.ifBlank { null },
                                        tenggat_waktu = inputTanggalTenggat.ifBlank { null },
                                        waktu_tenggat = inputWaktuTenggat.ifBlank { null },
                                        id_guru = currentUser?.id
                                    )
                                    SupabaseClient.client.postgrest["tugas"].insert(newTask)
                                    
                                    sheetState.hide()
                                    showAddDialog = false
                                    Toast.makeText(context, "Tugas berhasil dibuat", Toast.LENGTH_SHORT).show()
                                    fetchTugas()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Tugas")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Pickers Setup
                if (showStartDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showStartDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                                    inputTanggalMulai = formattedDate
                                }
                                showStartDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showStartDatePicker = false }) { Text("Batal") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                if (showDueDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDueDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                                    inputTanggalTenggat = formattedDate
                                }
                                showDueDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDueDatePicker = false }) { Text("Batal") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                if (showStartTimePicker) {
                    val timePickerState = rememberTimePickerState()
                    AlertDialog(
                        onDismissRequest = { showStartTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                inputWaktuMulai = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                                showStartTimePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showStartTimePicker = false }) { Text("Batal") }
                        },
                        text = {
                            TimePicker(state = timePickerState)
                        }
                    )
                }

                if (showDueTimePicker) {
                    val timePickerState = rememberTimePickerState()
                    AlertDialog(
                        onDismissRequest = { showDueTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                inputWaktuTenggat = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                                showDueTimePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDueTimePicker = false }) { Text("Batal") }
                        },
                        text = {
                            TimePicker(state = timePickerState)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Tugas, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Color indicator line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = task.judul,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    if (!task.tenggat_waktu.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = task.tenggat_waktu,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                if (!task.deskripsi.isNullOrBlank()) {
                    Text(
                        text = task.deskripsi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lihat Detail",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { /*TODO*/ }
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus Tugas",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GuruTaskAndExportPreview() {
    KidsTrackerTheme {
        GuruTaskAndExportScreen(rememberNavController())
    }
}
