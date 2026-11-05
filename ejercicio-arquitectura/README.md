# Biblioteca: arquitectura de software con Java 21 + Maven

## Objetivo
Identificar y aplicar **capas lógicas**, **MVC Web**, **monolito modular** y
**publicación/suscripción local** en un sistema ejecutable.

## Requisitos
- Java JDK 21 (`java -version`).
- Apache Maven 3.9 o superior (`mvn -version`).
- IDE: IntelliJ IDEA o Visual Studio Code.
- Solo las pruebas usan dependencias externas: JUnit 4.13.2 y Mockito 5.11.0.
  Maven necesita acceder a ellas la primera vez, si no están en caché.

## Compilar, probar y ejecutar
```bash
mvn test
mvn -q -DskipTests package
mvn exec:java
```
Abrir <http://localhost:8080/>. Para otro puerto:
`mvn exec:java -Dexec.args="8090"`

## Rutas
- `GET /` o `GET /libros`: catálogo y formulario HTML.
- `GET /reservas`: reservas realizadas.
- `POST /reservas` con `libroId=1&cantidad=2`: crear reserva.
- `GET /salud`: verificación básica.

```bash
curl -i http://localhost:8080/
curl -i -X POST http://localhost:8080/reservas \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'libroId=1&cantidad=2'
curl -i http://localhost:8080/reservas
```

## Responsabilidades
- `model`: registros inmutables `Libro` y `Reserva`.
- `repository`: contratos de datos y almacenes en memoria.
- `service`: reglas de creación y disponibilidad.
- `web`: controlador HTTP + vista HTML.
- `event`: `ReservaCreada`, bus local, suscriptores.

**Importante:** Un bus en memoria **no convierte** la aplicación en microservicios,
no ofrece durabilidad ni entrega garantizada. Tampoco sustituye transacciones reales.
La persistencia desaparece al reiniciar el proceso: es una limitación deliberada del ejercicio.

## Errores HTTP esperados
- `201`: reserva creada.
- `400`: datos inválidos.
- `404`: libro/ruta inexistente.
- `409`: stock insuficiente.

## Extensión para estudiantes
Dibujar una arquitectura con servicio de catálogo y servicio de reservas
independientes. Explicar dónde estaría cada base de datos, cómo manejar
reservas concurrentes, y qué sucede si falla la red.


    ## Estado del código inicial
    La aplicación **compila** y muestra el catálogo. `POST /reservas` responde 501
    hasta completar las tareas. Las pruebas JUnit fallan deliberadamente: son
    las especificaciones ejecutables que deben satisfacer.
    **No copie** la solución docente para entregar.
