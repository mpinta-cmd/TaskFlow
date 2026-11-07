
package com.example.taskflow.data.local

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [
        TaskEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)

abstract class TaskDatabase : RoomDatabase() {


    // ==========================================
    // ACCESO A LOS DAO
    // ==========================================

    abstract fun taskDao(): TaskDao

    abstract fun userDao(): UserDao


    companion object {

        @Volatile
        private var INSTANCE: TaskDatabase? = null


        // ======================================
        // MIGRACIÓN DE VERSIÓN 1 A VERSIÓN 2
        // ======================================

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {


                    // 1. CREAR TABLA DE USUARIOS

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `users` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `username` TEXT NOT NULL,
                            `passwordHash` TEXT NOT NULL,
                            `salt` TEXT NOT NULL
                        )
                        """.trimIndent()
                    )


                    // 2. ÍNDICE ÚNICO DE USUARIOS

                    database.execSQL(
                        """
                        CREATE UNIQUE INDEX IF NOT EXISTS
                        `index_users_username`
                        ON `users` (`username`)
                        """.trimIndent()
                    )


                    // 3. CREAR NUEVA TABLA DE TAREAS

                    database.execSQL(
                        """
                        CREATE TABLE `tasks_new` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `title` TEXT NOT NULL,
                            `category` TEXT NOT NULL,
                            `priority` TEXT NOT NULL,
                            `isCompleted` INTEGER NOT NULL,
                            `userId` INTEGER,
                            FOREIGN KEY (`userId`)
                            REFERENCES `users` (`id`)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )


                    // 4. COPIAR TAREAS ANTIGUAS

                    database.execSQL(
                        """
                        INSERT INTO `tasks_new` (
                            `id`,
                            `title`,
                            `category`,
                            `priority`,
                            `isCompleted`,
                            `userId`
                        )
                        SELECT
                            `id`,
                            `title`,
                            `category`,
                            `priority`,
                            `isCompleted`,
                            NULL
                        FROM `tasks`
                        """.trimIndent()
                    )


                    // 5. ELIMINAR TABLA ANTIGUA

                    database.execSQL(
                        "DROP TABLE `tasks`"
                    )


                    // 6. RENOMBRAR TABLA

                    database.execSQL(
                        "ALTER TABLE `tasks_new` RENAME TO `tasks`"
                    )


                    // 7. CREAR ÍNDICE PARA USER ID

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_tasks_userId`
                        ON `tasks` (`userId`)
                        """.trimIndent()
                    )

                }

            }


        // ======================================
        // OBTENER INSTANCIA DE LA BASE DE DATOS
        // ======================================

        fun getDatabase(context: Context): TaskDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "taskflow_database"
                )

                    // REGISTRAR MIGRACIÓN

                    .addMigrations(MIGRATION_1_2)

                    .build()

                    .also { database ->

                        INSTANCE = database

                    }

            }

        }

    }

}
