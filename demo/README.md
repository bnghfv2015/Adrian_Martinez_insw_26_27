# Order Management - Adrian Martinez (Introducción a la Ingeniería del Software 26/27 UE)

Aplicación de escritorio en **Java Swing + Maven** que lee pedidos desde `orders.json` y permite buscarlos por su ID. Sigue el patrón **MVC** (modelo, vista y controlador).

## Requisitos

- **Java 17** (JDK)
- **Maven**
- IDE: Visual Studio Code con *Extension Pack for Java*

## Estructura del proyecto

```plaintext
demo/
├── src/
│   ├── main/
│   │   ├── java/com/uem/
│   │   │   ├── Main.java                      # Arranque: lee orders.json y crea el MVC
│   │   │   ├── controller/OrderController.java
│   │   │   ├── model/                         # Order, Article, Calculator, Searcher
│   │   │   ├── service/ExchangeRateService.java  # Tipo de cambio EUR/USD (servicio externo)
│   │   │   └── view/OrderView.java
│   │   └── resources/
│   │       ├── orders.json                    # Pedidos
│   │       └── app.png                        # Icono de la aplicación
│   └── test/java/                             # Tests JUnit 5
└── pom.xml
```

## Compilación y ejecución

```bash
cd demo
mvn clean package
java -jar target/demo-1.1.jar
```

El `.jar` generado incluye todas las dependencias (Jackson, SLF4J), así que también se puede abrir con **doble click**.

## Icono de la aplicación

El icono está en `src/main/resources/app.png`. Al estar en `resources`, Maven lo mete dentro del `.jar`, y `OrderView` lo carga con `getClass().getResource("/app.png")` y `setIconImage(...)`. Si el fichero no existe, la app arranca igual, sin icono.

## Tipo de cambio EUR/USD

La información del pedido muestra los totales en **euros y dólares**. El tipo de cambio varía cada día, así que **no se guarda en una constante**: se pide a un servicio externo en el momento de mostrar el pedido.

**Servicio elegido: [Frankfurter](https://frankfurter.dev)**
- Es gratuito, no necesita API key ni registro.
- Publica los tipos de cambio de referencia del **Banco Central Europeo**, que se actualizan cada día laborable.

**Petición:**
```
GET https://api.frankfurter.dev/v1/latest?base=EUR&symbols=USD
```
**Respuesta:**
```json
{"amount":1.0,"base":"EUR","date":"2026-11-06","rates":{"USD":1.16}}
```

**Cómo está hecho:**
1. `ExchangeRateService` hace la petición HTTP con `java.net.http.HttpClient` (viene con Java 11+, no hace falta ninguna dependencia nueva) con un timeout de 5 segundos.
2. Lee el JSON con **Jackson** (que ya usaba el proyecto) y saca `rates.USD` y la fecha.
3. `OrderController` llama al servicio cada vez que se busca un pedido y le pasa el resultado a `OrderView`.
4. `OrderView` muestra los totales en € y en $, junto al tipo de cambio usado y su fecha.
5. **Si no hay internet o el servicio falla**, la app no se rompe: muestra los totales en euros y un aviso de que no se ha podido obtener el valor en dólares.

Los tests (`ExchangeRateServiceTest`) prueban solo la lectura del JSON, para no depender de internet al compilar en GitHub Actions.

## Integración continua y releases

El workflow `.github/workflows/ci.yml` se ejecuta con cada push a `main`: compila con Maven, pasa los tests y, si ha cambiado la versión del `pom.xml`, crea una **release** en GitHub con el `.jar` ejecutable (`swing-app-<version>.jar`).

Flujo de trabajo:
1. Trabajar en la rama `develop`.
2. Para una versión nueva, subir `<version>` en `pom.xml`.
3. Pull Request de `develop` → `main` y merge. La release se genera sola.