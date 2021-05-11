package com.example.anabada.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.anabada.db.model.BoardsData

@Database(
    entities = [BoardsData::class],
    version = 1, exportSchema = false
)

//@TypeConverters(
//    Converters::class,
//    LanguagesTypeConverter::class,
//    NameConverter::class,
//    NativeConverter::class,
//    TranslationsConverter::class)

abstract class AnabadaDB : RoomDatabase() {
//    abstract val countriesDao: CountriesDao
}