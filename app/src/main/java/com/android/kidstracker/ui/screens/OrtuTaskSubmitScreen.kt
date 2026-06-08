package com.android.kidstracker.ui.screens

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
import kotlinx.coroutines.launch

// Dummy Data Class
data class OrtuTaskItem(
    val title: String,
    val description: String,
    val deadline: String,
    val isCompleted: Boolean = false,
    val categoryColor: Color = Color.Transparent
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
    var selectedTask by remember { mutableStateOf<OrtuTaskItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val tasks = listOf(
        OrtuTaskItem(
            title = "Menggambar Bentuk Dasar",
            description = "Latihan motorik halus dengan menggambar lingkaran, kotak, dan segitiga di kertas.",
            deadline = "Besok, 17:00",
            isCompleted = false,
            categoryColor = Color(0xFF004A76) // primary
        ),
        OrtuTaskItem(
            title = "Bercerita Pendek",
            description = "Minta anak menceritakan kembali dongeng fabel sederhana yang dibacakan.",
            deadline = "15 Okt, 10:00",
            isCompleted = false,
            categoryColor = Color(0xFF3E6837) // secondary
        ),
        OrtuTaskItem(
            title = "Menyusun Balok",
            description = "Tugas Selesai",
            deadline = "",
            isCompleted = true
        )
    )

    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }

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
                    text = { Text("Belum Dikerjakan (${activeTasks.size})") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Selesai (${completedTasks.size})") }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val listToDisplay = if (selectedTabIndex == 0) activeTasks else completedTasks

                items(listToDisplay) { task ->
                    if (task.isCompleted) {
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
                                        text = task.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        // Active Task Card
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedTask = task
                                isSheetOpen = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Left Border Indicator
                                Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(task.categoryColor))
                                
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Icon(Icons.Default.ChevronRight, contentDescription = "Detail", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = task.description,
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
                                            Text("Tenggat: ${task.deadline}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                        }
                                        Button(
                                            onClick = {
                                                selectedTask = task
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
                            Text(selectedTask!!.deadline, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(text = selectedTask!!.title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
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
                            .clickable { /*TODO Upload*/ }
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
                            Text("Unggah Foto Hasil Kerja", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                            Text("Ketuk untuk mengambil foto atau pilih dari galeri", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) isSheetOpen = false
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
