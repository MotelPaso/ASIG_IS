# PLAN — Analizador léxico: correcciones, pruebas y métricas

Documento de trabajo del laboratorio. Describe **qué se va a cambiar y por qué**.
El orden de ejecución paso a paso está en `TODO.md`.

**Estado de referencia:** commit `21591ec` (`LexicalAnalyzer.java` sin modificar).
**Entorno:** Maven 3.10.0 sobre JDK 27 (Temurin), compilando con `--release 17`.

---

## 1. Diagnóstico inicial

La versión entregada es una implementación deliberadamente no validada. Al ejecutar
los diez casos de la guía contra el código actual se obtiene **4/10 aprobados**:

| N.º | Entrada | Esperado | Observado | Causa |
| --- | --- | --- | --- | --- |
| 01 | `""` | lista vacía | correcto | — |
| 02 | `"let print"` | 2 KEYWORD @0,@4 | correcto | — |
| 03 | `"letter"` | IDENTIFIER `letter` | `KEYWORD letter@0` | `word.startsWith("let")` |
| 04 | `"_total2"` | IDENTIFIER @0 | `IDENTIFIER total2@1` | `_` no es `Character.isLetter` |
| 05 | `"2026"` | un INTEGER `2026` | `2@0 0@1 2@2 6@3` | `readNumber` avanza un solo carácter |
| 06 | `"=+();"` | 5 símbolos @0–4 | correcto | — |
| 07 | `"let total = 12 + valor;"` | 7 tokens | 8 tokens | mismo defecto de `readNumber` |
| 08 | `" \nprint ( x )"` | posiciones 2,8,10,12 | correcto | — |
| 09 | `"abc@5"` | IDENTIFIER, ERROR@3, INTEGER@4 | 2 tokens, sin ERROR | `else { cursor++; }` descarta el carácter |
| 10 | `"x"` y luego `"y"`, mismo objeto | solo `y@0` | `x@0` y `y@0` | `tokens` es atributo y nunca se limpia |

Violaciones adicionales de la especificación (fuera de los diez casos):

| Entrada | Observado | Especificación |
| --- | --- | --- |
| `"café"` | `IDENTIFIER café@0` | solo ASCII: `é` debe ser ERROR |
| `"ñ"` | `IDENTIFIER ñ@0` | solo ASCII: `ñ` debe ser ERROR |
| `"a_b"` | `a@0`, `b@2` | `_` es parte de identificador: un token |
| `"let2"`, `"letx"` | KEYWORD | solo palabras reservadas exactas |
| `"PRINT"` | IDENTIFIER | correcto (sensible a mayúsculas) |

---

## 2. Cambios en producción

### 2.1 `LexicalAnalyzer.java` — refactorización completa

Se aplican cinco mejoras de diseño:

1. **Eliminar estado compartido.** `source`, `cursor` y `tokens` pasan a variables
   locales de `tokenize`; cada llamada queda independiente de las anteriores.
2. **Una sola fuente de verdad para palabras reservadas.** Se elimina
   `startsWith("let")`; toda palabra se clasifica con `catalog.isKeyword(word)`.
3. **Manejo explícito de errores.** El carácter no reconocido produce un token
   `ERROR` de un carácter en vez de descartarse en silencio.
4. **Separar responsabilidades.** Se eliminan `debugEnabled`, `lastReport`,
   `enableDebug`, `debugSummary` y `countCharacters`: no participan de la
   tokenización, no están cubiertos por pruebas y la presentación ya vive en
   `LexicalReportService`.
5. **Reglas ASCII explícitas y mapeo de símbolos.** Predicados `isAsciiLetter`,
   `isAsciiDigit`, `isIdentifierStart`, `isIdentifierPart`, `isWhitespace`, y
   `symbolType(char)` como punto de extensión para nuevos símbolos.

Forma objetivo (resumen):

```java
public class LexicalAnalyzer {
    private final KeywordCatalog catalog;

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int cursor = 0;
        while (cursor < input.length()) {
            char current = input.charAt(cursor);
            if (isWhitespace(current))            cursor++;
            else if (isIdentifierStart(current))  cursor = readWord(input, cursor, tokens);
            else if (isAsciiDigit(current))       cursor = readNumber(input, cursor, tokens);
            else {
                tokens.add(new Token(symbolType(current), String.valueOf(current), cursor));
                cursor++;
            }
        }
        return tokens;
    }

    private int readWord(String input, int start, List<Token> tokens) { /* catalog.isKeyword */ }
    private static int readNumber(String input, int start, List<Token> tokens) { /* dígitos */ }
    private static TokenType symbolType(char c) { /* switch; default ERROR */ }
    private static boolean isWhitespace / isAsciiLetter / isAsciiDigit /
                          isIdentifierStart / isIdentifierPart(char c) { /* ASCII */ }
}
```

### 2.2 `pom.xml` — compatibilidad Mockito/JDK 27

Mockito 5.12.0 (ByteBuddy 1.14.15) no puede crear mocks en JDK 27:
`Mockito cannot mock this class`. Se agrega a `maven-surefire-plugin`:

```xml
<argLine>-Dnet.bytebuddy.experimental=true</argLine>
```

Verificado: con esa propiedad `MockitoExampleTest` pasa (`OK (1 test)`). Es inocua
en JDK 17, que es el `release` de compilación.

---

## 3. Cambios en pruebas

### 3.1 `LexicalAnalyzerStudentTest` (10 obligatorias)

Se reemplaza `@Ignore` + `fail("Implementar testNN")` por aserciones reales.
Se agrega un auxiliar para comparar salida observada con esperada sin duplicar
código:

```java
private static void assertTokens(List<Token> expected, List<Token> actual) {
    assertEquals(expected, actual);   // Token es un record: igualdad por valor
}
```

Las expectativas se construyen con `new Token(...)`, sin acoplarse a
`DefaultTokenFormatter`.

### 3.2 `LexicalAnalyzerExtraTest` (5 adicionales)

Clase separada para no mezclar los diez casos calificados:

| Test | Entrada | Esperado |
| --- | --- | --- |
| `test11CaseSensitivity` | `"PRINT let"` | `IDENTIFIER PRINT@0`, `KEYWORD let@6` |
| `test12WordBoundary` | `"let2 letx letter"` | tres `IDENTIFIER` @0, @5, @10 |
| `test13UnderscoreAndLeadingDigit` | `"a_b 1abc"` | `IDENTIFIER a_b@0`, `INTEGER 1@4`, `IDENTIFIER abc@5` |
| `test14ConsecutiveErrors` | `"@@"` | `ERROR @@0`, `ERROR @@1` |
| `test15NonAsciiIsError` | `"café ñ"` | `IDENTIFIER caf@0`, `ERROR é@3`, `ERROR ñ@5` |

### 3.3 `KeywordCatalogMockTest` (nueva)

- `mock(KeywordCatalog.class)`
- `when(catalog.isKeyword("custom")).thenReturn(true)`
- `when(catalog.isKeyword("other")).thenReturn(false)`
- `tokenize("custom other")` → `KEYWORD custom@0`, `IDENTIFIER other@7`
- `verify(catalog).isKeyword("custom")` y `verify(catalog).isKeyword("other")`

Discusión para el informe: el mock aísla el conocimiento de palabras reservadas y
demuestra que el analizador **consulta** la dependencia; una prueba con
`DefaultKeywordCatalog` verificaría el conjunto real de palabras, pero no el uso
de la dependencia. El test con mock no habría detectado el defecto
`startsWith("let")` porque `custom` no empieza con `let`.

---

## 4. Evidencia y métricas

### 4.1 `scripts/metrics.py`

Script único para ambos estados, con las reglas declaradas en el informe:

- **LOC:** líneas no vacías y no exclusivamente de comentario; código + comentario
  cuenta como código; se incluyen llaves.
- **LCOM1:** métodos de instancia no triviales (excluye constructores,
  getters/setters triviales y `static`); `A(m)` por búsqueda textual del atributo
  en el cuerpo; pares no ordenados con intersección vacía.
- **CBO:** tipos con inicial mayúscula seguida de minúscula usados en el archivo
  más los importados, excluyendo la propia clase y `Object`; incluye `String` y
  biblioteca estándar.
- **CC:** `1 +` puntos de decisión por método (`if`, `else if`, `for`, `while`,
  `case`, `&&`, `||`, ternario); `else` y `default` no suman.

### 4.2 `docs/evidencia/`

| Archivo | Contenido |
| --- | --- |
| `inicial/LexicalAnalyzer.java` | copia del estado inicial (respaldo del commit `21591ec`) |
| `metricas-inicial.md` | salida de `metrics.py` sobre el estado inicial |
| `metricas-final.md` | salida de `metrics.py` sobre el estado final |
| `test-inicial.md` | salida de `mvn test` antes de corregir |
| `test-final.md` | salida de `mvn test` al cierre |

### 4.3 `docs/INFORME.md`

Copia de `PLANTILLA_INFORME.md` completada (la plantilla original no se modifica):
diagnóstico, tabla de pruebas, tabla de correcciones/refactorizaciones, métricas con
tablas LCOM1 y pares, lista CBO, CC por método y grafos, evaluación de calidad de
350–600 palabras y cómo reproducir. Exportación a PDF para la entrega.

---

## 5. Métricas esperadas

Valores iniciales ya medidos; los finales son **estimaciones a verificar** con el
script, no supuestos.

| Indicador | Inicial | Final esperado |
| --- | ---: | ---: |
| LOC de `LexicalAnalyzer` | 68 | ~70–75 |
| LOC total del paquete | 129 | ~130–135 |
| LCOM1 de `LexicalAnalyzer` | 6 | 1 |
| CBO de `LexicalAnalyzer` | 7 | 6 |
| CC máxima entre métodos | 11 (`tokenize`) | 6 (`symbolType`) |
| Suma de CC | 21 | ~29 |
| Tests aprobados / ejecutados | 4/10 | 10/10 + 5 extra + Mockito |

Interpretaciones que el informe debe sostener con evidencia:

- LCOM1 = 1 es casi degenerado (n = 2) y, bajo la regla de **acceso directo**,
  `tokenize` y `readWord` son disjuntos aunque `tokenize` llame a `readWord`.
- La **suma** de CC puede subir porque los predicados ASCII agregan `&&`/`||`;
  lo que baja es el máximo, no el total.
- LOC puede no bajar: las reglas explícitas y el `switch` cuestan líneas.
- El máximo de CC final recae en un `switch` plano, cuya CC sobreestima la
  complejidad cognitiva real.

---

## 6. Decisiones y riesgos

- **Se eliminan métodos públicos** (`enableDebug`, `debugSummary`,
  `countCharacters`). Es un cambio de API, no una corrección: se clasifica como
  refactorización y se justifica por ser código no usado, no probado y ajeno a la
  responsabilidad de la clase.
- **No se modifica el valor esperado de ningún test** para acomodar un defecto.
- **No se tocan** `docs/GUIA_ESTUDIANTES.md` ni `docs/PLANTILLA_INFORME.md`, que
  tienen cambios locales del usuario.
- La primera ejecución de `mvn test` descarga `surefire 3.2.5` (requiere red una vez).
- El informe declarará "JDK 27 (Temurin) con `--release 17`" para que coincida con
  el `pom.xml` y con la plantilla.

## 7. Fuera de alcance

- Análisis sintáctico, comentarios, decimales y operadores adicionales.
- Contrato para `null` (fuera de la especificación de la práctica).
- Cambios en `DefaultKeywordCatalog`, `TokenFormatter` o `LexicalReportService`.

## 8. Resultados reales (verificados)

| Indicador | Inicial | Final real |
| --- | ---: | ---: |
| LOC de `LexicalAnalyzer` | 68 | 70 |
| LOC total del paquete | 129 | 131 |
| LCOM1 | 6 | 1 |
| CBO | 7 | 6 |
| CC máxima | 11 (`tokenize`) | 6 (`symbolType`) |
| Suma de CC | 21 | 32 |
| Tests | 4/10 (diagnóstico) | 17/17 |

Diferencias con la estimación: la suma de CC resultó 32 (se estimaban ~29)
porque los predicados ASCII usan más `&&`/`||` de los previstos; LOC quedó en 70,
dentro del rango estimado. La convención de conteo de `case` se declara en el
informe: cada etiqueta cuenta 1, de modo que `symbolType` (5 `case`) tiene CC 6.
