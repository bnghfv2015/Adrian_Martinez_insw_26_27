# Order Management - Adrian Martinez (Introducción a la Ingeniería del Software 26/27 UE)

Aplicación de escritorio en **Java Swing + Maven** para gestionar pedidos guardados en un fichero `orders.json`. Permite **ver la lista de pedidos, buscarlos por su ID, crear pedidos nuevos, borrarlos y editar la cantidad y el descuento de sus artículos**. Sigue el patrón **MVC** (modelo, vista y controlador).

## Requisitos

- **Java 17** (JDK)
- **Maven**
- IDE: Visual Studio Code con *Extension Pack for Java*

## Manual de uso

Al abrir la aplicación aparece una única pantalla con tres zonas:

- **Arriba:** el campo *Order ID*, el botón **Search** y el botón **New order**.
- **A la izquierda:** la lista *Orders* con los ids de todos los pedidos, para ver de un vistazo cuáles hay.
- **A la derecha:** el detalle del pedido seleccionado, con los botones **Edit order** y **Delete order** debajo.

### Ver y buscar pedidos
- Haz **clic en un id de la lista** y verás el detalle a la derecha: artículos, cantidades, precios, descuentos, totales en euros y en dólares.
- También puedes **escribir un id** en *Order ID* y pulsar **Search**. Si no existe, aparece "Order not found.".

### Crear un pedido
1. Pulsa **New order**. Se abre un formulario.
2. Escribe el **id** del pedido. Tiene que ser **único**: si ya existe, la aplicación avisa y no deja guardarlo. Tampoco puede estar vacío.
3. Rellena la tabla de artículos: **nombre**, **cantidad** (entero, mínimo 1), **precio unitario** y **descuento** (de 0 a 100 %). Con **Add article** añades más filas y con **Remove article** quitas la fila seleccionada.
4. Pulsa **Save**. El pedido se guarda en el fichero json y aparece en la lista, ya seleccionado. **Cancel** cierra el formulario sin guardar.

### Editar un pedido
1. Selecciona un pedido y pulsa **Edit order**.
2. Solo se pueden cambiar la **cantidad** y el **descuento** de cada artículo. El nombre y el precio no se pueden modificar; si hay que cambiarlos, lo más sencillo es borrar el pedido y crearlo de nuevo.
3. Pulsa **Save**. Se actualiza el fichero json y el detalle muestra los totales recalculados.

### Borrar un pedido
1. Selecciona un pedido y pulsa **Delete order**.
2. Confirma con **Yes** en el aviso. El pedido desaparece de la lista y del fichero json.

Los botones **Edit order** y **Delete order** solo están activos cuando hay un pedido mostrado.

### Dónde se guardan los datos
El `orders.json` que va dentro del `.jar` solo se usa como **datos iniciales**: está dentro del ejecutable y no se puede modificar. La primera vez que se abre la aplicación se copia a:

```
<carpeta del usuario>/OrderManagement/orders.json
```

Por ejemplo `C:\Users\adrian\OrderManagement\orders.json`. Todos los cambios (crear, editar, borrar) **sobrescriben ese fichero** y se mantienen la próxima vez que se abra la aplicación. Para volver a los datos iniciales basta con borrar la carpeta `OrderManagement`.

## Estructura del proyecto

```plaintext
demo/
├── src/
│   ├── main/
│   │   ├── java/com/uem/
│   │   │   ├── Main.java                         # Arranque: carga los pedidos y crea el MVC
│   │   │   ├── controller/OrderController.java   # Buscar, crear, editar y borrar pedidos
│   │   │   ├── model/                            # Order, Article, Calculator, Searcher
│   │   │   ├── service/
│   │   │   │   ├── ExchangeRateService.java      # Tipo de cambio EUR/USD (servicio externo)
│   │   │   │   └── OrderRepository.java          # Lee y guarda el fichero orders.json
│   │   │   └── view/
│   │   │       ├── OrderView.java                # Pantalla principal
│   │   │       ├── OrderFormDialog.java          # Formulario de nuevo pedido
│   │   │       └── OrderEditDialog.java          # Formulario de edición
│   │   └── resources/
│   │       ├── orders.json                       # Pedidos iniciales
│   │       └── app.png                           # Icono de la aplicación
│   └── test/java/                                # Tests JUnit 5
└── pom.xml
```

## Compilación y ejecución

```bash
cd demo
mvn clean package
java -jar target/demo-1.2.jar
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
3. `OrderController` llama al servicio cada vez que se muestra un pedido y le pasa el resultado a `OrderView`.
4. `OrderView` muestra los totales en € y en $, junto al tipo de cambio usado y su fecha.
5. **Si no hay internet o el servicio falla**, la app no se rompe: muestra los totales en euros y un aviso de que no se ha podido obtener el valor en dólares.

Los tests (`ExchangeRateServiceTest`) prueban solo la lectura del JSON, para no depender de internet al compilar en GitHub Actions.

## Integración continua y releases

El workflow `.github/workflows/ci.yml` se ejecuta con cada push a `main`: compila con Maven, pasa los tests y, si ha cambiado la versión del `pom.xml`, crea una **release** en GitHub con el `.jar` ejecutable (`swing-app-<version>.jar`).

Flujo de trabajo:
1. Trabajar en la rama `develop`.
2. Para una versión nueva, subir `<version>` en `pom.xml`.
3. Pull Request de `develop` → `main` y merge. La release se genera sola.

## Historial de versiones

- **1.2:** lista de ids de pedidos en la pantalla inicial, crear pedidos con formulario, borrar pedidos y editar cantidad y descuento. Los pedidos se guardan en `orders.json`.
- **1.1:** icono de la aplicación, tipo de cambio EUR/USD en tiempo real y `.jar` ejecutable.
- **1.0:** lectura de pedidos desde `orders.json` y búsqueda por id con patrón MVC.