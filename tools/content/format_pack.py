"""Writes a language pack as readable, stable JSON (one diff line per change, as far as possible).

A JSON object or array is kept on one line when it fits in WIDTH characters, otherwise its items go
on their own lines. Key order is kept as found. Usage: python format_pack.py PACK.json [...]
(the files are rewritten in place).
"""

import json
import sys

WIDTH = 110
INDENT = "  "


def dump(value, level=0):
    compact = json.dumps(value, ensure_ascii=False, separators=(", ", ": "))
    if len(INDENT * level) + len(compact) <= WIDTH or not isinstance(value, (dict, list)) or not value:
        return compact
    inner = INDENT * (level + 1)
    if isinstance(value, dict):
        items = [f"{inner}{json.dumps(key, ensure_ascii=False)}: {dump(item, level + 1)}" for key, item in value.items()]
        return "{\n" + ",\n".join(items) + "\n" + INDENT * level + "}"
    items = [inner + dump(item, level + 1) for item in value]
    return "[\n" + ",\n".join(items) + "\n" + INDENT * level + "]"


def format_file(path):
    with open(path, encoding="utf-8") as file:
        pack = json.load(file)
    with open(path, "w", encoding="utf-8") as file:
        file.write(dump(pack) + "\n")


if __name__ == "__main__":
    for path in sys.argv[1:]:
        format_file(path)
