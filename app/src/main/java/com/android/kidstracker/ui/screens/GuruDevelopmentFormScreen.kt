package com.android.kidstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import io.github.jan.supabase.storage.storage
import androidx.compose.material.icons.filled.Image
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class Evaluasi(
    val id: String = "",
    val id_murid: String,
    val id_guru: String,
    val periode: String,
    val kategori: String,
    val status_perkembangan: String,
    val catatan: String,
    val foto_url: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruDevelopmentFormScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Form States
    var muridList by remember { mutableStateOf<List<Murid>>(emptyList()) }
    var selectedMurid by remember { mutableStateOf<Murid?>(null) }
    var expandedStudent by remember { mutableStateOf(false) }

    val categories = listOf("Nilai Agama & Moral", "Fisik Motorik", "Kognitif", "Bahasa", "Sosial Emosional", "Seni")
    var selectedCategory by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }

    val statuses = listOf("Belum Berkembang (BB)", "Mulai Berkembang (MB)", "Berkembang Sesuai Harapan (BSH)", "Berkembang Sangat Baik (BSB)")
    var selectedStatus by remember { mutableStateOf("") }
    var expandedStatus by remember { mutableStateOf(false) }

    val periodeList = listOf("Juli 2026", "Agustus 2026", "September 2026", "Oktober 2026", "November 2026", "Desember 2026")
    var selectedPeriode by remember { mutableStateOf("") }
    var expandedPeriode by remember { mutableStateOf(false) }

    var catatan by remember { mutableStateOf("") }
    
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
                muridList = SupabaseClient.client.postgrest["murid"]
                    .select { filter { eq("id_guru", currentUser.id) } }
                    .decodeList<Murid>()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memuat daftar murid", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Evaluasi Perkembangan",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
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
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Text
                    Column {
                        Text(
                            text = "Formulir Evaluasi",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Catat observasi perkembangan harian siswa untuk membantu memantau kemajuan mereka.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pilih Siswa (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = expandedStudent,
                        onExpandedChange = { expandedStudent = !expandedStudent }
                    ) {
                        OutlinedTextField(
                            value = selectedMurid?.nama ?: "Pilih nama siswa...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Siswa") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStudent) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStudent,
                            onDismissRequest = { expandedStudent = false }
                        ) {
                            muridList.forEach { murid ->
                                DropdownMenuItem(
                                    text = { Text(murid.nama) },
                                    onClick = {
                                        selectedMurid = murid
                                        expandedStudent = false
                                    }
                                )
                            }
                        }
                    }

                    // Periode Observasi
                    ExposedDropdownMenuBox(
                        expanded = expandedPeriode,
                        onExpandedChange = { expandedPeriode = !expandedPeriode }
                    ) {
                        OutlinedTextField(
                            value = selectedPeriode.ifEmpty { "Pilih Periode..." },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Periode Observasi") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPeriode) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPeriode,
                            onDismissRequest = { expandedPeriode = false },
                            modifier = Modifier.heightIn(max = 200.dp)
                        ) {
                            periodeList.forEach { periode ->
                                DropdownMenuItem(
                                    text = { Text(periode) },
                                    onClick = {
                                        selectedPeriode = periode
                                        expandedPeriode = false
                                    }
                                )
                            }
                        }
                    }

                    // Kategori Perkembangan
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.ifEmpty { "Pilih kategori..." },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori Perkembangan") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categories.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedCategory = selectionOption
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    // Status Perkembangan
                    ExposedDropdownMenuBox(
                        expanded = expandedStatus,
                        onExpandedChange = { expandedStatus = !expandedStatus }
                    ) {
                        OutlinedTextField(
                            value = selectedStatus.ifEmpty { "Pilih status..." },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status Perkembangan") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStatus,
                            onDismissRequest = { expandedStatus = false }
                        ) {
                            statuses.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedStatus = selectionOption
                                        expandedStatus = false
                                    }
                                )
                            }
                        }
                    }

                    // Catatan Evaluasi
                    Column {
                        OutlinedTextField(
                            value = catatan,
                            onValueChange = { catatan = it },
                            label = { Text("Catatan Observasi") },
                            placeholder = { Text("Tuliskan secara spesifik apa yang diamati anak hari ini...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Berikan detail aktivitas, respons anak, dan tingkat kemandirian.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pilih Foto Button
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Upload Foto",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (selectedImageUri == null) "Pilih Foto Observasi" else "Ubah Foto Observasi")
                        }
                        if (selectedImageUri != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Foto terpilih: ${selectedImageUri?.lastPathSegment ?: "Gambar.jpg"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { onNavigateBack() },
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        val currentUser = SupabaseClient.client.auth.currentUserOrNull()
                                        if (selectedMurid == null || selectedPeriode.isEmpty() || selectedCategory.isEmpty() || selectedStatus.isEmpty() || catatan.isEmpty()) {
                                            Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }

                                        var imageUrl: String? = null
                                        if (selectedImageUri != null) {
                                            val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                                            val imageByteArray = inputStream?.readBytes()
                                            inputStream?.close()

                                            if (imageByteArray != null) {
                                                val fileName = "evaluasi_${System.currentTimeMillis()}.jpg"
                                                SupabaseClient.client.storage.from("foto_evaluasi").upload(fileName, imageByteArray)
                                                imageUrl = SupabaseClient.client.storage.from("foto_evaluasi").publicUrl(fileName)
                                            }
                                        }

                                        val evaluasiBaru = Evaluasi(
                                            id_murid = selectedMurid!!.id,
                                            id_guru = currentUser?.id ?: "",
                                            periode = selectedPeriode,
                                            kategori = selectedCategory,
                                            status_perkembangan = selectedStatus,
                                            catatan = catatan,
                                            foto_url = imageUrl
                                        )

                                        SupabaseClient.client.postgrest["evaluasi"].insert(evaluasiBaru)
                                        
                                        Toast.makeText(context, "Evaluasi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                        
                                        // Reset Form
                                        selectedMurid = null
                                        selectedPeriode = ""
                                        selectedCategory = ""
                                        selectedStatus = ""
                                        catatan = ""
                                        selectedImageUri = null
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Simpan",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GuruDevelopmentFormPreview() {
    KidsTrackerTheme {
        GuruDevelopmentFormScreen(rememberNavController())
    }
}
