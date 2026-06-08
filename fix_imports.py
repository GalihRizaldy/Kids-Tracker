
import os
import glob

files = glob.glob(r"c:\Users\Galih\Documents\Coding\KidsTracker\app\src\main\java\com\android\kidstracker\ui\screens\*.kt")

for file_path in files:
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    modified = False
    if "import androidx.navigation.NavController" not in content:
        # Add imports right after package
        content = content.replace("package com.android.kidstracker.ui.screens", "package com.android.kidstracker.ui.screens\n\nimport androidx.navigation.NavController\nimport androidx.navigation.compose.rememberNavController")
        modified = True

    if modified:
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(content)
            print(f"Fixed imports in {os.path.basename(file_path)}")

