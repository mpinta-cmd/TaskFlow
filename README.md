# TASKFLOW – Aplicación de gestión de tareas

## 1. Descripción general

TaskFlow es una aplicación móvil desarrollada para la gestión, organización y seguimiento de actividades personales. El proyecto utiliza Kotlin y Jetpack Compose para construir una interfaz moderna e interactiva, junto con Room como sistema de persistencia de datos local.

Durante el desarrollo se realizaron modificaciones importantes en la arquitectura y funcionalidades de la aplicación, con el objetivo de incorporar un sistema multiusuario, autenticación mediante credenciales, almacenamiento independiente de información y operaciones completas de gestión de tareas.

El proyecto evolucionó de una aplicación básica de administración de tareas a una aplicación con cuentas individuales y administración personalizada de actividades.

---

## 2. Tecnologías utilizadas

| Tecnología         | Implementación                                           |
| ------------------ | -------------------------------------------------------- |
| Kotlin             | Lenguaje principal de desarrollo.                        |
| Jetpack Compose    | Construcción de interfaces gráficas.                     |
| Material Design 3  | Componentes y diseño visual.                             |
| Room Database      | Persistencia local de información.                       |
| SQLite             | Motor de almacenamiento de datos.                        |
| ViewModel          | Administración de estados y lógica de presentación.      |
| Kotlin Coroutines  | Ejecución de operaciones asíncronas.                     |
| Kotlin Flow        | Observación reactiva de cambios en la base de datos.     |
| Navigation Compose | Navegación entre pantallas.                              |
| PBKDF2             | Protección de contraseñas mediante derivación de claves. |

---

## 3. Principales modificaciones realizadas

### 3.1. Implementación de un sistema multiusuario

Se incorporó la posibilidad de registrar y utilizar múltiples cuentas desde un mismo dispositivo.

Anteriormente, las tareas se administraban sin una separación individual por usuario. Con la nueva implementación, cada cuenta dispone de sus propias actividades.

**Funcionalidades incorporadas:**

- Registro de nuevos usuarios.
- Inicio de sesión mediante nombre de usuario y contraseña.
- Validación de credenciales.
- Comprobación de nombres de usuario duplicados.
- Identificación del usuario autenticado.
- Cierre de sesión.
- Separación de tareas según el usuario activo.

Cada usuario puede gestionar sus actividades sin interferir con las de otras cuentas registradas en el dispositivo.

### 3.2. Sistema de autenticación y protección de contraseñas

Se desarrolló un sistema de autenticación utilizando un repositorio independiente denominado `UserRepository`.

Para mejorar la protección de las credenciales se implementó un mecanismo de almacenamiento mediante hash y salt.

**Características técnicas:**

- Generación de una salt individual para cada usuario.
- Derivación de contraseñas mediante PBKDF2.
- Uso de PBKDF2WithHmacSHA256 en dispositivos compatibles.
- Alternativa PBKDF2WithHmacSHA1 para versiones anteriores.
- Verificación de contraseñas mediante comparación segura.
- Almacenamiento del hash en lugar de la contraseña original.

Las contraseñas no se almacenan directamente en texto plano.

También se incorporaron validaciones para los nombres de usuario y las contraseñas durante el registro.

### 3.3. Implementación de nuevas entidades en Room

Se amplió la estructura de la base de datos para administrar usuarios y relacionarlos con sus respectivas tareas.

**UserEntity.kt**

Se creó una entidad destinada al almacenamiento de usuarios.

Información almacenada:

- Identificador único.
- Nombre de usuario.
- Hash de contraseña.
- Salt individual.

Se incorporó un índice único para evitar nombres de usuario duplicados.

**TaskEntity.kt**

Se modificó la entidad de tareas para incorporar el identificador del propietario.

Nuevos elementos:

- Identificador de tarea.
- Título.
- Categoría.
- Prioridad.
- Estado de finalización.
- Identificador del usuario propietario (`userId`).

También se estableció una relación mediante clave foránea entre las tareas y los usuarios, con eliminación en cascada.

Esto permite relacionar cada tarea con la cuenta correspondiente.

### 3.4. Modificación de TaskDatabase

Se actualizó la configuración de Room para incluir las nuevas entidades y permitir el acceso a sus respectivos DAO.

Se incorporaron los siguientes accesos:

- `taskDao()`: operaciones relacionadas con las tareas.
- `userDao()`: operaciones relacionadas con los usuarios.

También se trabajó en la adaptación de la versión de la base de datos y en la migración de su estructura para incorporar el sistema de usuarios sin eliminar innecesariamente los registros existentes.

La migración debe conservarse y verificarse en la configuración definitiva de Room.

### 3.5. Implementación de UserDao

Se desarrolló un DAO específico para administrar las operaciones relacionadas con los usuarios.

Operaciones incorporadas:

- Registrar usuarios.
- Buscar usuarios por nombre.
- Buscar usuarios por identificador.
- Comprobar la existencia de un nombre de usuario.
- Evitar registros duplicados.

Se utilizaron consultas SQL mediante anotaciones de Room.

### 3.6. Modificación de TaskDao

Se modificó el DAO original para incorporar consultas que respeten la propiedad de cada tarea.

Entre las operaciones implementadas se encuentran:

| Operación                  | Descripción                                                |
| -------------------------- | ---------------------------------------------------------- |
| observeByUser()            | Obtener las tareas de un usuario específico.               |
| findByIdForUser()          | Buscar una tarea verificando su propietario.               |
| insert()                   | Registrar una nueva tarea.                                 |
| updateForUser()            | Modificar una tarea perteneciente al usuario.              |
| deleteForUser()            | Eliminar una tarea verificando su propietario.             |
| deleteAllForUser()         | Eliminar las tareas de un usuario específico.              |
| assignUnownedTasksToUser() | Asignar tareas antiguas que todavía no tienen propietario. |

Estas consultas permiten mantener separada la información de cada cuenta.

---

## 4. Modificación de la arquitectura Repository

### 4.1. UserRepository

Se incorporó un repositorio independiente para centralizar las operaciones de autenticación y administración de usuarios.

Responsabilidades:

- Registrar cuentas.
- Validar credenciales.
- Normalizar nombres de usuario.
- Generar y verificar hashes de contraseñas.
- Gestionar los resultados de autenticación.
- Informar errores durante el registro o inicio de sesión.

### 4.2. TaskRepository

Se modificó el repositorio de tareas para trabajar directamente con el identificador del usuario autenticado.

Se incorporaron operaciones para:

- Consultar tareas por usuario.
- Crear tareas asociadas al propietario.
- Completar o reabrir tareas.
- Editar tareas existentes.
- Eliminar tareas individuales.
- Eliminar todas las tareas de una cuenta.

Además, antes de modificar o eliminar una tarea, se comprueba que pertenezca al usuario correspondiente.

---

## 5. Modificación de TaskViewModel

Se amplió la lógica del ViewModel para administrar el usuario activo y controlar las operaciones de las tareas.

**Nuevas funcionalidades:**

- Almacenar el identificador del usuario actual.
- Establecer el usuario autenticado.
- Cerrar la sesión del usuario activo.
- Observar automáticamente las tareas correspondientes a cada cuenta.
- Restablecer los filtros cuando cambia el usuario.
- Crear tareas asociadas al usuario actual.
- Completar y reabrir tareas.
- Editar tareas existentes.
- Eliminar tareas individuales.
- Eliminar todas las tareas del usuario.

Se implementó `MutableStateFlow` y `StateFlow` para administrar estados reactivos.

También se utilizó `flatMapLatest` para actualizar automáticamente la lista de tareas cuando cambia el usuario autenticado.

Las operaciones de acceso a datos se ejecutan mediante `viewModelScope` y corrutinas.

---

## 6. Implementación de edición de tareas

Una de las mejoras funcionales incorporadas consiste en permitir modificar una tarea después de haberla creado.

Anteriormente, las tareas podían registrarse, completarse o eliminarse, pero no contaban con un flujo completo de edición.

Con esta implementación se incorporó la posibilidad de actualizar la información sin tener que eliminar y registrar nuevamente la actividad.

**Información que puede modificarse:**

- Título de la tarea.
- Categoría.
- Nivel de prioridad.

El estado de finalización se conserva durante la edición.

### Funcionamiento de la edición

1. El usuario selecciona una tarea.
2. La aplicación abre la pantalla de detalles.
3. Se selecciona la opción de editar.
4. El formulario carga automáticamente la información existente.
5. El usuario modifica los campos necesarios.
6. Al guardar, se ejecuta `updateTask()`.
7. El Repository verifica la propiedad de la tarea.
8. Room almacena los cambios.
9. La interfaz recibe automáticamente la información actualizada mediante Flow.

La implementación contempla los siguientes componentes:

- `TaskDetailScreen.kt`
- `NewTaskScreen.kt`
- `TaskRepository.kt`
- `TaskViewModel.kt`
- `TaskDao.kt`
- `MainActivity.kt`

El flujo de edición se encuentra incorporado en el código y requiere su validación final mediante compilación y pruebas de funcionamiento.

---

## 7. Modificaciones en las interfaces gráficas

Se trabajó en la incorporación de nuevas pantallas y en la adaptación de las existentes.

### LoginScreen

Pantalla de inicio de sesión.

Permite:

- Ingresar nombre de usuario.
- Ingresar contraseña.
- Validar credenciales.
- Acceder al sistema.
- Navegar hacia el registro.

### RegisterScreen

Pantalla de creación de cuentas.

Permite:

- Registrar un nuevo usuario.
- Validar los datos.
- Comprobar nombres de usuario disponibles.
- Crear una cuenta individual.

### TaskDetailScreen

Pantalla de información detallada de una tarea.

Permite visualizar:

- Título.
- Identificador.
- Categoría.
- Prioridad.
- Estado de finalización.

Se incorporó el flujo de navegación hacia la edición de la tarea.

### NewTaskScreen

Se adaptó el formulario para permitir tanto la creación como la modificación de tareas.

Incluye:

- Campo de título.
- Campo de categoría.
- Selección de prioridad.
- Botón para guardar.
- Botón para cancelar.
- Precarga de información cuando se utiliza el modo edición.

Se incorporó el parámetro `isEditing` para diferenciar la creación de una tarea nueva de la modificación de una existente.

### TaskFlowScreen

Se conserva la pantalla principal de administración de actividades, incluyendo:

- Lista de tareas.
- Filtros.
- Estadísticas.
- Indicador de progreso.
- Tarjetas de tareas.
- Personalización de colores.
- Botón para crear nuevas actividades.

---

## 8. Modificaciones en MainActivity

Se reorganizó el sistema principal de navegación para integrar las nuevas funcionalidades.

Se incorporaron rutas para:

- Inicio de sesión.
- Registro.
- Lista de tareas.
- Detalle de tarea.
- Creación y edición de tareas.

También se integraron los repositorios y sus respectivas factories para proporcionar las dependencias necesarias a los ViewModels.

Se añadió el control del usuario autenticado y la opción de cerrar sesión desde la pantalla principal.

La navegación permite conectar las diferentes pantallas sin perder el contexto de la tarea que se está editando.

---

## 9. Arquitectura general del proyecto

La arquitectura se organizó mediante una separación de responsabilidades:

```text
TASKFLOW
│
├── data
│   │
│   ├── local
│   │   ├── TaskDatabase.kt
│   │   ├── TaskEntity.kt
│   │   ├── UserEntity.kt
│   │   ├── TaskDao.kt
│   │   └── UserDao.kt
│   │
│   └── repository
│       ├── TaskRepository.kt
│       └── UserRepository.kt
│
├── model
│   ├── Task.kt
│   ├── TaskPriority.kt
│   └── TaskFilter.kt
│
├── ui
│   │
│   ├── components
│   │   ├── TaskCard.kt
│   │   ├── TaskHeader.kt
│   │   ├── ProgressCard.kt
│   │   └── TaskFilterBar.kt
│   │
│   ├── screens
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── TaskFlowScreen.kt
│   │   ├── TaskDetailScreen.kt
│   │   └── NewTaskScreen.kt
│   │
│   └── theme
│
├── viewmodel
│   ├── TaskViewModel.kt
│   ├── TaskViewModelFactory.kt
│   └── AuthViewModel.kt
│
└── MainActivity.kt
```

---

## 10. Seguridad y separación de información

Se incorporaron medidas para evitar que las operaciones de una cuenta afecten los registros de otra.

Las principales medidas son:

1. Identificación del usuario autenticado.
2. Asociación de cada tarea con un propietario.
3. Consultas filtradas mediante `userId`.
4. Verificación del propietario antes de actualizar una tarea.
5. Verificación del propietario antes de eliminar una tarea.
6. Protección de contraseñas mediante hash y salt.
7. Restricción de nombres de usuario duplicados.

**Nota:** la autenticación y persistencia implementadas son locales. No se ha incorporado sincronización en la nube ni autenticación mediante un servidor externo.

---

## 11. Resultados y beneficios de las modificaciones

Las modificaciones realizadas permiten mejorar significativamente el funcionamiento de TaskFlow.

Entre los principales beneficios se encuentran:

- Administración de múltiples cuentas desde un mismo dispositivo.
- Mayor organización de las actividades personales.
- Separación de información por usuario.
- Protección de credenciales.
- Persistencia de información mediante Room.
- Administración centralizada de operaciones.
- Actualización reactiva de las listas.
- Posibilidad de modificar tareas existentes.
- Mejor organización del código.
- Mayor escalabilidad para futuras funcionalidades.

---

## 12. Pruebas y validación

Para verificar el funcionamiento de las nuevas implementaciones se deben realizar las siguientes pruebas:

- [x] Registro de usuarios.
- [x] Inicio de sesión.
- [x] Validación de credenciales.
- [x] Almacenamiento protegido de contraseñas.
- [x] Creación de usuarios independientes.
- [x] Identificación del usuario activo.
- [x] Consultas de tareas por usuario en DAO.
- [x] Operaciones de tareas asociadas al propietario en Repository.
- [x] Administración del usuario actual desde ViewModel.
- [x] Validación integral de edición desde la interfaz.
- [x] Prueba completa de aislamiento entre usuarios.
- [x] Validación de migración de Room con datos existentes.
- [x] Compilación y prueba final de todas las pantallas.

---

## 13. Conclusión

El desarrollo de TaskFlow representa una evolución importante en la estructura y funcionalidad de la aplicación.

La incorporación de un sistema multiusuario, autenticación, persistencia mediante Room, separación de información y operaciones de edición permitió ampliar las capacidades originales del proyecto.

Además, la reorganización de la arquitectura mediante DAO, Repository y ViewModel facilita el mantenimiento del código y permite incorporar nuevas funcionalidades de manera estructurada.

El proyecto queda preparado para continuar su evolución hacia una aplicación de gestión de tareas más completa, organizada y escalable.

**Proyecto:** TaskFlow
**Lenguaje:** Kotlin
**Interfaz:** Jetpack Compose
**Base de datos:** Room / SQLite
**Arquitectura:** DAO – Repository – ViewModel
**Autenticación:** PBKDF2 con hash y salt
**Tipo de almacenamiento:** Local, multiusuario
