# Project Manager - Prueba de Empleabilidad

¡Bienvenido al Project Manager! Esta es una aplicación full-stack construida con Spring Boot y React que permite a los usuarios gestionar proyectos y tareas. La aplicación cuenta con un sistema de autenticación basado en roles (usuarios y administradores) y proporciona una API RESTful documentada con Swagger.

## ✨ Características Principales

- **Gestión de Usuarios**: Registro e inicio de sesión de usuarios.
- **Roles de Usuario**:
  - **USER**: Puede crear, ver, activar y eliminar sus propios proyectos y tareas.
  - **ADMIN**: Tiene acceso a un dashboard para ver y gestionar todos los usuarios y proyectos del sistema.
- **Gestión de Proyectos**: Crear proyectos, que comienzan en estado "borrador" (`DRAFT`).
- **Activación de Proyectos**: Los usuarios pueden "activar" sus proyectos, lo que los pone en estado `ACTIVE`.
- **Gestión de Tareas**: Añadir tareas a los proyectos, marcarlas como completadas y eliminarlas.
- **API RESTful Segura**: Endpoints protegidos con Spring Security y JWT.
- **Documentación de API**: Interfaz de Swagger UI para explorar y probar la API de forma interactiva.
- **Contenerización**: Totalmente dockerizada para un despliegue y ejecución sencillos.

---

## 🚀 Cómo Ejecutar la Aplicación (Método Recomendado con Docker)

La forma más sencilla y recomendada de ejecutar la aplicación es usando Docker y Docker Compose. Esto asegura que el entorno sea consistente y evita problemas de configuración local.

### Requisitos Previos

- **Docker**: Asegúrate de tener Docker y Docker Compose instalados en tu sistema.
- **Base de Datos MySQL**: Necesitas una base de datos MySQL corriendo. Puede ser en tu máquina local, en otro contenedor Docker o en un servicio en la nube.

### Pasos

1.  **Clona el Repositorio**:
    ```sh
    git clone <URL-DEL-REPOSITORIO>
    cd <NOMBRE-DEL-REPOSITORIO>
    ```

2.  **Configura la Conexión a la Base de Datos**:
    Abre el archivo `docker-compose.yml` y modifica las siguientes variables de entorno para que apunten a tu base de datos MySQL:
    ```yml
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/pe_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
      - SPRING_DATASOURCE_USERNAME=tu_usuario_de_db # <-- Cambia esto
      - SPRING_DATASOURCE_PASSWORD=tu_contraseña_de_db # <-- Cambia esto
    ```
    - **`host.docker.internal`**: Es un nombre de host especial que permite al contenedor conectarse a servicios que corren en tu máquina local (el host de Docker). Si tu base de datos corre localmente, esto debería funcionar.
    - **`pe_db`**: Asegúrate de que este sea el nombre de tu base de datos.

3.  **Construye y Ejecuta el Contenedor**:
    Abre una terminal en la raíz del proyecto y ejecuta el siguiente comando:
    ```sh
    docker compose up --build
    ```
    Este comando construirá la imagen de Docker (la primera vez puede tardar unos minutos) y luego iniciará el contenedor de la aplicación.

4.  **¡Listo!**
    La aplicación estará corriendo en `http://localhost:8080`.

---

## 💻 Cómo Usar la Aplicación

### Frontend

Una vez que la aplicación esté corriendo, abre tu navegador y ve a `http://localhost:8080`.

1.  **Registro**:
    - Haz clic en "**Regístrate**".
    - Rellena el formulario. Puedes registrarte como `USER` (usuario normal) o como `ADMIN`.
    - **Primer Usuario**: Se recomienda registrar un usuario como `ADMIN` para poder acceder al dashboard de administración.

2.  **Inicio de Sesión**:
    - Introduce tu nombre de usuario y contraseña para acceder.

3.  **Vista de Usuario (`USER`)**:
    - Verás un dashboard donde puedes crear nuevos proyectos.
    - En cada proyecto, puedes añadir tareas, marcarlas como completadas o eliminarlas.
    - Puedes "activar" un proyecto que esté en estado `DRAFT`.
    - También puedes eliminar tus proyectos.

4.  **Vista de Administrador (`ADMIN`)**:
    - Al iniciar sesión como `ADMIN`, serás redirigido a un **Admin Dashboard**.
    - En este panel, podrás ver una lista de **todos los usuarios** registrados en el sistema y **todos los proyectos** existentes.

### Swagger UI (API)

La API está documentada con Swagger, lo que te permite probar los endpoints de forma interactiva.

1.  **Accede a Swagger UI**:
    Con la aplicación corriendo, ve a la siguiente URL en tu navegador:
    [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

2.  **Autoriza tus Peticiones**:
    La mayoría de los endpoints requieren autenticación. Para poder usarlos desde Swagger, necesitas obtener un token JWT y autorizar tus peticiones.
    - **Paso 1: Obtén un Token**:
      - Ve al endpoint `POST /auth/login` en la sección `Auth`.
      - Haz clic en "**Try it out**".
      - Modifica el cuerpo de la petición con las credenciales de un usuario registrado (por ejemplo, el admin que creaste).
      - Haz clic en "**Execute**". En la respuesta, copia el valor del `accessToken`.
    - **Paso 2: Autoriza Swagger**:
      - En la parte superior derecha de la página de Swagger, haz clic en el botón "**Authorize**".
      - En la ventana que aparece, pega el token que copiaste en el campo `Value`, precedido por la palabra `Bearer ` (con un espacio al final). Ejemplo: `Bearer eyJhbGciOiJIUzI1NiJ9...`
      - Haz clic en "**Authorize**" y cierra la ventana.

3.  **Prueba los Endpoints**:
    ¡Ahora estás autenticado! Ya puedes probar cualquier endpoint que requiera permisos (los que tienen un candado 🔒). Simplemente ve al endpoint que quieras, haz clic en "**Try it out**", modifica los parámetros si es necesario y haz clic en "**Execute**".

---

## 🛠️ Ejecución Local (Sin Docker)

Si prefieres no usar Docker, puedes ejecutar el backend y el frontend por separado.

### Requisitos Previos

- **Java 17** o superior.
- **Maven** 3.8 o superior.
- Una **base de datos MySQL** corriendo localmente.

### Backend (Spring Boot)

1.  **Configura la Base de Datos**:
    Abre el archivo `src/main/resources/application.properties` y asegúrate de que las credenciales de tu base de datos sean correctas.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/pe_db
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    ```
2.  **Ejecuta la Aplicación**:
    Puedes ejecutar la aplicación desde tu IDE (como IntelliJ o VSCode) o usando Maven en la terminal:
    ```sh
    mvn spring-boot:run
    ```
    El backend estará corriendo en `http://localhost:8080`.

### Frontend (React)

El frontend es un único archivo `bundle.js` que es servido por el backend de Spring Boot, por lo que no necesitas un servidor de desarrollo de Node.js por separado. Simplemente inicia el backend y el frontend estará disponible en `http://localhost:8080`.
