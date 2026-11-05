# Vappnet - Registro de Horas (Deepsource)

Aplicación móvil nativa desarrollada en Android con Kotlin para el registro, seguimiento y gestión del tiempo dedicado a proyectos operacionales en la plataforma Deepsource.
El proyecto implementa una arquitectura moderna orientada a componentes (Jetpack Compose), persistencia de datos local (Room Database), reactividad mediante Coroutines & Flow, validaciones puras unit-tested y un AppWidget nativo interactivo para la pantalla de inicio del dispositivo.


# Integrantes / Estudiantes
## Benjamin Tomas Salgado Aliaga
## Jordan Alberto Moreno Sanchez
## Jose Ignacio Gavilan Llach
## Martin Alejandro Soto Gonzalez


# Descripción del Proyecto
Vappnet es una solución móvil diseñada para optimizar el control de horas de trabajo en proyectos de software operacionales. La aplicación permite a los usuarios registrar sesiones de trabajo asociadas a proyectos específicos, adjuntar evidencia multimedia (fotos capturadas desde la cámara) y guardar coordenadas de geolocalización mediante GPS. Además, ofrece un panel de resumen con métricas visuales acumuladas, un historial cronológico gestionable y un widget de acceso rápido para la pantalla de inicio del dispositivo.

# Funcionalidades Implementadas

## Dashboard y Resumen General:
Contador animado en tiempo real con el total general de horas acumuladas.
Desglose por proyecto con indicadores visuales de progreso (LinearProgressIndicator).
Vista previa de la actividad más reciente.

 ## Formulario de Registro con Validación Dinámica:
Selección de usuario y proyecto desde listas del catálogo.
Selector de fecha nativo de Material Design 3 (con restricción para evitar fechas futuras).
Campos de entrada de horas, minutos y descripción de la tarea.
Validación en tiempo real al perder el foco y retroalimentación visual con íconos de estado animados.

## Evidencia Multimedia Nativa:
Cámara: Captura e inclusión de fotografía del trabajo realizado mediante FileProvider.
Ubicación GPS: Obtención de coordenadas geográficas (latitud/longitud) exactas del lugar de trabajo usando FusedLocationProviderClient.
Gestión de permisos en tiempo de ejecución para cámara y ubicación.

## Historial y Gestión de Registros:
Lista cronológica interactiva de todos los registros guardados en el dispositivo.
Eliminación de registros individuales con diálogo de confirmación y eliminación automática de la fotografía asociada en el almacenamiento local.

## AppWidget Nativo para Pantalla de Inicio:
Visualización del tiempo acumulado por proyecto en el escritorio de Android.
Actualización automática en tiempo real cada vez que se crea o elimina un registro en la app.
Botón interactivo "Registrar horas" que abre directamente la pantalla de registro dentro de la aplicación.

## Pruebas Unitarias:
Suite de pruebas unitarias puras con JUnit para validar la lógica del formulario (FormularioValidatorTest).


# Arquitectura y Tecnologías
El proyecto sigue la arquitectura recomendada por Google (Clean Architecture en capas simples):


## Lenguaje: Kotlin 2.0+
## UI Layer: Jetpack Compose (UI 100% declarativa, Material Design 3, animaciones integradas).
## State Management: AndroidViewModel con StateFlow y recolección reactiva sensible al ciclo de vida (collectAsStateWithLifecycle).
## Domain Layer: Lógica de negocio y reglas de validación puras en Kotlin (desacopladas del framework de Android) con soporte para Unit Testing (JUnit).
## Data Layer: Persistencia local con Room Database (DAO, Entities y StateFlow Flow streaming).
## Carga de Imágenes: Coil para Jetpack Compose.
## Recursos Nativos: FileProvider para gestión segura de archivos e integración de ubicación con Google Play Services Location.

# Pasos para Ejecutar el Proyecto
## Requisitos Previos
## Android Studio: Versión Iguana / Ladybug o superior (o IntelliJ IDEA con plugin de Android habilitado).
## JDK: Java 17 instalado y configurado.
## Dispositivo: Dispositivo físico con Android 8.0 (API Nivel 26) o posterior con depuración USB activada, o un emulador de Android Studio (AVD).

# Pasos de Instalación y Ejecución
## Clonar o descargar el repositorio:
git clone <URL_DEL_REPOSITORIO>

## Abrir el proyecto:
Abre Android Studio (o IntelliJ IDEA).
Selecciona Open y busca la carpeta raíz del proyecto descargado.
Espera a que Gradle sincronice todas las dependencias del proyecto (Sync Project with Gradle Files).

## Ejecutar Pruebas Unitarias (Opcional):
En el panel lateral, navega a app/src/test/java/com/example/registrohoras/domain/FormularioValidatorTest.kt.
Haz clic derecho sobre el archivo y selecciona Run 'FormularioValidatorTest' (o ejecuta ./gradlew test en la terminal).

## Compilar y Ejecutar la App:
Selecciona tu emulador o dispositivo físico conectado en la barra superior.
Haz clic en el botón de reproducción verde Run 'app' (o presiona Shift + F10).
La aplicación se instalará y abrirá automáticamente en la pantalla de Resumen.

## Probar el Widget de Escritorio:
Vuelve a la pantalla de inicio del emulador/dispositivo.
Mantén presionado un espacio vacío de la pantalla de inicio y selecciona Widgets.
Busca la aplicación Vappnet y arrastra el widget Horas por proyecto a la pantalla.


# Estructura del Proyecto
### app/src/main/
### ├── java/com/example/registrohoras/
### │   ├── data/                   # Capa de persistencia local (Room DB, Entities y DAO)
### │   │   ├── local/
### │   │   │   ├── AppDatabase.kt  # Instancia principal de Room Database
### │   │   │   ├── RegistroDao.kt  # Interfaz DAO para consultas e inserciones
### │   │   │   └── RegistroEntity.kt # Tabla de registros y data classes agregadas
### │   ├── domain/                 # Lógica de negocio pura en Kotlin
### │   │   ├── FormularioValidator.kt # Validador del formulario y reglas cruzadas
### │   │   └── Modelos.kt          # Entidades de dominio, formato de horas y datos demo
### │   ├── ui/                     # Capa de interfaz gráfica en Jetpack Compose
### │   │   ├── components/         # Componentes reutilizables (Campos de entrada, RegistroCard)
### │   │   ├── navigation/         # Gráfico de navegación (AppNavigation y Destinos)
### │   │   ├── screens/            # Pantallas (ResumenScreen, RegistroScreen, HistorialScreen)
### │   │   └── theme/              # Tematización (Color, Shapes, RegistroHorasTheme)
### │   ├── util/                   # Helpers nativos del sistema
### │   │   ├── FotoHelper.kt       # Gestión segura de imágenes usando FileProvider
### │   │   ├── LocationHelper.kt   # Consultas de GPS mediante FusedLocationProviderClient
### │   │   └── PermisosHelper.kt   # Verificación de permisos en tiempo de ejecución
### │   ├── viewmodel/              # Gestores de estado y flujo de datos
### │   │   ├── HorasViewModel.kt   # Estado para pantallas Resumen e Historial
### │   │   └── RegistroViewModel.kt# Estado interactivo y validaciones del Formulario
### │   ├── widget/                 # Componente nativo de escritorio
### │   │   └── HorasWidgetProvider.kt # Provider para el AppWidget de la Home Screen
### │   ├── MainActivity.kt         # Actividad principal y manejo de Intents externos
### │   └── RegistroApp.kt          # Clase Application y contenedor global de dependencias
### ├── test/java/com/example/registrohoras/
### │   └── domain/                 # Pruebas unitarias
### │       └── FormularioValidatorTest.kt # Pruebas con JUnit para las reglas del negocio
### └── res/                        # Recursos estáticos
###     ├── drawable/               # Iconografía e imágenes vectoriales (Fondo de widget, ic_launcher)
###    ├── layout/                 # Layouts XML para el AppWidget nativo
### ├── values/                 # Estilos nativos (styles.xml) y textos (strings.xml)
###    └── xml/                    # Configuración de FileProvider y AppWidget Metadata



# Licencia
Este proyecto fue desarrollado con fines educativos para la asignatura de Desarrollo de Aplicaciones Móviles.
