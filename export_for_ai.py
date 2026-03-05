import os

IGNORE_DIRS = {'.git', '.idea', 'target', 'postgres_data', 'docker_data', '.mvn'}
IGNORE_EXTS = {'.jar', '.class', '.ico', '.db', '.png'}
IGNORE_FILES = {'.env', 'export_for_ai.py', 'ai_context.txt'}
OUTPUT_FILE = 'ai_context.txt'

def is_ignored(file_name):
    return any(file_name.endswith(ext) for ext in IGNORE_EXTS)

with open(OUTPUT_FILE, 'w', encoding='utf-8') as out:
    for root, dirs, files in os.walk('.'):
        dirs[:] =[d for d in dirs if d not in IGNORE_DIRS]

        for file in files:
            if file in IGNORE_FILES or is_ignored(file):
                continue

            path = os.path.join(root, file)
            clean_path = path[2:] if path.startswith('./') else path

            try:
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    out.write(f"===== File: {clean_path} =====\n")
                    out.write(content)
                    out.write("\n\n")
            except UnicodeDecodeError:
                pass

print(f"Success! {OUTPUT_FILE}")