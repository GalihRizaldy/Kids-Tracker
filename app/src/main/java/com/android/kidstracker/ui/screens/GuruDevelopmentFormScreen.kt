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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruDevelopmentFormScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = {}
) {
    // Form States
    var selectedStudent by remember { mutableStateOf("") }
    var expandedStudent by remember { mutableStateOf(false) }
    val students = listOf("Budi Santoso", "Siti Aminah", "Agus Pratama")

    var selectedCategory by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    val categories = listOf("Fisik", "Kognitif", "Sosial")

    var dateValue by remember { mutableStateOf("24 Okt 2024") } // Dummy default date
    var notesValue by remember { mutableStateOf("") }

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
                            value = selectedStudent.ifEmpty { "Pilih nama siswa..." },
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
                            students.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedStudent = selectionOption
                                        expandedStudent = false
                                    }
                                )
                            }
                        }
                    }

                    // Tanggal Observasi
                    OutlinedTextField(
                        value = dateValue,
                        onValueChange = { dateValue = it },
                        label = { Text("Tanggal Observasi") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Kalender",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                    )

                    // Kategori Perkembangan (Dropdown - as per user request)
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

                    // Catatan Evaluasi
                    Column {
                        OutlinedTextField(
                            value = notesValue,
                            onValueChange = { notesValue = it },
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

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = { /*TODO*/ }
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
