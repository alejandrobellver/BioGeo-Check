import os
import re

def strip_comments(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    def replacer(match):
        s = match.group(0)
        if s.startswith('//'):
            return ''
        else:
            return s
            
    pattern = re.compile(r'"(?:[^"\\]|\\.)*"|//.*')
    new_content = pattern.sub(replacer, content)
    
    # Cleanup empty lines created by comment removal
    new_content = re.sub(r'(?m)^\s*$\n?', '', new_content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Stripped comments from {filepath}")

for root, _, files in os.walk('app/src/main/java'):
    for file in files:
        if file.endswith('.kt'):
            strip_comments(os.path.join(root, file))
