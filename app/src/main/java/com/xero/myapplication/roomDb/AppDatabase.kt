package com.xero.myapplication.roomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ProductModel::class, WishlistItem::class], version = 3)
abstract class AppDatabase : RoomDatabase() {


    companion object {
        private var database: AppDatabase? = null
        private const val DATABASE_NAME = "E-COMMERCE"

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return database!!
        }
    }
    abstract fun productDao(): ProductDao
}
