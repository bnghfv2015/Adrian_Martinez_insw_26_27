# Adrian_Martinez_insw_26_27

Repositorio de Adrian Martinez - Introducción a la Ingeniería del Software 26/27 (UE).

## Order Management

Aplicación de escritorio en **Java Swing + Maven** para gestionar pedidos guardados en un fichero `orders.json`. Permite ver la lista de pedidos, buscarlos por su ID, crear pedidos nuevos, borrarlos y editar la cantidad y el descuento de sus artículos. Sigue el patrón MVC (modelo, vista y controlador).

## Documentación y manual de uso

El proyecto Maven está en la carpeta `demo/`. La documentación completa, con el **manual de uso**, la estructura del proyecto, la compilación y el funcionamiento del tipo de cambio EUR/USD, está en `demo/README.md`:

https://github.com/bnghfv2015/Adrian_Martinez_insw_26_27/blob/main/demo/README.md

## Descarga

La última versión ejecutable (`swing-app-1.2.jar`, se abre con doble click, requiere Java 17) está en la sección **Releases**:

https://github.com/bnghfv2015/Adrian_Martinez_insw_26_27/releases/latest

## Flujo de trabajo

1. Se trabaja en la rama `develop`.
2. Para una versión nueva se sube `version` en `demo/pom.xml`.
3. Pull Request de `develop` a `main` y merge. GitHub Actions compila, pasa los tests y genera la release sola.
