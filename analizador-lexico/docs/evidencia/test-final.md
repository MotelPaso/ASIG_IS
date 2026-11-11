# Evidencia de pruebas — estado final

Corrida real de `mvn clean test` sobre el estado final. El log completo está en
[`test-final.log`](test-final.log).

```text
[INFO] Compiling 9 source files with javac [debug release 17] to target/classes
[INFO] Compiling 4 source files with javac [debug release 17] to target/test-classes
...
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

| Clase | Ejecutadas | Fallos |
| --- | ---: | ---: |
| `KeywordCatalogMockTest` | 1 | 0 |
| `LexicalAnalyzerExtraTest` | 5 | 0 |
| `LexicalAnalyzerStudentTest` | 10 | 0 |
| `MockitoExampleTest` | 1 | 0 |
| **Total** | **17** | **0** |
