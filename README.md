# Registro de horas

Aplicación móvil Android (Kotlin + Jetpack Compose) para registrar el tiempo dedicado a cada proyecto.
Proyecto de la **Evaluación Parcial 2 – DSY1105 Desarrollo de Aplicaciones Móviles**.

## Integrantes

- Benjamin Salgado
- Jordan Moreno
- Jose Gavilan
- Martin Soto

**Equipo:** Vappnet

## Contexto

Una empresa de servicios necesita que su equipo registre cuántas horas dedica a cada proyecto. La app permite
elegir usuario y proyecto, registrar la tarea con su duración, adjuntar evidencia (foto y ubicación) y revisar
los acumulados por proyecto, también desde un widget en la pantalla de inicio.

## Funcionalidades

- **Resumen:** total acumulado y horas por proyecto con barras y contadores animados.
- **Registrar:** formulario con usuario, proyecto, fecha, tarea, horas y minutos.
  - Validación **por campo** con ícono (error / correcto) y mensaje animado bajo cada campo.
  - Reglas: usuario y proyecto obligatorios, fecha no futura, tarea de 3 a 80 caracteres,
    minutos de 0 a 59, duración total mayor que 0 y máximo 24 h.
- **Historial:** todos los registros, con foto y ubicación si existen; eliminar con confirmación.
- **Widget** "Horas por proyecto": muestra los totales y su botón abre la app en la pantalla de registro.
- **Recursos nativos (2):**
  1. **Cámara** — foto como evidencia (permiso en tiempo de ejecución + `FileProvider`).
  2. **Ubicación (GPS)** — coordenadas del lugar de trabajo (permiso en tiempo de ejecución).
- **Persistencia local:** Room (SQLite); los datos permanecen al cerrar la app.
- **Sincronización con el microservicio (Retrofit):** cada registro se envía a un backend Spring Boot; si no hay
  internet queda pendiente y se reenvía después (pantalla Inicio: pendientes, total del servidor y botón *Sincronizar ahora*).
- **API externa (Nager.Date):** al elegir la fecha, la app avisa si ese día es feriado en Chile.

## Arquitectura (MVVM)

```
com.example.registrohoras
├── MainActivity.kt / RegistroApp.kt     Punto de entrada y creación de BD/repositorio
├── data/
│   ├── local/                           Room: RegistroEntity, RegistroDao, AppDatabase (v5)
│   ├── remote/                          Retrofit: RegistroApi (backend), FeriadosApi (externa), SyncRepository
│   └── RegistroRepository.kt            Acceso a datos para los ViewModels
├── domain/
│   ├── FormularioValidator.kt           Reglas de validación (Kotlin puro, con tests)
│   └── Modelos.kt                       Usuario, Proyecto, catálogo y formatos
├── viewmodel/
│   ├── RegistroViewModel.kt             Estado del formulario, validación y guardado
│   └── HorasViewModel.kt                Totales e historial
├── ui/
│   ├── theme/                           Colores, formas, modo claro/oscuro
│   ├── components/                      Campos con validación, tarjetas reutilizables
│   ├── screens/                         Resumen, Registrar, Historial
│   └── navigation/                      NavHost + barra de navegación inferior
├── util/                                Cámara (FotoHelper), ubicación, permisos
└── widget/                              Widget de pantalla de inicio
```

La interfaz **no contiene lógica de validación**: solo dibuja el `FormularioState` que expone el ViewModel,
y las reglas viven en `FormularioValidator`.

## Backend (microservicio Spring Boot)

Carpeta `backend/` (Java 17, Spring Boot 3.3, Spring Data JPA, H2 en archivo). Endpoints:

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/health` | Comprueba que el servicio está arriba |
| GET | `/api/registros?proyectoId=` | Lista los registros (opcionalmente de un proyecto) |
| POST | `/api/registros` | Crea (201) o actualiza (200) un registro; valida los datos (400 si son inválidos) |
| DELETE | `/api/registros/{dispositivo}/{idLocal}` | Elimina (204) o 404 si no existe |
| GET | `/api/resumen` | Total de minutos y minutos por proyecto |

Ejecutar: `cd backend && mvn spring-boot:run` (o abrir la carpeta `backend` en IntelliJ y ejecutar
`RegistroHorasApplication`). Pruebas: `mvn test`. El POST es idempotente: reenviar el mismo registro no lo duplica.

## Conexión de la app con el backend

1. Levanta el backend (puerto 8080).
2. Emulador: funciona sin cambios (`http://10.0.2.2:8080/`).
3. Teléfono real por USB: `adb reverse tcp:8080 tcp:8080` y compila con `-PapiBaseUrl=http://localhost:8080/`.
4. Sin backend la app sigue funcionando; los registros quedan *pendientes* y se envían cuando haya conexión.

## Firma del APK

1. Copia `keystore.properties.example` a `keystore.properties` y completa los datos de tu llave
   (para crear una: `keytool -genkeypair -v -keystore firma/vappnet-release.jks -alias vappnet -keyalg RSA -keysize 2048 -validity 10000`).
2. `./gradlew assembleRelease` genera `app/build/outputs/apk/release/app-release.apk` ya firmado
   (o *Build > Generate Signed App Bundle / APK* en Android Studio).
3. Verificar: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`.

`keystore.properties` y el `.jks` están en `.gitignore`: la llave no se sube al repositorio.

## Cómo ejecutar

1. Android Studio Meerkat 2024.3.1 o superior, JDK 17 (el integrado sirve) y **Android SDK 35**.
2. `File > Open` y elige la carpeta del proyecto (la que contiene `settings.gradle.kts` y la carpeta `app/`).
3. Espera la sincronización de Gradle (la primera vez necesita internet).
4. Crea o usa un emulador con API 26+ **con Google Play** (la ubicación usa Google Play Services) y pulsa **Run**.
5. Para probar la ubicación en el emulador: `⋯ > Location` y define un punto.
6. Widget: mantén presionado el inicio > Widgets > "Registro de horas" > "Horas por proyecto".

## Pruebas

`./gradlew test` ejecuta las pruebas unitarias de la app (validaciones, hasher, widget y capa Retrofit con MockWebServer).
`mvn test` (en `backend/`) prueba los endpoints REST.
