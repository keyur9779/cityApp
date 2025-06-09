package com.testcityapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CityEmissionEntity::class], version = 1)
@TypeConverters(DateTimeConverter::class)
abstract class CityDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
}