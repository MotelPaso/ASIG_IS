# Laboratorio: analizador léxico, pruebas y métricas de código

Proyecto Maven para Java 17, **JUnit 4** y **Mockito**. Compatible con IntelliJ IDEA o Visual Studio Code (extensiones Java y Maven).

**Lea primero [`docs/GUIA_ESTUDIANTES.md`](docs/GUIA_ESTUDIANTES.md).** La implementación es una **versión inicial**, deliberadamente no validada: **no se debe asumir que es correcta**.

## Estructura

```text
pom.xml
src/main/java/cl/edu/metrics/lexer/
  LexicalAnalyzer.java       # Código principal sujeto a pruebas y refactorización
  Token.java                 # Token(tipo, lexema, posición)
  TokenType.java             # Tipos de token
  KeywordCatalog.java       # Interfaz de dependencia
  DefaultKeywordCatalog.java
  TokenFormatter.java        # Interfaz, para Mockito
  DefaultTokenFormatter.java
  LexicalReportService.java
  Main.java
src/test/java/cl/edu/metrics/lexer/
  MockitoExampleTest.java          # Ejemplo completo con mock
  LexicalAnalyzerStudentTest.java  # Diez pruebas por completar

docs/
  GUIA_ESTUDIANTES.md
  PLANTILLA_INFORME.md
```

## Ejecutar

```bash
mvn test
mvn exec:java
```

La ejecución inicial de `mvn test` debe ejecutar el ejemplo con Mockito; las diez pruebas del alumno están marcadas con `@Ignore` y deben activarse una por una. **No confundir pruebas ignoradas con pruebas aprobadas.**

Sin Maven instalado en la terminal, use la ventana Maven del IDE para ejecutar `test`; las dependencias se descargan la primera vez que haya acceso a Internet.

