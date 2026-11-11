# Métricas — estado Final

Unidad: `LexicalAnalyzer.java` · paquete: `src/main/java/cl/edu/metrics/lexer` · generado por `scripts/metrics.py`.

## LOC

| Archivo | LOC |
| --- | ---: |
| `LexicalAnalyzer.java` | 70 |
| `LexicalReportService.java` | 18 |
| `Main.java` | 13 |
| `DefaultKeywordCatalog.java` | 9 |
| `DefaultTokenFormatter.java` | 7 |
| `KeywordCatalog.java` | 4 |
| `TokenFormatter.java` | 4 |
| `TokenType.java` | 4 |
| `Token.java` | 2 |
| **Total del paquete** | **131** |

## LCOM1

Atributos de instancia: `catalog`.
Métodos considerados (n = 2): `tokenize`, `readWord`.

| Método | A(m) |
| --- | --- |
| `tokenize` | ∅ |
| `readWord` | `catalog` |

| Par | Intersección | ¿Disjunto? |
| --- | --- | :---: |
| `tokenize` – `readWord` | ∅ | sí |

**LCOM1 = 1** (pares examinados: 1; máximo posible n(n-1)/2 = 1).

Excluidos: `readNumber` (static); `symbolType` (static); `isWhitespace` (static); `isAsciiLetter` (static); `isAsciiDigit` (static); `isIdentifierStart` (static); `isIdentifierPart` (static).

## CBO

| Tipo | Apariciones |
| --- | ---: |
| `TokenType` | 11 |
| `Token` | 7 |
| `List` | 5 |
| `String` | 5 |
| `ArrayList` | 2 |
| `KeywordCatalog` | 2 |

**CBO = 6**.

## CC por método

| Método | Línea | CC | if | for | while | case | && | \|\| | ? |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| `symbolType` | 52 | **6** | 0 | 0 | 0 | 5 | 0 | 0 | 0 |
| `tokenize` | 13 | **5** | 3 | 0 | 1 | 0 | 0 | 0 | 0 |
| `readWord` | 32 | **4** | 0 | 0 | 1 | 0 | 1 | 0 | 1 |
| `isWhitespace` | 63 | **4** | 0 | 0 | 0 | 0 | 0 | 3 | 0 |
| `isAsciiLetter` | 67 | **4** | 0 | 0 | 0 | 0 | 2 | 1 | 0 |
| `readNumber` | 43 | **3** | 0 | 0 | 1 | 0 | 1 | 0 | 0 |
| `isAsciiDigit` | 71 | **2** | 0 | 0 | 0 | 0 | 1 | 0 | 0 |
| `isIdentifierStart` | 75 | **2** | 0 | 0 | 0 | 0 | 0 | 1 | 0 |
| `isIdentifierPart` | 79 | **2** | 0 | 0 | 0 | 0 | 0 | 1 | 0 |

**CC máxima = 6 (`symbolType`); suma de CC = 32.**

