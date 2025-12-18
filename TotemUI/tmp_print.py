from pathlib import Path
lines = Path('src/components/auth/LoginModal.tsx').read_text(encoding='utf8').splitlines()
for idx, line in enumerate(lines, 1):
    if 'Solicitar acesso' in line:
        for j in range(idx-3, idx+2):
            print(f"{j}: {lines[j-1]!r}")
        break
