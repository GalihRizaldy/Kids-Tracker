
import re

# 1. Update GuruDashboardScreen.kt
file_dash = r"C:\Users\Galih\Documents\Coding\KidsTracker\app\src\main\java\com\android\kidstracker\ui\screens\GuruDashboardScreen.kt"
with open(file_dash, "r", encoding="utf-8") as f:
    content = f.read()

# Update signature
content = content.replace(
    "fun GuruDashboardScreen(navController: NavController) {", 
    "fun GuruDashboardScreen(\n    navController: NavController,\n    onNavigateToMurid: () -> Unit = {},\n    onNavigateToForm: () -> Unit = {},\n    onNavigateToTugas: () -> Unit = {}\n) {"
)

# Replace the onClick handlers for the specific buttons. 
# They are currently empty { /*TODO*/ } in QuickActionButton calls.
# Let us replace them one by one based on title.
content = content.replace(
    "QuickActionButton(icon = Icons.Default.Groups, title = \"Manajemen Murid\", onClick = { /*TODO*/ })",
    "QuickActionButton(icon = Icons.Default.Groups, title = \"Manajemen Murid\", onClick = onNavigateToMurid)"
)
content = content.replace(
    "QuickActionButton(icon = Icons.Default.Assignment, title = \"Isi Evaluasi\", onClick = { /*TODO*/ })",
    "QuickActionButton(icon = Icons.Default.Assignment, title = \"Isi Evaluasi\", onClick = onNavigateToForm)"
)
content = content.replace(
    "QuickActionButton(icon = Icons.Default.Task, title = \"Kelola Tugas\", onClick = { /*TODO*/ })",
    "QuickActionButton(icon = Icons.Default.Task, title = \"Kelola Tugas\", onClick = onNavigateToTugas)"
)

with open(file_dash, "w", encoding="utf-8") as f:
    f.write(content)

# 2. Update the 3 sub-screens
sub_screens = [
    r"C:\Users\Galih\Documents\Coding\KidsTracker\app\src\main\java\com\android\kidstracker\ui\screens\GuruStudentManagementScreen.kt",
    r"C:\Users\Galih\Documents\Coding\KidsTracker\app\src\main\java\com\android\kidstracker\ui\screens\GuruDevelopmentFormScreen.kt",
    r"C:\Users\Galih\Documents\Coding\KidsTracker\app\src\main\java\com\android\kidstracker\ui\screens\GuruTaskAndExportScreen.kt"
]

for file_path in sub_screens:
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    screen_name = file_path.split("\\")[-1].replace(".kt", "")
    
    # Add ArrowBack import if missing
    if "import androidx.compose.material.icons.automirrored.filled.ArrowBack" not in content:
        content = content.replace("import androidx.compose.material.icons.filled.*", "import androidx.compose.material.icons.filled.*\nimport androidx.compose.material.icons.automirrored.filled.ArrowBack")
        
    # Update signature
    content = content.replace(
        f"fun {screen_name}(navController: NavController) {{",
        f"fun {screen_name}(\n    navController: NavController,\n    onNavigateBack: () -> Unit = {{}}\n) {{"
    )
    
    # Inject navigationIcon if it doesn't exist in TopAppBar
    # Currently, they have TopAppBar( title = { ... }, actions = { ... } )
    # Let's insert navigationIcon before actions
    if "navigationIcon = {" not in content and "TopAppBar(" in content:
        content = re.sub(r"(TopAppBar\(\s*title = \{[\s\S]*?\},)(\s*actions = \{)", 
            r"\1\n                navigationIcon = {\n                    IconButton(onClick = onNavigateBack) {\n                        Icon(\n                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,\n                            contentDescription = \"Kembali\",\n                            tint = MaterialTheme.colorScheme.onSurfaceVariant\n                        )\n                    }\n                },\n\2", 
            content)
    
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)

# 3. Update AppNavigation.kt
file_nav = r"C:\Users\Galih\Documents\Coding\KidsTracker\app\src\main\java\com\android\kidstracker\ui\navigation\AppNavigation.kt"
with open(file_nav, "r", encoding="utf-8") as f:
    content = f.read()

# Update GuruDashboardScreen instantiation
content = content.replace(
    "com.android.kidstracker.ui.screens.GuruDashboardScreen(navController = navController)",
    """com.android.kidstracker.ui.screens.GuruDashboardScreen(
                navController = navController,
                onNavigateToMurid = { navController.navigate(Screen.GuruStudentManagement.route) },
                onNavigateToForm = { navController.navigate(Screen.GuruDevelopmentForm.route) },
                onNavigateToTugas = { navController.navigate(Screen.GuruTaskAndExport.route) }
            )"""
)

# Update sub-screens instantiation
content = content.replace(
    "com.android.kidstracker.ui.screens.GuruStudentManagementScreen(navController = navController)",
    """com.android.kidstracker.ui.screens.GuruStudentManagementScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )"""
)
content = content.replace(
    "com.android.kidstracker.ui.screens.GuruDevelopmentFormScreen(navController = navController)",
    """com.android.kidstracker.ui.screens.GuruDevelopmentFormScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )"""
)
content = content.replace(
    "com.android.kidstracker.ui.screens.GuruTaskAndExportScreen(navController = navController)",
    """com.android.kidstracker.ui.screens.GuruTaskAndExportScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )"""
)

with open(file_nav, "w", encoding="utf-8") as f:
    f.write(content)

print("Guru navigation updated")

