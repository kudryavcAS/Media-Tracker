import os

ALLOWED_EXTS = {'.java', '.html', '.properties'}
ALLOWED_FILES = {'pom.xml', 'Dockerfile', 'docker-compose.yml'}
IGNORE_DIRS = {'target', 'postgres_data', 'docker_data'}
OUTPUT_FILE = 'ai_context.txt'

def is_allowed(file_name):
    if file_name in ALLOWED_FILES:
        return True
    return any(file_name.endswith(ext) for ext in ALLOWED_EXTS)

with open(OUTPUT_FILE, 'w', encoding='utf-8') as out:
    for root, dirs, files in os.walk('.'):
        dirs[:] =[d for d in dirs if not d.startswith('.') and d not in IGNORE_DIRS]

        for file in files:
            if not is_allowed(file):
                continue

            path = os.path.join(root, file)
            clean_path = path[2:] if path.startswith('./') else path

            try:
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    out.write(f"===== File: {clean_path} =====\n")
                    out.write(content)
                    out.write("\n\n")
            except Exception as e:
                print(f"Ошибка чтения {clean_path}: {e}")

print(f"Готово! Собраны только нужные файлы в {OUTPUT_FILE}")