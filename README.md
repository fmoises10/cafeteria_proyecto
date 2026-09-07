# Sistema de Cafetería

Sistema web para la administración de una cafetería. El proyecto permite controlar usuarios, inventario, contenedores, productos, ventas y caja.

## Tecnologías utilizadas

* Java 21
* Spring Boot
* Spring Data JPA
* SQLite
* HTML
* JavaScript
* Maven

## Funciones actuales

* Inicio de sesión de usuarios.
* Gestión de personal.
* Activación y desactivación de usuarios.
* Registro y control de inventario.
* Registro de contenedores con código de barras.
* Descuento de insumos mediante código de barras.
* Registro de los consumos realizados.
* Registro de productos y precios.
* Registro de ventas.
* Métodos de pago: efectivo, QR y tarjeta.
* Apertura y cierre de caja.
* Reportes de ventas y caja.
* Generación e impresión de tickets.

## Estructura del proyecto

El proyecto está organizado siguiendo una estructura básica de Spring Boot:

```text
src/main/java/com/example/demo/
├── controller/
├── model/
└── repository/

src/main/resources/
└── static/
```

Los modelos representan los datos del sistema, los repositorios se encargan del acceso a la base de datos y los controladores reciben las solicitudes del sistema.

## Base de datos

Se utiliza SQLite para almacenar la información del sistema.

El archivo de base de datos utilizado actualmente es:

```text
database.db
```

## Cómo ejecutar el proyecto

Clonar el repositorio y entrar a la carpeta del proyecto.

Luego ejecutar:

```powershell
.\mvnw.cmd spring-boot:run
```

Después abrir en el navegador:

```text
http://localhost:8080
```

## Estado del proyecto

Actualmente el sistema ya cuenta con los módulos principales funcionando.

Queda pendiente terminar algunos detalles, principalmente el reporte detallado de consumos, el diseño visual y las validaciones finales.

## Repositorio

El proyecto se encuentra en GitHub:

`https://github.com/fmoises10/cajeteria_proyecto`
