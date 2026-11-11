# Evidencia de pruebas — estado inicial

Corrida real de `mvn clean test` sobre el código inicial
(`docs/evidencia/inicial/LexicalAnalyzer.java`, idéntico al commit `21591ec`)
con las pruebas ya implementadas. El fuente final se restauró inmediatamente
después. El log completo está en [`test-inicial.log`](test-inicial.log).

```text
[INFO] Compiling 9 source files with javac [debug release 17] to target/classes
[INFO] Compiling 4 source files with javac [debug release 17] to target/test-classes
...
[ERROR] Tests run: 17, Failures: 10, Errors: 0, Skipped: 0
[ERROR] BUILD FAILURE
```

| Clase | Ejecutadas | Fallos |
| --- | ---: | ---: |
| `KeywordCatalogMockTest` | 1 | 0 |
| `LexicalAnalyzerExtraTest` | 5 | 4 |
| `LexicalAnalyzerStudentTest` | 10 | 6 |
| `MockitoExampleTest` | 1 | 0 |
| **Total** | **17** | **10** |

## Fallos observados

| Prueba | Observado |
| --- | --- |
| `test03IdentifierStartingWithLet` | `KEYWORD letter@0` en vez de `IDENTIFIER` |
| `test04IdentifierWithUnderscore` | `IDENTIFIER total2@1` en vez de `_total2@0` |
| `test05MultiDigitInteger` | cuatro `INTEGER` (`2@0 0@1 2@2 6@3`) |
| `test07CompleteExpression` | `12` partido en `1@12` y `2@13` |
| `test09InvalidCharacter` | sin token `ERROR` para `@` |
| `test10ConsecutiveCalls` | devuelve `x@0` y `y@0` |
| `test12WordBoundary` | `let2`, `letx`, `letter` como `KEYWORD` |
| `test13UnderscoreAndLeadingDigit` | `a_b` partido en `a@0` y `b@2` |
| `test14ConsecutiveErrors` | lista vacía para `@@` |
| `test15NonAsciiIsError` | `café` y `ñ` aceptados como `IDENTIFIER` |

Los diez fallos coinciden con el diagnóstico de la sección 1 del informe.
`test11CaseSensitivity` pasa desde el estado inicial, igual que las pruebas
01, 02, 06 y 08.
