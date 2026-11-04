# Guía de laboratorio: calidad de un analizador léxico

**Asignatura:** Ingeniería de Software · **Lenguaje:** Java 17 · **Herramientas:** Maven, JUnit 4, Mockito · **Entorno:** IntelliJ IDEA o VS Code.

## 1. Contexto y objetivo

Una aplicación sencilla reconoce elementos de un minilenguaje. Se entrega una implementación inicial (`LexicalAnalyzer`) que debe ser **validada mediante pruebas automatizadas y revisada desde el punto de vista de la calidad del diseño**. Las pruebas pueden revelar defectos o funcionalidades difíciles de probar. Se permite y se espera refactorizar cuando exista una justificación.

Al finalizar deben: (a) escribir al menos diez pruebas JUnit 4; (b) usar Mockito para sustituir una dependencia; (c) corregir defectos identificados y refactorizar; (d) medir LOC, LCOM1, CBO y CC; (e) emitir un juicio de calidad fundamentado en evidencias, no solo en números.

## 2. Especificación del minilenguaje

El analizador recibe un `String` y devuelve una `List<Token>`. Cada `Token` tiene `type`, `lexeme` y `position` (índice **base cero** en el texto original). El token debe cubrir el lexema completo (coincidencia **más larga**). No se generan tokens para espacios, tabulaciones ni saltos de línea, pero estos sí influyen en `position`.

| Tipo | Regla | Ejemplo |
|---|---|---|
| `KEYWORD` | Palabras completas `let`, `print` (sensibles a mayúsculas) | `let` |
| `IDENTIFIER` | `[A-Za-z_][A-Za-z0-9_]*`, excepto las palabras reservadas exactas | `_total2` |
| `INTEGER` | `[0-9]+` (todos los dígitos contiguos forman **un** token) | `2026` |
| `ASSIGN` | `=` | `=` |
| `PLUS` | `+` | `+` |
| `LPAREN`, `RPAREN` | `(`, `)` | `(x)` |
| `SEMICOLON` | `;` | `;` |
| `ERROR` | Cada carácter no reconocido produce un token de error de un carácter | `@` |

**Alcance:** Solo caracteres ASCII para letras y dígitos; no se exigen comentarios, decimales, operadores adicionales ni análisis sintáctico. `null` queda fuera del contrato de esta práctica. Cada llamada a `tokenize` debe ser **independiente de llamadas previas**.

### Ejemplo esperado

Entrada:

```text
let total = 12 + valor;
```

Salida (tipo, lexema, posición):

```text
KEYWORD     let     0
IDENTIFIER  total   4
ASSIGN      =      10
INTEGER     12     12
PLUS        +      15
IDENTIFIER  valor  17
SEMICOLON   ;      22
```

## 3. Diez pruebas obligatorias

Se entrega la clase `LexicalAnalyzerStudentTest` con diez métodos pendientes. **Retire `@Ignore` de cada uno al implementarlo**, agregue aserciones y registre el resultado antes y después de corregir el código. Se indica el comportamiento esperado, **no el resultado de la versión defectuosa**.

| N.º | Entrada | Resultado o condición esperada |
|---|---|---|
| 1 | `""` | Lista vacía, longitud 0 |
| 2 | `"let print"` | Dos `KEYWORD`, posiciones 0 y 4 |
| 3 | `"letter"` | Un `IDENTIFIER`, lexema completo `letter` |
| 4 | `"_total2"` | Un `IDENTIFIER` en posición 0 |
| 5 | `"2026"` | Un único `INTEGER` con lexema `2026` |
| 6 | `"=+();"` | Cinco tokens de símbolos, posiciones 0,1,2,3,4 |
| 7 | `"let total = 12 + valor;"` | Los siete tokens de la tabla de ejemplo |
| 8 | `" \nprint ( x )"` | `KEYWORD print@2`, `LPAREN (@8`, `IDENTIFIER x@10`, `RPAREN )@12` |
| 9 | `"abc@5"` | `IDENTIFIER abc@0`, `ERROR @@3`, `INTEGER 5@4` |
| 10 | Primero `"x"`; después `"y"` **con el mismo objeto** | En la segunda llamada solo aparece `IDENTIFIER y@0` |

Los tests deben comparar **salida observada con salida esperada**; un `assertNotNull` aislado no es suficiente. Pueden crear funciones auxiliares de aserción para disminuir duplicación en los tests. **Se permiten pruebas adicionales** de mayúsculas, frontera entre palabras, entradas repetidas, números al comienzo y errores consecutivos.

## 4. Mockito (simple, como en la clase)

Revise `MockitoExampleTest`: usa `@Before`, `mock`, `when(...).thenReturn(...)`, `assertEquals` y `verify`. Este ejemplo ejecutable **ya está resuelto**. Su tarea adicional es crear `KeywordCatalogMockTest` para sustituir `KeywordCatalog` por un mock y verificar que la clasificación depende de su resultado, sin usar la implementación concreta de palabras reservadas.

Esqueleto orientativo:

```java
private KeywordCatalog catalog;
private LexicalAnalyzer lexer;

@Before
public void init() {
    catalog = mock(KeywordCatalog.class);
    lexer = new LexicalAnalyzer(catalog);
    when(catalog.isKeyword("custom")).thenReturn(true);
}

@Test
public void testCustomKeyword() {
    // TODO: invocar lexer, comprobar tipo y lexema;
    // TODO: verificar la consulta al catálogo con verify(...).
}
```

**Discusión:** ¿Qué se aísla con el mock? ¿Qué comprobaría una prueba con `DefaultKeywordCatalog` real y qué no comprobaría este mock? No utilice `mock` para sustituir la clase que se está probando.

## 5. Refactorización

1. Ejecute las pruebas sobre el código inicial y registre fallos concretos con el nombre del test.
2. Revise el método `tokenize` y las rutinas privadas. Proponga **al menos dos mejoras de diseño**; por ejemplo, extraer responsabilidades, hacer explícito el manejo de errores, evitar estado compartido innecesario o separar análisis y presentación.
3. Si una rama es difícil de probar, evalúe extracción de métodos o inyección de dependencias. Explique **qué cambió y por qué mejora la prueba**.
4. Corrija el comportamiento para satisfacer la especificación. Vuelva a ejecutar los tests. No se permite solucionar un fallo modificando el resultado esperado del test para adaptarlo al defecto.
5. Mantenga un registro breve de `test fallido → causa → cambio → test aprobado`.

**Nota:** No todo cambio es una mejora. La refactorización busca preservar el comportamiento correcto y mejorar estructura; la corrección de defectos cambia comportamiento incorrecto. Distinga ambos en el informe.

## 6. Métricas a calcular (antes y después)

**Unidad principal:** `LexicalAnalyzer.java`, excluyendo tests, salvo que se indique lo contrario. Reporte también LOC total del paquete de producción. Use exactamente el mismo criterio de medición en ambos estados y guarde evidencia (commit inicial y commit final o copia de referencia).

### LOC: líneas de código

Contabilice líneas **no vacías y no exclusivamente de comentario**; líneas que contienen código y comentarios cuentan como código. Incluya las llaves. Reporte el total por archivo y del paquete. Explique por qué más LOC no significa automáticamente más calidad ni productividad.

### LCOM1: falta de cohesión (variante usada en clase)

Para los métodos de instancia considerados `m1,...,mn`, defina `A(mi)` como el **conjunto de atributos de instancia de la propia clase** a los que accede directamente el método. Considere todos los pares **no ordenados** de métodos distintos, cada uno **una vez**:

`LCOM1(C) = |{(mi,mj): 1 ≤ i < j ≤ n, A(mi) ∩ A(mj) = ∅}|`.

Se excluyen **constructores y getters/setters triviales** siguiendo la convención del material; también excluya métodos `static` porque la tabla se centra en métodos de instancia. Incluya los métodos `private` y `public` de instancia no triviales. No suponga que una llamada de `m1` a `m2` implica acceso indirecto a todos los atributos de `m2`: declare y mantenga la regla de **acceso directo**. Una intersección vacía cuenta incluso si alguno de los conjuntos es vacío. Acompañe el valor con la tabla `método → atributos` y tabla de pares (máximo `n(n-1)/2`).

**Importante:** existen otras versiones LCOM con fórmulas distintas. No use `n(n-1)/2` como si fuera LCOM1: es únicamente la cantidad máxima de pares a examinar.

### CBO: acoplamiento estructural

Cuente **tipos de clases o interfaces externos a `LexicalAnalyzer`** que aparezcan como dependencia sintáctica distinta en campos, parámetros, tipos devueltos, creación, herencia, etc. Cuente **cada tipo una sola vez**, aunque aparezca muchas veces. Excluya tipos primitivos y `String` **solo si decide explícitamente esa política**; para hacer los resultados comparables en este laboratorio use la siguiente convención: **incluya `String` y tipos de la biblioteca estándar**, pero **no cuente `Object` implícito, tipos primitivos ni el propio `LexicalAnalyzer`**. Incluya los tipos usados dentro de genéricos. Registre cada tipo distinto con una evidencia concreta en el código. Comente también, como dato adicional opcional, CBO restringido a clases propias del proyecto.

### CC: complejidad ciclomática

Calcule por **método**, no solamente por clase. Para el código del laboratorio, use la regla pedagógica `CC = 1 + número de puntos de decisión`, contando cada `if`, `else if`, `for`, `while`, cada rama `case` adicional y, como acuerdo de clase, cada `&&`, `||` de condiciones. `else` no agrega uno por sí solo. Declare si existen construcciones fuera de esa regla y cómo las trató. Para el método más complejo, dibuje un **grafo de flujo de control simplificado** que justifique las bifurcaciones. El máximo de CC de los métodos y la distribución son más informativos que sumar CC de toda la clase.

> Las reglas de LCOM1 y CBO son convenciones de laboratorio para asegurar mediciones reproducibles; una herramienta puede implementar variantes diferentes.

### Tabla obligatoria de resultados

| Indicador | Inicial | Final | Interpretación |
|---|---:|---:|---|
| LOC de `LexicalAnalyzer` | ... | ... | ... |
| LOC total del paquete | ... | ... | ... |
| LCOM1 de `LexicalAnalyzer` | ... | ... | ... |
| CBO de `LexicalAnalyzer` | ... | ... | ... |
| CC máxima entre sus métodos | ... | ... | ... |
| Método con mayor CC | ... | ... | ... |
| Tests aprobados / ejecutados | ... | ... | ... |

Si reemplaza o divide la clase en varias, **no compare ingenuamente LCOM1 o CBO de clases distintas**: describa la nueva arquitectura, indique para qué clase se calculó cada valor y presente además métricas por clase. LCOM1 **depende del número de métodos**, de modo que la comparación en valores brutos puede ser engañosa.

## 7. Opinión fundamentada sobre calidad

Responda en 350–600 palabras: ¿cómo era la estructura inicial?, ¿qué defectos muestran las pruebas?, ¿qué mejora y qué empeora tras refactorizar?, ¿qué indican LOC, LCOM1, CBO y CC en este caso?, ¿qué aspectos de calidad **no** capturan estas métricas?, ¿considera mantenible, testeable y extensible la solución final? Fundamente con ejemplos de métodos y pruebas. **No afirme que un código es bueno solo porque una métrica es baja.**

## 8. Entregables y evaluación (100 puntos)

- **30 puntos:** diez tests JUnit 4 activos y con aserciones útiles (3 cada uno).
- **10 puntos:** test propio con Mockito: stub, verificación e interpretación.
- **20 puntos:** corrección de defectos y refactorización justificada, manteniendo pruebas verdes.
- **25 puntos:** LOC (5), LCOM1 con tablas de conjuntos y pares (8), CBO con lista de dependencias (6), CC por método y grafo (6).
- **15 puntos:** informe comparativo de calidad y juicio argumentado.

Entregue: proyecto Maven completo (ZIP o repositorio Git con instrucciones), fuente de tests, informe PDF o Markdown basado en `PLANTILLA_INFORME.md`, evidencia de `mvn test` y un historial o registro de cambios.

## 9. Preguntas para discusión en clase

1. ¿Cuál es la diferencia entre una prueba que falla y un defecto del diseño de la prueba?
2. ¿Por qué `@Ignore` no equivale a un test aprobado?
3. Si extraemos un método de `tokenize`, ¿necesariamente disminuye la CC total de la lógica?
4. Si `LCOM1=0`, ¿podemos concluir sin más que la clase tiene una única responsabilidad?
5. ¿Por qué agregar una interfaz para facilitar Mockito podría aumentar CBO?
