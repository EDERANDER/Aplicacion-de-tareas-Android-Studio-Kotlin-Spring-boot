# Resumen del Proyecto para IA Asistente

Hola. Este documento describe el estado actual de la aplicación de tareas para que puedas empezar a trabajar rápidamente.

## 1. Resumen General

Esta es una aplicación de Android nativa construida con **Kotlin** y **Jetpack Compose**. Sigue una arquitectura **MVVM (Model-View-ViewModel)**. La aplicación se conecta a un backend de **Spring Boot** para gestionar usuarios y sus tareas.

El flujo principal es:
1.  El usuario se registra o inicia sesión.
2.  Tras un login exitoso, los datos del usuario (incluyendo su ID único) se guardan localmente.
3.  La aplicación utiliza este ID guardado para todas las operaciones futuras (Crear, Leer, Actualizar, Eliminar tareas) para ese usuario.

## 2. Estructura del Proyecto

Los archivos clave se encuentran en `app/src/main/java/com/unap/aplicaciontareasfinal/`:

-   `data/`: Contiene las `data class` de Kotlin que modelan los datos de la aplicación.
    -   `User.kt`: Modelo del usuario.
    -   `Task.kt`: Modelo de una tarea.
    -   `LoginRequest.kt`, `RegisterRequest.kt`: DTOs para las peticiones a la API.
    -   `TaskCreateRequest.kt`, `TaskUpdateRequest.kt`: DTOs para crear/actualizar tareas.

-   `datastore/`: Gestiona el almacenamiento de datos locales.
    -   `UserDataStore.kt`: Utiliza **Jetpack DataStore** para guardar de forma persistente los datos del usuario logueado, principalmente el `USER_ID`. Este es el "local storage" de la aplicación.

-   `network/`: Se encarga de la comunicación con el backend.
    -   Utiliza la librería **Ktor Client**.
    -   `UserService.kt`: Contiene la lógica para el login y registro de usuarios.
    -   `TaskService.kt`: Contiene la lógica para el CRUD (Crear, Leer, Actualizar, Eliminar) de las tareas.

-   `viewmodel/`: Contiene los `ViewModels` que exponen los datos a la UI y manejan la lógica de negocio.
    -   `LoginViewModel.kt`: Maneja el estado de la pantalla de login.
    -   `TaskViewModel.kt`: Maneja el estado de la lista de tareas, creación, actualización y eliminación.
    -   `ViewModelFactory.kt`: Una factoría singleton para inyectar las dependencias (`UserDataStore`, `UserService`, `TaskService`) en los ViewModels.

-   `ui/`: Contiene los `Composables` de la interfaz de usuario.
    -   `inicioSesion/`: Pantallas de Login, Registro, etc.
    -   `crud/`: Pantallas principales de la aplicación tras el login.
        -   `ListadoTareas.kt`: Muestra la lista de tareas.
        -   `AgregarTarea.kt`: Contiene el formulario para crear una nueva tarea.
        -   `Navegacion.kt`: Contiene el `Scaffold` principal y la lógica de navegación entre las pantallas de Tareas, Añadir Tarea y Perfil.

## 3. API del Backend

La URL base de la API es: `https://aplicacion-de-tareas-spring-boot.onrender.com/api`

**Endpoints principales:**

-   **Usuarios:**
    -   `POST /usuarios/login`: Iniciar sesión.
    -   `POST /usuarios/crearUsuario`: Registrar un nuevo usuario.

-   **Tareas:**
    -   `GET /tareas/listaTareas/{idUsuario}`: Obtener todas las tareas de un usuario.
    -   `POST /tareas/crearTarea/{idUsuario}`: Crear una nueva tarea.
    -   `PUT /tareas/actualizarTarea/{idUsuario}/{idTarea}`: Actualizar una tarea existente.
    -   `DELETE /tareas/eliminarTarea/{idUsuario}/{idTarea}`: Eliminar una tarea.

## 4. Flujo de Datos (Ejemplo: Cargar Tareas)

1.  **UI (`ListadoTareas.kt`)**: El `Composable` `TaskScreen` se inicializa.
2.  `LaunchedEffect` llama a `taskViewModel.loadTasks()`.
3.  **ViewModel (`TaskViewModel.kt`)**: La función `loadTasks()`:
    a. Obtiene el ID del usuario desde `userDataStore.getUser.first()?.id`.
    b. Llama a `taskService.getTasksForUser(userId)`.
    c. Actualiza un `StateFlow` (`_tasks`) con la lista de tareas recibida.
4.  **Service (`TaskService.kt`)**: La función `getTasksForUser()`:
    a. Realiza una petición `GET` a la API con Ktor.
    b. Deserializa la respuesta JSON a una `List<Task>`.
5.  **UI (`ListadoTareas.kt`)**: El `Composable` observa los cambios en el `StateFlow` del ViewModel y redibuja la lista de tareas.

Este mismo patrón se sigue para todas las demás interacciones (crear, actualizar, eliminar). Todo está orquestado por el `TaskViewModel`.
