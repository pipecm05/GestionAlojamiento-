GestionAlojamiento – Aparta-Suites UQ
Sistema de escritorio para apoyar la administración de un establecimiento de alojamiento. El proyecto permite gestionar apartamentos, huéspedes, reservas, personal, turnos, tarifas, folios y otros procesos operativos mediante una interfaz gráfica desarrollada con JavaFX.
Proyecto académico de la Universidad del Quindío – Ingeniería de Sistemas y Computación.

Tabla de contenido
- Descripción
- Funcionalidades
- Tecnologías
- Requisitos
- Instalación y ejecución
- Acceso de demostración
- Estructura del proyecto
- Estructuras de datos implementadas
- Pruebas
- Consideraciones
Descripción
GestionAlojamiento es una aplicación Java de escritorio orientada a la gestión de Aparta-Suites UQ. Cuenta con acceso para administradores y huéspedes, y reúne en una misma aplicación diferentes procesos relacionados con la operación del alojamiento.

La aplicación utiliza JavaFX para la interfaz gráfica y FXML para definir las vistas. La lógica del negocio se organiza en servicios, mientras que la información se representa mediante clases del modelo y se administra utilizando estructuras de datos propias implementadas en Java.
Funcionalidades
Acceso y usuarios
- Inicio de sesión.
- Registro de cuentas de huéspedes.
- Manejo de roles de administrador y huésped.
- Gestión de la sesión activa.
Administración del alojamiento
- Registro, consulta y administración de apartamentos.
- Registro y consulta de huéspedes.
- Consulta de disponibilidad mediante calendario.
- Administración del estado de los apartamentos.
Reservas
- Registro de reservas individuales.
- Registro de reservas grupales y asignación de huéspedes a apartamentos.
- Consulta de disponibilidad y validación de fechas.
- Confirmación, cancelación y seguimiento del estado de las reservas.
- Solicitudes de cancelación realizadas por huéspedes y gestión de su aprobación o rechazo.
- Registro de check-in y check-out.
Operación y servicios
- Gestión de solicitudes recibidas por un canal externo simulado, procesadas mediante una cola FIFO.
- Gestión de preparación de apartamentos, incluyendo inicio y finalización de la preparación.
- Administración de empleados y turnos.
- Configuración de temporadas y cálculo de tarifas.
- Gestión de favoritos de huéspedes.
- Administración de folios, cargos, pagos e historial de movimientos.
- Visualización de ubicaciones y consulta de rutas dentro del complejo mediante un grafo.
- Consulta de reportes administrativos.
Tecnologías
Tecnología	Uso
Java 21	Lenguaje y plataforma de desarrollo
JavaFX 21.0.6	Interfaz gráfica de escritorio
FXML	Definición de vistas
Maven	Gestión de dependencias, compilación y ejecución
JUnit 5.11.4	Pruebas unitarias


Requisitos
Antes de ejecutar el proyecto, asegúrate de tener instalado:
- JDK 21 o una versión compatible con la configuración del proyecto.
- Apache Maven (se recomienda Maven 3.9 o superior).
- Un IDE compatible con Java, como IntelliJ IDEA, Eclipse o Visual Studio Code (opcional).
Comprueba las instalaciones desde una terminal:
java -version
mvn -version
Instalación y ejecución
1. Clona el repositorio o descarga el proyecto y descomprímelo.
git clone <URL_DEL_REPOSITORIO>
cd GestionAlojamiento
Si descargaste el ZIP, entra desde la terminal a la carpeta que contiene el archivo pom.xml.
2. Compila el proyecto:
mvn clean compile
3. Ejecuta la aplicación JavaFX:
mvn javafx:run
También puedes abrir el proyecto desde tu IDE como un proyecto Maven y ejecutar la clase principal:
co.edu.uniquindio.gestionalojamiento.MainApp
Acceso de demostración
La implementación actual incluye una cuenta administrativa de demostración:
Campo	Valor
Correo	1@gmail.com
Contraseña	123
Rol	Administrador


Los huéspedes pueden crear su propia cuenta desde la opción Crear cuenta de huésped en la pantalla de inicio de sesión.
Estas credenciales están definidas para fines de demostración académica. No deben utilizarse como credenciales de un sistema desplegado en producción.

Estructura del proyecto
El código fuente principal se encuentra en:
src/
├── main/
│   ├── java/
│   │   └── co/edu/uniquindio/gestionalojamiento/
│   │       ├── MainApp.java
│   │       ├── ContextoAplicacion.java
│   │       ├── controladores/
│   │       ├── datos/
│   │       ├── enums/
│   │       ├── estructuras/
│   │       ├── excepciones/
│   │       ├── modelo/
│   │       └── servicios/
│   └── resources/
│       └── vistas/          # Interfaces FXML
└── test/
    └── java/                # Pruebas unitarias
Responsabilidad de los paquetes
- controladores: conectan las vistas JavaFX con la lógica de la aplicación.
- datos: contiene la carga de información de prueba.
- enums: define estados, roles, canales y tipos utilizados por el sistema.
- estructuras: implementa las estructuras de datos propias y sus clases auxiliares.
- excepciones: contiene excepciones específicas del dominio.
- modelo: representa las entidades y objetos del negocio.
- servicios: implementa las operaciones y reglas de negocio.
- resources/vistas: contiene las interfaces declaradas mediante FXML.
Estructuras de datos implementadas
El proyecto implementa estructuras de datos propias para administrar la información y resolver diferentes necesidades del sistema:
- Árbol binario de búsqueda.
- Cola.
- Cola de prioridad.
- Pila.
- Lista doblemente enlazada.
- Lista circular.
- Conjunto.
- Grafo con vértices y aristas.
- Árbol de intervalos para apoyar la búsqueda de conflictos entre reservas.
También se incluyen nodos, iteradores y comparadores utilizados por estas implementaciones.
Pruebas
Para ejecutar las pruebas unitarias con JUnit 5:
mvn test
Para compilar y ejecutar las pruebas desde cero:
mvn clean test
Consideraciones
- La aplicación carga registros ficticios de demostración mediante DatosPrueba al inicializar el contexto de la aplicación.
- Los servicios mantienen los datos en memoria durante la ejecución. En la implementación revisada no se configura una base de datos ni persistencia permanente; por tanto, los cambios realizados durante una ejecución no deben asumirse guardados al cerrar la aplicación.
- El canal externo corresponde a una simulación de recepción y procesamiento de solicitudes, no a una integración comprobada con una plataforma externa real.
- El mapa y las rutas representan ubicaciones del complejo y permiten consultar recorridos mediante la estructura de grafo.
GestionAlojamiento – Aparta-Suites UQ
Proyecto académico – Universidad del Quindío