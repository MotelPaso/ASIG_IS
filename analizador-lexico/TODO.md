# TODO — orden de ejecución

Un paso a la vez. Cada paso se detiene para aprobación antes de continuar.
Los cambios de producción y pruebas están descritos en `PLAN.md`.

**Convención de commits:** un commit por paso, solo con los archivos del paso.
No se incluyen `docs/GUIA_ESTUDIANTES.md` ni `docs/PLANTILLA_INFORME.md`
(modificados por el usuario).

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

- [ ] Agregar `argLine` a `maven-surefire-plugin` en `pom.xml`.
- [ ] `mvn test` → esperado `Tests run: 11, Failures: 0` (Mockito verde,
      6 de las 10 pruebas del alumno fallan, 4 pasan).
- [ ] Guardar salida en `docs/evidencia/test-inicial.md`.
- [ ] Commit: `build: habilitar Mockito en JDK 27`.

**Criterio de aceptación:** `mvn test` corre sin el error
`Mockito cannot mock this class`.

---

## Paso 3 — Implementar las 10 pruebas obligatorias

- [ ] Reemplazar `@Ignore` + `fail(...)` por aserciones.
- [ ] Agregar el auxiliar `assertTokens(List<Token>, List<Token>)`.
- [ ] `mvn test` → rojo esperado: fallan 03, 04, 05, 07, 09, 10.
- [ ] Guardar la salida roja (nombres de los tests que fallan) como evidencia.
- [ ] Commit: `test: implementar las diez pruebas obligatorias`.

**Criterio de aceptación:** los diez tests comparan salida observada con esperada
y las fallas coinciden exactamente con el diagnóstico.

---

## Paso 4 — Corregir palabra reservada exacta

- [ ] Eliminar `startsWith("let")`; clasificar solo con `catalog.isKeyword`.
- [ ] `mvn test` → pasa 03.
- [ ] Commit: `fix: clasificar palabras reservadas exactas (test03)`.

---

## Paso 5 — Corregir identificadores ASCII y guion bajo

- [ ] Predicados `isAsciiLetter` / `isIdentifierStart` / `isIdentifierPart`.
- [ ] `mvn test` → pasa 04.
- [ ] Commit: `fix: identificadores ASCII con guion bajo (test04)`.

---

## Paso 6 — Corregir enteros multi-dígito

- [ ] `readNumber` consume todos los dígitos contiguos.
- [ ] `mvn test` → pasan 05 y 07.
- [ ] Commit: `fix: consumir enteros completos (test05, test07)`.

---

## Paso 7 — Emitir token ERROR

- [ ] El carácter no reconocido produce `ERROR` de un carácter.
- [ ] `mvn test` → pasa 09.
- [ ] Commit: `fix: emitir token ERROR (test09)`.

---

## Paso 8 — Eliminar estado compartido

- [ ] `source`, `cursor` y `tokens` pasan a variables locales.
- [ ] `mvn test` → pasa 10; las diez quedan verdes.
- [ ] Commit: `fix: tokenize independiente entre llamadas (test10)`.

**Criterio de aceptación:** `Tests run: 11, Failures: 0`.

---

## Paso 9 — Refactorizar: separar responsabilidades

- [ ] Eliminar `debugEnabled`, `lastReport`, `enableDebug`, `debugSummary`,
      `countCharacters`.
- [ ] `mvn test` → sigue verde.
- [ ] Commit: `refactor: quitar responsabilidades ajenas al analizador`.

---

## Paso 10 — Refactorizar: símbolos y predicados ASCII

- [ ] Extraer `symbolType(char)` con `switch` y `default ERROR`.
- [ ] Extraer `isWhitespace`, `isAsciiDigit`, `isIdentifierPart`.
- [ ] `mvn test` → sigue verde.
- [ ] Commit: `refactor: extraer mapeo de símbolos y predicados ASCII`.

---

## Paso 11 — Pruebas adicionales

- [ ] Crear `LexicalAnalyzerExtraTest` con los 5 casos del `PLAN.md`.
- [ ] `mvn test` → todo verde.
- [ ] Commit: `test: agregar cinco casos de frontera`.

---

## Paso 12 — Prueba con Mockito

- [ ] Crear `KeywordCatalogMockTest` (stub, aserciones y `verify`).
- [ ] `mvn test` → todo verde.
- [ ] Commit: `test: verificar el uso de KeywordCatalog con Mockito`.

---

## Paso 13 — Métricas finales y corrida completa

- [ ] `python3 scripts/metrics.py ... --label Final > docs/evidencia/metricas-final.md`.
- [ ] `mvn test` → guardar `docs/evidencia/test-final.md`.
- [ ] Verificar y anotar las diferencias contra las estimaciones del `PLAN.md`.
- [ ] Commit: `docs: métricas y evidencia final`.

---

## Paso 14 — Informe

- [ ] Crear `docs/INFORME.md` a partir de la plantilla y completar las secciones 1–6.
- [ ] Incluir tablas LCOM1/pares, lista CBO, CC por método y grafos de flujo.
- [ ] Redactar la evaluación de calidad (350–600 palabras).
- [ ] Exportar a PDF.
- [ ] Commit: `docs: informe del laboratorio`.
