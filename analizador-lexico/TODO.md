# TODO — orden de ejecución

Un paso a la vez. Cada paso se detiene para aprobación antes de continuar.
Los cambios de producción y pruebas están descritos en `PLAN.md`.

**Convención de commits:** un commit por paso, solo con los archivos del paso.
No se incluyen `docs/GUIA_ESTUDIANTES.md` ni `docs/PLANTILLA_INFORME.md`
(modificados por el usuario).

**Nota:** los pasos 2–12 se consolidaron en dos commits (`20cea95` y `6eadf8e`).
El paso 15 regeneró la evidencia roja real y corrigió el informe tras la revisión.

---

## Paso 0 — Documentos de trabajo

- [x] Crear `PLAN.md` (cambios propuestos).
- [x] Crear `TODO.md` (este archivo).

---

## Paso 1 — Congelar estado inicial y medir métricas base

- [x] Copiar el estado inicial:
  ```bash
  mkdir -p docs/evidencia/inicial scripts
  cp src/main/java/cl/edu/metrics/lexer/LexicalAnalyzer.java \
     docs/evidencia/inicial/LexicalAnalyzer.java
  ```
- [x] Escribir `scripts/metrics.py` (LOC, LCOM1, CBO, CC con reglas declaradas).
- [x] Ejecutar y guardar:
  ```bash
  python3 scripts/metrics.py \
    --package src/main/java/cl/edu/metrics/lexer \
    --unit LexicalAnalyzer.java \
    --label Inicial > docs/evidencia/metricas-inicial.md
  ```
- [x] Verificar contra lo esperado: LOC 68, LOC paquete 129, LCOM1 6, CBO 7,
      CC máx. 11 (`tokenize`), suma 21.
- [x] Commit: `chore: congelar estado inicial y métricas base`.

**Resultado:** `metricas-inicial.md` generado. El script corrigió el cálculo manual:
`tokenize` incluye también `if (debugEnabled)`, por lo que su CC es 11 (no 10) y la
suma es 21 (no 19). `readWord` es 5 porque su `while` usa `&&` y el ternario suma 1.

**Criterio de aceptación:** `metricas-inicial.md` coincide con los valores del
diagnóstico y queda respaldado el fuente original.

---

## Paso 2 — Arreglar el entorno de pruebas

- [x] Agregar `argLine` a `maven-surefire-plugin` en `pom.xml`.
- [x] `mvn test` sobre el estado inicial → Mockito verde; de las 10 pruebas del
      alumno pasan 4 y fallan 6. Log real en `docs/evidencia/test-inicial.log`
      (regenerado en el paso 15).
- [x] Guardar salida en `docs/evidencia/test-inicial.md`.
- [x] Commit: `build: habilitar Mockito en JDK 27`.

**Criterio de aceptación:** `mvn test` corre sin el error
`Mockito cannot mock this class`.

---

## Paso 3 — Implementar las 10 pruebas obligatorias

- [x] Reemplazar `@Ignore` + `fail(...)` por aserciones.
- [x] Agregar el auxiliar `assertTokens(List<Token>, List<Token>)`.
- [x] Validar las diez pruebas contra el contrato; todas pasan en la corrida final.
- [x] Guardar el diagnóstico inicial en `docs/evidencia/test-inicial.md`.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

**Criterio de aceptación:** los diez tests comparan salida observada con esperada
y las fallas coinciden exactamente con el diagnóstico.

---

## Paso 4 — Corregir palabra reservada exacta

- [x] Eliminar `startsWith("let")`; clasificar solo con `catalog.isKeyword`.
- [x] `mvn test` → pasa 03.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

---

## Paso 5 — Corregir identificadores ASCII y guion bajo

- [x] Predicados `isAsciiLetter` / `isIdentifierStart` / `isIdentifierPart`.
- [x] `mvn test` → pasa 04.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

---

## Paso 6 — Corregir enteros multi-dígito

- [x] `readNumber` consume todos los dígitos contiguos.
- [x] `mvn test` → pasan 05 y 07.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

---

## Paso 7 — Emitir token ERROR

- [x] El carácter no reconocido produce `ERROR` de un carácter.
- [x] `mvn test` → pasa 09.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

---

## Paso 8 — Eliminar estado compartido

- [x] `source`, `cursor` y `tokens` pasan a variables locales.
- [x] `mvn test` → pasa 10; las diez quedan verdes.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

**Criterio de aceptación:** `Tests run: 11, Failures: 0`.

---

## Paso 9 — Refactorizar: separar responsabilidades

- [x] Eliminar `debugEnabled`, `lastReport`, `enableDebug`, `debugSummary`,
      `countCharacters`.
- [x] `mvn test` → sigue verde.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

---

## Paso 10 — Refactorizar: símbolos y predicados ASCII

- [x] Extraer `symbolType(char)` con `switch` y `default ERROR`.
- [x] Extraer `isWhitespace`, `isAsciiDigit`, `isIdentifierPart`.
- [x] `mvn test` → sigue verde.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

---

## Paso 11 — Pruebas adicionales

- [x] Crear `LexicalAnalyzerExtraTest` con los 5 casos del `PLAN.md`.
- [x] `mvn test` → todo verde.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

---

## Paso 12 — Prueba con Mockito

- [x] Crear `KeywordCatalogMockTest` (stub, aserciones y `verify`).
- [x] `mvn test` → todo verde.
- [x] Commit: incluido en `fix: completar analizador y pruebas del laboratorio`.

---

## Paso 13 — Métricas finales y corrida completa

- [x] `python3 scripts/metrics.py ... --label Final > docs/evidencia/metricas-final.md`.
- [x] `mvn test` → guardar `docs/evidencia/test-final.md`.
- [x] Verificar y anotar las diferencias contra las estimaciones del `PLAN.md`.
- [x] Commit: `docs: métricas e informe final`.

---

## Paso 14 — Informe

- [x] Crear `docs/INFORME.md` a partir de la plantilla y completar las secciones 1–6.
- [x] Incluir tablas LCOM1/pares, lista CBO, CC por método y grafo simplificado.
- [x] Redactar la evaluación de calidad (350–600 palabras).
- [x] Exportar a PDF.
- [x] Commit: `docs: métricas e informe final`.

---

## Paso 15 — Correcciones de la revisión

- [x] Regenerar `test-inicial.log` con `mvn clean test` sobre el código inicial:
      17 ejecutadas, 10 fallos.
- [x] Reescribir la sección 4 con tablas LCOM1 (inicial y final), lista CBO con
      evidencia y CC por método.
- [x] Generar los grafos de flujo (`tokenize` inicial y `symbolType` final) con
      Graphviz.
- [x] Ampliar la sección 5 a 466 palabras y corregir «LOC 68 a 80» por «68 a 70».
- [x] Declarar la convención de `case` (cada etiqueta cuenta 1).
- [x] Añadir paréntesis en `isAsciiLetter`; regenerar pruebas y métricas.
- [x] Regenerar `metricas-final.md`, `test-final.log`, `test-final.md` y el PDF.
- [x] Actualizar `PLAN.md` con los resultados reales.
- [x] Commit: `docs: corregir informe y evidencia tras revisión`.

**Criterio de aceptación:** `mvn test` 17/17, informe con tablas y grafos,
sección 5 entre 350 y 600 palabras, PDF regenerado.
