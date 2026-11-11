# Evidencia de pruebas — estado inicial

El estado inicial conservaba los diez métodos de `LexicalAnalyzerStudentTest`
con `@Ignore`, por lo que Maven solo ejecutaba la prueba de ejemplo de Mockito.
La ejecución diagnóstica de los diez casos descritos en la guía produjo 4/10
comportamientos correctos: 01, 02, 06 y 08.

Los fallos observados fueron:

- 03: `letter` se clasificaba como palabra reservada por `startsWith("let")`.
- 04: el guion bajo no iniciaba identificadores.
- 05 y 07: los enteros se dividían carácter por carácter.
- 09: los caracteres no reconocidos se descartaban sin token `ERROR`.
- 10: los tokens quedaban acumulados entre llamadas.

