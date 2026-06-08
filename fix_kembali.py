
import glob

files = glob.glob(r"c:\Users\Galih\Documents\Coding\KidsTracker\app\src\main\java\com\android\kidstracker\ui\screens\*.kt")

for file_path in files:
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    if "\\\"Kembali\\\"" in content:
        content = content.replace("\\\"Kembali\\\"", "\"Kembali\"")
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(content)
            print(f"Fixed syntax in {file_path}")

