package com.example.registrohoras.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RegistroEntity::class, UsuarioEntity::class, ProyectoEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registroDao(): RegistroDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun proyectoDao(): ProyectoDao

    companion object {
        /** v1 -> v2: usuarios y proyectos pasan de ser datos fijos en el código a tablas (los registros se conservan). */
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `usuarios` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `correo` TEXT NOT NULL, `claveHash` TEXT NOT NULL, `salt` TEXT NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `proyectos` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        /** v2 -> v3: las personas agregadas con "Otro" no tienen cuenta, así que correo y contraseña pasan a ser opcionales. */
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `usuarios_nueva` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `correo` TEXT, `claveHash` TEXT, `salt` TEXT, PRIMARY KEY(`id`))")
                db.execSQL("INSERT INTO `usuarios_nueva` (`id`, `nombre`, `correo`, `claveHash`, `salt`) SELECT `id`, `nombre`, `correo`, `claveHash`, `salt` FROM `usuarios`")
                db.execSQL("DROP TABLE `usuarios`")
                db.execSQL("ALTER TABLE `usuarios_nueva` RENAME TO `usuarios`")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`)")
            }
        }

        /** v3 -> v4: perfil con nombre para mostrar y foto. */
        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `usuarios` ADD COLUMN `nombreMostrar` TEXT")
                db.execSQL("ALTER TABLE `usuarios` ADD COLUMN `fotoPath` TEXT")
            }
        }

        /** v4 -> v5: marca de sincronización con el servidor (los registros existentes quedan pendientes de enviar). */
        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `registros` ADD COLUMN `sincronizado` INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "registro_horas.db")
                .addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5)
                .build()
    }
}
