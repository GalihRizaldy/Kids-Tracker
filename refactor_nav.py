
import os
import glob

files = glob.glob(r"c:\Users\Galih\Documents\Coding\KidsTracker\app\src\main\java\com\android\kidstracker\ui\screens\*.kt")

for file_path in files:
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    # 1. Add imports
    if "import androidx.navigation.NavController" not in content:
        content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport androidx.navigation.NavController\nimport androidx.navigation.compose.rememberNavController")
    
    # 2. Modify signature
    filename = os.path.basename(file_path)
    screen_name = filename.replace(".kt", "")
    
    if screen_name == "LoginScreen":
        content = content.replace("fun LoginScreen(onLoginSuccess: (String) -> Unit = {}) {", "fun LoginScreen(navController: NavController) {")
        content = content.replace("onLoginSuccess(\"ortu\")", "navController.navigate(com.android.kidstracker.ui.navigation.Screen.OrtuDashboard.route)")
        content = content.replace("onLoginSuccess(\"guru\")", "navController.navigate(com.android.kidstracker.ui.navigation.Screen.GuruDashboard.route)")
        content = content.replace("onLoginSuccess(\"admin\")", "navController.navigate(com.android.kidstracker.ui.navigation.Screen.AdminDashboard.route)")
        
        # fix preview if exists
        content = content.replace(f"{screen_name}()", f"{screen_name}(rememberNavController())")
    else:
        # Search for fun ScreenName() {
        target = f"fun {screen_name}() {{"
        replacement = f"fun {screen_name}(navController: NavController) {{"
        content = content.replace(target, replacement)
        
        # Fix preview call
        content = content.replace(f"{screen_name}()", f"{screen_name}(rememberNavController())")
    
    # Write back
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)
        
print("Updated all screens")

