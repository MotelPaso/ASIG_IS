#!/usr/bin/env python3
"""Métricas reproducibles del laboratorio: LOC, LCOM1, CBO y CC.

Reglas aplicadas (declaradas en docs/INFORME.md):

- LOC: líneas no vacías y no exclusivamente de comentario. Una línea que
  contiene código y comentario cuenta como código. Se incluyen las llaves.
- LCOM1: métodos de instancia no triviales. Se excluyen constructores,
  getters/setters triviales y métodos static. A(m) es el conjunto de
  atributos de instancia de la propia clase cuyo nombre aparece en el
  cuerpo del método (acceso directo). LCOM1 = número de pares no ordenados
  de métodos distintos cuya intersección es vacía.
- CBO: tipos con inicial mayúscula seguida de minúscula que aparecen en el
  archivo, más los tipos importados. Se excluyen la propia clase y Object.
  Se incluyen String y los tipos de la biblioteca estándar.
- CC: 1 + puntos de decisión por método (if, else if, for, while, case,
  &&, || y ternario). 'else' y 'default' no suman.

El script es heurístico: no es un parser de Java. Para este laboratorio se
verifica que los conjuntos A(m), los tipos y los puntos de decisión
reportados coincidan con la lectura manual del archivo.
"""

from __future__ import annotations

import argparse
import re
from pathlib import Path

MODIFIERS = r"(?:public|private|protected|static|final|abstract|synchronized|native|default)"

METHOD_RE = re.compile(
    r"^[ \t]*(?P<mods>(?:" + MODIFIERS + r"\s+)+)"
    r"(?P<ret>[A-Za-z_][\w.<>\[\], ?]*?)\s+"
    r"(?P<name>[A-Za-z_]\w*)\s*\((?P<params>[^)]*)\)\s*"
    r"(?:throws\s+[\w.,\s]+)?\{",
    re.MULTILINE,
)

FIELD_RE = re.compile(
    r"^[ \t]*(?P<mods>(?:(?:" + MODIFIERS + r")\s+)*)"
    r"(?P<type>[A-Za-z_][\w.<>\[\], ?]*?)\s+"
    r"(?P<name>[A-Za-z_]\w*)\s*(?:=[^;{}]*)?;[ \t]*$"
)

IMPORT_RE = re.compile(r"^\s*import\s+(?:static\s+)?([\w.]+)\s*;", re.MULTILINE)
TYPE_RE = re.compile(r"\b([A-Z][a-z]\w*)\b")


def blank_comments_and_literals(text: str) -> str:
    """Reemplaza comentarios y contenido de literales por espacios.

    Conserva los saltos de línea y la longitud de cada línea para que el
    conteo de LOC y los números de línea sigan siendo válidos.
    """
    out: list[str] = []
    i, n = 0, len(text)
    in_line = in_block = in_str = in_char = False

    while i < n:
        c = text[i]
        nxt = text[i + 1] if i + 1 < n else ""

        if in_line:
            if c == "\n":
                in_line = False
                out.append(c)
            else:
                out.append(" ")
            i += 1
            continue

        if in_block:
            if c == "*" and nxt == "/":
                in_block = False
                out.append("  ")
                i += 2
            else:
                out.append("\n" if c == "\n" else " ")
                i += 1
            continue

        if in_str:
            if c == "\\" and i + 1 < n:
                out.append("  ")
                i += 2
                continue
            if c == '"':
                in_str = False
                out.append('"')
            else:
                out.append("\n" if c == "\n" else " ")
            i += 1
            continue

        if in_char:
            if c == "\\" and i + 1 < n:
                out.append("  ")
                i += 2
                continue
            if c == "'":
                in_char = False
                out.append("'")
            else:
                out.append(" ")
            i += 1
            continue

        if c == "/" and nxt == "/":
            in_line = True
            out.append("  ")
            i += 2
            continue
        if c == "/" and nxt == "*":
            in_block = True
            out.append("  ")
            i += 2
            continue
        if c == '"':
            in_str = True
            out.append('"')
            i += 1
            continue
        if c == "'":
            in_char = True
            out.append("'")
            i += 1
            continue

        out.append(c)
        i += 1

    return "".join(out)


def count_loc(stripped: str) -> int:
    return sum(1 for line in stripped.split("\n") if line.strip())


def match_brace(text: str, open_idx: int) -> int:
    depth = 0
    for i in range(open_idx, len(text)):
        if text[i] == "{":
            depth += 1
        elif text[i] == "}":
            depth -= 1
            if depth == 0:
                return i
    return -1


def line_depths(text: str) -> list[int]:
    depths: list[int] = []
    depth = 0
    for line in text.split("\n"):
        depths.append(depth)
        depth += line.count("{") - line.count("}")
    return depths


def extract_methods(stripped: str) -> list[dict]:
    methods = []
    for m in METHOD_RE.finditer(stripped):
        open_idx = m.end() - 1
        close_idx = match_brace(stripped, open_idx)
        if close_idx < 0:
            continue
        mods = m.group("mods").split()
        methods.append(
            {
                "name": m.group("name"),
                "mods": mods,
                "static": "static" in mods,
                "body": stripped[open_idx + 1 : close_idx],
                "line": stripped.count("\n", 0, m.start()) + 1,
            }
        )
    return methods


def extract_fields(stripped: str) -> list[dict]:
    depths = line_depths(stripped)
    fields = []
    for idx, line in enumerate(stripped.split("\n")):
        if depths[idx] != 1:
            continue
        m = FIELD_RE.match(line)
        if not m:
            continue
        mods = m.group("mods").split()
        fields.append(
            {
                "name": m.group("name"),
                "type": m.group("type"),
                "static": "static" in mods,
            }
        )
    return fields


def is_trivial(body: str) -> bool:
    compact = re.sub(r"\s+", "", body)
    if re.fullmatch(r"return(?:this\.)?[A-Za-z_]\w*;", compact):
        return True
    if re.fullmatch(r"(?:this\.)?[A-Za-z_]\w*=[A-Za-z_]\w*;", compact):
        return True
    return False


def cc_of(body: str) -> tuple[int, dict[str, int]]:
    counts = {
        "if": len(re.findall(r"\bif\b", body)),
        "for": len(re.findall(r"\bfor\b", body)),
        "while": len(re.findall(r"\bwhile\b", body)),
        "case": len(re.findall(r"\bcase\b", body)),
        "&&": body.count("&&"),
        "||": body.count("||"),
        "?": body.count("?"),
    }
    return 1 + sum(counts.values()), counts


def cbo_types(stripped: str, own_class: str) -> list[tuple[str, int]]:
    counts: dict[str, int] = {}
    for m in TYPE_RE.finditer(stripped):
        counts[m.group(1)] = counts.get(m.group(1), 0) + 1
    for m in IMPORT_RE.finditer(stripped):
        simple = m.group(1).split(".")[-1]
        counts.setdefault(simple, 0)
    counts.pop(own_class, None)
    counts.pop("Object", None)
    return sorted(counts.items(), key=lambda kv: (-kv[1], kv[0]))


def render(label: str, unit: Path, package: Path) -> str:
    raw = unit.read_text(encoding="utf-8")
    stripped = blank_comments_and_literals(raw)
    own_class = unit.stem

    loc_rows = []
    for path in sorted(package.glob("*.java")):
        loc_rows.append((path.name, count_loc(blank_comments_and_literals(path.read_text(encoding="utf-8")))))
    loc_rows.sort(key=lambda r: (-r[1], r[0]))
    total_loc = sum(r[1] for r in loc_rows)

    fields = extract_fields(stripped)
    instance_fields = [f["name"] for f in fields if not f["static"]]
    methods = extract_methods(stripped)

    considered = []
    excluded = []
    for m in methods:
        if m["static"]:
            excluded.append((m["name"], "static"))
        elif is_trivial(m["body"]):
            excluded.append((m["name"], "getter/setter trivial"))
        else:
            considered.append(m)

    access = {}
    for m in considered:
        access[m["name"]] = [f for f in instance_fields if re.search(r"\b" + re.escape(f) + r"\b", m["body"])]

    pairs = []
    names = [m["name"] for m in considered]
    for i in range(len(names)):
        for j in range(i + 1, len(names)):
            a = set(access[names[i]])
            b = set(access[names[j]])
            inter = a & b
            pairs.append((names[i], names[j], sorted(inter), not inter))
    lcom1 = sum(1 for p in pairs if p[3])

    cbo = cbo_types(stripped, own_class)

    cc_rows = []
    for m in methods:
        cc, counts = cc_of(m["body"])
        cc_rows.append((m["name"], m["line"], cc, counts))
    cc_rows.sort(key=lambda r: (-r[2], r[1]))
    cc_max = cc_rows[0] if cc_rows else ("-", 0, 0, {})
    cc_sum = sum(r[2] for r in cc_rows)

    out = []
    out.append(f"# Métricas — estado {label}")
    out.append("")
    out.append(f"Unidad: `{unit.name}` · paquete: `{package}` · generado por `scripts/metrics.py`.")
    out.append("")
    out.append("## LOC")
    out.append("")
    out.append("| Archivo | LOC |")
    out.append("| --- | ---: |")
    for name, loc in loc_rows:
        out.append(f"| `{name}` | {loc} |")
    out.append(f"| **Total del paquete** | **{total_loc}** |")
    out.append("")
    out.append("## LCOM1")
    out.append("")
    out.append(f"Atributos de instancia: {', '.join(f'`{f}`' for f in instance_fields) or '—'}.")
    out.append(f"Métodos considerados (n = {len(considered)}): {', '.join(f'`{m['name']}`' for m in considered)}.")
    out.append("")
    out.append("| Método | A(m) |")
    out.append("| --- | --- |")
    for m in considered:
        attrs = ", ".join(f"`{a}`" for a in access[m["name"]]) or "∅"
        out.append(f"| `{m['name']}` | {attrs} |")
    out.append("")
    out.append("| Par | Intersección | ¿Disjunto? |")
    out.append("| --- | --- | :---: |")
    for a, b, inter, disjoint in pairs:
        inter_txt = ", ".join(f"`{x}`" for x in inter) or "∅"
        out.append(f"| `{a}` – `{b}` | {inter_txt} | {'sí' if disjoint else 'no'} |")
    out.append("")
    out.append(f"**LCOM1 = {lcom1}** (pares examinados: {len(pairs)}; máximo posible n(n-1)/2 = {len(names) * (len(names) - 1) // 2}).")
    if excluded:
        out.append("")
        out.append("Excluidos: " + "; ".join(f"`{n}` ({r})" for n, r in excluded) + ".")
    out.append("")
    out.append("## CBO")
    out.append("")
    out.append("| Tipo | Apariciones |")
    out.append("| --- | ---: |")
    for name, count in cbo:
        out.append(f"| `{name}` | {count} |")
    out.append("")
    out.append(f"**CBO = {len(cbo)}**.")
    out.append("")
    out.append("## CC por método")
    out.append("")
    out.append("| Método | Línea | CC | if | for | while | case | && | \\|\\| | ? |")
    out.append("| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |")
    for name, line, cc, counts in cc_rows:
        out.append(
            f"| `{name}` | {line} | **{cc}** | {counts['if']} | {counts['for']} | {counts['while']} | "
            f"{counts['case']} | {counts['&&']} | {counts['||']} | {counts['?']} |"
        )
    out.append("")
    out.append(f"**CC máxima = {cc_max[2]} (`{cc_max[0]}`); suma de CC = {cc_sum}.**")
    out.append("")
    return "\n".join(out)


def main() -> int:
    parser = argparse.ArgumentParser(description="Métricas del laboratorio (LOC, LCOM1, CBO, CC).")
    parser.add_argument("--package", required=True, help="Directorio del paquete de producción")
    parser.add_argument("--unit", default="LexicalAnalyzer.java", help="Archivo de la unidad principal")
    parser.add_argument("--label", default="Inicial", help="Etiqueta del estado medido")
    args = parser.parse_args()

    package = Path(args.package)
    unit = package / args.unit
    if not unit.is_file():
        parser.error(f"no existe {unit}")

    print(render(args.label, unit, package))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
