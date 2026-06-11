package com.android.kidstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.kidstracker.ui.theme.KidsTrackerTheme
import com.android.kidstracker.data.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Serializable
data class PengumpulanTugas(
    val id: String = "",
    val id_tugas: String,
    val id_murid: String,
    val tanggal_kumpul: String? = null,
    val catatan: String? = null,
    val foto_url: String? = null
)

data class TugasAnakItem(
    val tugas: Tugas,
    val murid: Murid,
    val pengumpulan: PengumpulanTugas?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrtuTaskSubmitScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isSheetOpen by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<TugasAnakItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var daftarMurid by remember { mutableStateOf<List<Murid>>(emptyList()) }
    var selectedMurid by remember { mutableStateOf<Murid?>(null) }
    var semuaTugasList by remember { mutableStateOf<List<TugasAnakItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val tugasUntukAnakIni = semuaTugasList.filter { it.murid.id == selectedMurid?.id }
    val selesaiList = tugasUntukAnakIni.filter { it.pengumpulan != null }
    val belumDikerjakanList = tugasUntukAnakIni.filter { it.pengumpulan == null }

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(Unit) {
        try {
            val currentUser = SupabaseClient.client.auth.currentUserOrNull()
            if (currentUser != null) {
                val muridFetched = SupabaseClient.client.postgrest["murid"]
                    .select { filter { eq("id_ortu", currentUser.id) } }
                    .decodeList<Murid>()

                daftarMurid = muridFetched
                if (muridFetched.isNotEmpty() && selectedMurid == null) {
                    selectedMurid = muridFetched[0]
                }

                val guruIds = muridFetched.mapNotNull { it.id_guru }.distinct()
                val muridIds = muridFetched.map { it.id }

                if (guruIds.isNotEmpty() && muridIds.isNotEmpty()) {
                    val semuaTugas = SupabaseClient.client.postgrest["tugas"]
                        .select { filter { isIn("id_guru", guruIds) } }
                        .decodeList<Tugas>()

                    val semuaPengumpulan = SupabaseClient.client.postgrest["pengumpulan_tugas"]
                        .select { filter { isIn("id_murid", muridIds) } }
                        .decodeList<PengumpulanTugas>()

                    val pairedList = mutableListOf<TugasAnakItem>()
                    muridFetched.forEach { murid ->
                        val tugasGuruIni = semuaTugas.filter { it.id_guru == murid.id_guru }
                        tugasGuruIni.forEach { tugas ->
                            val pengumpulan = semuaPengumpulan.find { it.id_tugas == tugas.id && it.id_murid == murid.id }
                            pairedList.add(TugasAnakItem(tugas, murid, pengumpulan))
                        }
                    }
                    semuaTugasList = pairedList
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
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
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { /*TODO*/ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /*TODO*/ },
                    icon = { Icon(Icons.Default.MonitorHeart, contentDescription = "Growth") },
                    label = { Text("Growth") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Child Selector
            if (daftarMurid.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daftarMurid) { murid ->
                        val isSelected = selectedMurid?.id == murid.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMurid = murid },
                            label = { Text(murid.nama) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Tabs for Active vs Completed
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Belum Dikerjakan (${belumDikerjakanList.size})") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Selesai (${selesaiList.size})") }
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val listToDisplay = if (selectedTabIndex == 0) belumDikerjakanList else selesaiList
    
                    items(listToDisplay) { item ->
                        val task = item.tugas
                        if (selectedTabIndex == 1) {
                            // Completed Task Card
                            Card(
                                modifier = Modifier.fillMaxWidth().alpha(0.6f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = task.judul,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = task.deskripsi ?: "Tidak ada deskripsi",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            // Active Task Card
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selectedTask = item
                                    isSheetOpen = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    // Left Border Indicator
                                    Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                                    
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = task.judul,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Icon(Icons.Default.ChevronRight, contentDescription = "Detail", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = task.deskripsi ?: "Tidak ada deskripsi",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                val tenggat = if (task.tenggat_waktu != null && task.waktu_tenggat != null) "${task.tenggat_waktu} ${task.waktu_tenggat}" else task.tenggat_waktu ?: "Tidak ada"
                                                Text("Tenggat: $tenggat", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                            }
                                            Button(
                                                onClick = {
                                                    selectedTask = item
                                                    isSheetOpen = true
                                                },
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("Kerjakan", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sheet for Submission Form
        if (isSheetOpen && selectedTask != null) {
            ModalBottomSheet(
                onDismissRequest = { isSheetOpen = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                var formNotes by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // Task Details Header
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Tugas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            val tenggat = if (selectedTask!!.tugas.tenggat_waktu != null && selectedTask!!.tugas.waktu_tenggat != null) "${selectedTask!!.tugas.tenggat_waktu} ${selectedTask!!.tugas.waktu_tenggat}" else selectedTask!!.tugas.tenggat_waktu ?: "Tidak ada"
                            Text(tenggat, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(text = selectedTask!!.tugas.judul, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bantu anak Anda berlatih sesuai dengan deskripsi tugas. Kumpulkan bukti berupa foto hasil kerja atau kegiatan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Kumpulkan Tugas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))

                    // File Upload Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .clickable {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = "Upload", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(if (selectedImageUri == null) "Unggah Foto Hasil Kerja" else "Ubah Foto Hasil Kerja", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                            Text(if (selectedImageUri == null) "Ketuk untuk mengambil foto atau pilih dari galeri" else "Foto terpilih: ${selectedImageUri?.lastPathSegment}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notes Field
                    OutlinedTextField(
                        value = formNotes,
                        onValueChange = { formNotes = it },
                        label = { Text("Catatan Orang Tua (Opsional)") },
                        placeholder = { Text("Contoh: Anak masih kesulitan menggambar segitiga...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) isSheetOpen = false
                                }
                            },
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        var imageUrl: String? = null
                                        if (selectedImageUri != null) {
                                            val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                                            val imageByteArray = inputStream?.readBytes()
                                            inputStream?.close()

                                            if (imageByteArray != null) {
                                                val fileName = "tugas_${System.currentTimeMillis()}.jpg"
                                                SupabaseClient.client.storage.from("foto_tugas").upload(fileName, imageByteArray)
                                                imageUrl = SupabaseClient.client.storage.from("foto_tugas").publicUrl(fileName)
                                            }
                                        }

                                        val pengumpulan = PengumpulanTugas(
                                            id_tugas = selectedTask!!.tugas.id,
                                            id_murid = selectedTask!!.murid.id,
                                            tanggal_kumpul = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                                            catatan = formNotes,
                                            foto_url = imageUrl
                                        )

                                        SupabaseClient.client.postgrest["pengumpulan_tugas"].insert(pengumpulan)
                                        
                                        Toast.makeText(context, "Tugas berhasil dikumpulkan!", Toast.LENGTH_SHORT).show()
                                        
                                        // Update local state so it moves to Selesai
                                        val updatedItem = selectedTask!!.copy(pengumpulan = pengumpulan)
                                        semuaTugasList = semuaTugasList.map { 
                                            if (it.tugas.id == updatedItem.tugas.id && it.murid.id == updatedItem.murid.id) updatedItem else it 
                                        }
                                        
                                        sheetState.hide()
                                        isSheetOpen = false
                                        selectedImageUri = null
                                        formNotes = ""
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal mengumpulkan: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit Tugas")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrtuTaskSubmitPreview() {
    KidsTrackerTheme {
        OrtuTaskSubmitScreen(rememberNavController())
    }
}
