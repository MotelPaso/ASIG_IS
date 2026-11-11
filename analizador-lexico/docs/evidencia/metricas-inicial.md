# Métricas — estado Inicial

Unidad: `LexicalAnalyzer.java` · paquete: `src/main/java/cl/edu/metrics/lexer` · generado por `scripts/metrics.py`.

## LOC

| Archivo | LOC |
| --- | ---: |
| `LexicalAnalyzer.java` | 68 |
| `LexicalReportService.java` | 18 |
| `Main.java` | 13 |
| `DefaultKeywordCatalog.java` | 9 |
| `DefaultTokenFormatter.java` | 7 |
| `KeywordCatalog.java` | 4 |
| `TokenFormatter.java` | 4 |
| `TokenType.java` | 4 |
| `Token.java` | 2 |
| **Total del paquete** | **129** |

## LCOM1

Atributos de instancia: `catalog`, `source`, `cursor`, `tokens`, `debugEnabled`, `lastReport`.
Métodos considerados (n = 5): `tokenize`, `readWord`, `readNumber`, `debugSummary`, `countCharacters`.

| Método | A(m) |
| --- | --- |
| `tokenize` | `source`, `cursor`, `tokens`, `debugEnabled`, `lastReport` |
| `readWord` | `catalog`, `source`, `cursor`, `tokens` |
| `readNumber` | `source`, `cursor`, `tokens` |
| `debugSummary` | `debugEnabled`, `lastReport` |
| `countCharacters` | ∅ |

| Par | Intersección | ¿Disjunto? |
| --- | --- | :---: |
| `tokenize` – `readWord` | `cursor`, `source`, `tokens` | no |
| `tokenize` – `readNumber` | `cursor`, `source`, `tokens` | no |
| `tokenize` – `debugSummary` | `debugEnabled`, `lastReport` | no |
| `tokenize` – `countCharacters` | ∅ | sí |
| `readWord` – `readNumber` | `cursor`, `source`, `tokens` | no |
| `readWord` – `debugSummary` | ∅ | sí |
| `readWord` – `countCharacters` | ∅ | sí |
| `readNumber` – `debugSummary` | ∅ | sí |
| `readNumber` – `countCharacters` | ∅ | sí |
| `debugSummary` – `countCharacters` | ∅ | sí |

**LCOM1 = 6** (pares examinados: 10; máximo posible n(n-1)/2 = 10).

Excluidos: `enableDebug` (getter/setter trivial).

## CBO

| Tipo | Apariciones |
| --- | ---: |
| `Token` | 9 |
| `TokenType` | 9 |
| `String` | 6 |
| `Character` | 4 |
| `ArrayList` | 3 |
| `List` | 3 |
| `KeywordCatalog` | 2 |

**CBO = 7**.

## CC por método

| Método | Línea | CC | if | for | while | case | && | \|\| | ? |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| `tokenize` | 22 | **11** | 9 | 0 | 1 | 0 | 0 | 0 | 0 |
| `readWord` | 53 | **5** | 0 | 0 | 1 | 0 | 1 | 1 | 1 |
| `debugSummary` | 74 | **2** | 0 | 0 | 0 | 0 | 0 | 0 | 1 |
| `readNumber` | 64 | **1** | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| `enableDebug` | 70 | **1** | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| `countCharacters` | 78 | **1** | 0 | 0 | 0 | 0 | 0 | 0 | 0 |

**CC máxima = 11 (`tokenize`); suma de CC = 21.**

