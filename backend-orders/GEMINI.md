# Project: Fullstack Java 25 AOT Springboot 4.0.1 + React VITE 7.3.1

## Description

A Develop Test for reaching a job

## Technology Versions

- Use Java 25 AOT
- Use Springboot 4.0.1
- Use VITE 7.3.1

## Architecture And Clean Code

- Use Hexagonal architecture
- Use SOLID principles
- Use Development Patterns
- Focus on DDD and TDD

## General Instructions

- When you generate new TypeScript code, follow the existing coding style.
- Ensure all new functions and classes have JSDoc comments.
- Prefer functional programming paradigms where appropriate.

## Comments

- In code coments are strictly for documentation in english

## Project Context

### Job Description (Spanish)

Propósito del Rol

    Ser una pieza clave en el desarrollo de software de una compañía global con operaciones en 36 países.
    Asumir la responsabilidad de proyectos innovadores en un equipo ágil y orientado a resultados.

Perfil Profesional Ideal

    Tecnólogo/a o profesional en Desarrollo de Software, Ingeniería de Sistemas, Informática o Ciencias de la Computación.
    Persona innovadora, responsable y curiosa por la tecnología, con pasión por el desarrollo de software.
    Nivel de inglés: B1.

Experiencia Requerida

    Backend (Java): Mínimo 3 años con Java/JEE (JPA, JDBC, Web Services), Spring Framework y Spring Boot; APIs REST.
    Frontend: Al menos 3 años con HTML, CSS, JavaScript y React.
    Pruebas: Sólidas pruebas unitarias con JUnit.
    Base de datos: Experiencia con bases relacionales y SQL (SQL Server).
    Metodologías: Trabajo en ágil (preferiblemente Scrum).
    Servicios web: Amplio conocimiento y experiencia en desarrollo de Web Services.

Condiciones del Contrato

    Tipo de contrato: Término indefinido.
    Modalidad: Presencial durante los 2 primeros meses; luego híbrido (3 días en oficina y 2 días en casa).
    Ubicación: Bogotá.
    Jornada/horario: Oficina.
    Compensación y beneficios: COP 7.500.000 mensuales (COP 6.800.000 prestacionales) + COP 700.000 por alimentos.

Responsabilidades Claves

    Diseñar, desarrollar y mantener aplicaciones con Java/Spring Boot y React.
    Implementar y consumir APIs REST y Web Services.
    Escribir y mantener pruebas unitarias (JUnit) y buenas prácticas de calidad.
    Colaborar con el equipo bajo Scrum y entregar incrementos orientados a resultados.
    Gestionar acceso y consultas a SQL Server.
    Participar en la revisión de código y en la mejora continua del producto.

Competencias Valoradas

    Desarrollo de microservicios.
    Comprensión profunda de CI/CD.
    Experiencia Full Stack (Java + React).
    Desarrollo móvil (Android e iOS).
    Patrones de diseño, refactorización y código limpio.
    Integraciones y gestión de APIs.
    AWS u otros servicios cloud.

### Technical Details (Spanish)

DESARROLLADOR JAVA Y SPRING BOOT
En el presente documento encontrará los detalles de la prueba técnica. La prueba deberá ser resuelta y
entregada en la fecha enviada a tu correo. Toda prueba entregada posterior a dicha fechano será válida.
Objetivo de la prueba:
Validar tus conocimientos y técnicas en desarrollo de aplicaciones en las tecnologías JAVA, SPRING BOOT y
REACT y despliegue en AWS. No está descrito en la prueba, pero tener en cuenta las buenas prácticas
de desarrollo (Principios SOLID, pruebas unitarias, documentación, entre otros), estilos y patrones de
arquitectura.
Descripción de la prueba: Construir una aplicación que exponga las siguientes vistas:
a) Vista Empresa con un formulario que capture la siguiente información:
• NIT (Llave primaria).
• Nombre de la empresa.
• Dirección.
• Teléfono.
b) Vista de Productos con un formulario que captura la siguiente información:
• Código.
• Nombre del producto.
• Características.
• Precio en varias monedas.
• Empresa.
c) Vista de Inicio de Sesión con un formulario que capture la información del usuario: correo y contraseña.
d) Vista de Inventario con un formulario que permita la descargar de un PDF con la información de esa
tabla y adicional utilizar alguna API de AWS para poder enviar ese PDF a un correo deseado.
e) Deben existir dos tipos de usuarios:
• Administrador: Tiene acceso a las funciones de eliminación, registro y/o edición de una Empresa.
Adicionalmente, este usuario podrá registrar productos por empresa y guardarlos en una tabla
inventario, donde se vean los productos por empresa.
• Externo: Puede visualizar las empresas como visitante.
f) El modelo entidad-relación de la base de datos para guardar la información anterior debe contener:
Empresa, Productos, Categorías, Clientes y Ordenes. Asegúrate que la Base de Datos que plantees cumpla
con los siguientes requisitos:
• Un Producto puede pertenecer a múltiples Categorías.
• Un Cliente puede tener múltiples Órdenes.
• Las ordenes pueden tener múltiples Productos.
g) La contraseña utilizada debe estar encriptada para autenticación del Usuario Administrador.
h) Publique tu aplicación en un servidor en la nube de AWS.
Entregables:
• Enlace de la aplicación desplegada en AWS.
• Usuario y contraseña de los tipos de usuario Administrador y Externo.
• Enlace del repositorio donde está almacenado el código fuente.
• Readme de todo el proyecto.
Nota:
La funcionalidad de la aplicación es totalmente abierta a tu análisis y diseño, es parte de lo que sepretende
evaluar, así que eres libre de definir la forma de capturar y generar la información. Además, utilizar las buenas
prácticas de la arquitectura de software para el desarrollo de aplicaciones web modernas.
Toda la información registrada en los formularios debe ir almacenada en una base de datos.
Cualquier duda o inquietud te puedes comunicar al WhatsApp +57 324 685 9004 indicando que tienes dudas.

