import os
import re

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    def replacer(match):
        comment = match.group(0)
        
        # We only want to modify the comment part after //
        prefix = comment[:2]
        text = comment[2:]
        
        # Lowercase
        text = text.lower()
        
        # Remove emojis (any character outside basic multilingual plane generally, or specifically the ones we know)
        text = re.sub(r'[\U00010000-\U0010ffff]', '', text)
        text = re.sub(r'[🚀📋👤📝🏢📜🗺️💾⏱️🔍👈🌟]', '', text)
        
        # Remove numbering like 1. , 2. 
        text = re.sub(r'^\s*\d+\.\s*', ' ', text)
        
        # Remove specific phrases
        text = text.replace('(tu código)', '')
        text = text.replace('(tu codigo)', '')
        
        # Remove 'importante:' etc
        text = text.replace('importante:', '')
        text = text.replace('nuevo:', '')
        
        # Ensure it has exactly one space after //
        text = text.strip()
        
        return prefix + ' ' + text if text else prefix

    new_content = re.sub(r'//.*', replacer, content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated {filepath}")

for root, _, files in os.walk('app/src/main/java'):
    for file in files:
        if file.endswith('.kt'):
            process_file(os.path.join(root, file))
