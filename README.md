# App-Moviles

├── data/          # Persistencia con Room (Database, DAO y Entities)
├── domain/        # Modelos de datos del negocio y reglas de validación puras
├── ui/            # Interfaz gráfica en Jetpack Compose (screens, components, nav, theme)
├── util/          # Helpers para recursos nativos (Cámara, GPS y Permisos)
├── viewmodel/     # Gestión del estado de la UI y flujos de datos (StateFlow)
├── widget/        # AppWidgetProvider para el widget de la pantalla de inicio
├── MainActivity   # Actividad principal que aloja el contenedor de navegación Jetpack Compose
└── RegistroApp    # Clase Application que inicializa la base de datos y el repositorio
