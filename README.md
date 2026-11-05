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

## Arquitectura (MVVM)

```
com.example.registrohoras
├── MainActivity.kt / RegistroApp.kt     Punto de entrada y creación de BD/repositorio
├── data/
│   ├── local/                           Room: RegistroEntity, RegistroDao, AppDatabase
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

## Cómo ejecutar

1. Android Studio Meerkat 2024.3.1 o superior, JDK 17 (el integrado sirve) y **Android SDK 35**.
2. `File > Open` y elige la carpeta del proyecto (la que contiene `settings.gradle.kts`).
3. Espera la sincronización de Gradle (la primera vez necesita internet).
4. Crea o usa un emulador con API 26+ **con Google Play** (la ubicación usa Google Play Services) y pulsa **Run**.
5. Para probar la ubicación en el emulador: `⋯ > Location` y define un punto.
6. Widget: mantén presionado el inicio > Widgets > "Registro de horas" > "Horas por proyecto".

## Pruebas

`./gradlew test` ejecuta las pruebas unitarias de las validaciones (`FormularioValidatorTest`).
