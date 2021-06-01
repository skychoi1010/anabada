package com.example.anabada.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.anabada.db.model.BoardsData
import com.example.anabada.db.model.RemoteKeys

@Database(
    entities = [BoardsData::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)

//@TypeConverters(
//    Converters::class,
//    LanguagesTypeConverter::class,
//    NameConverter::class,
//    NativeConverter::class,
//    TranslationsConverter::class)

abstract class AnabadaDatabase : RoomDatabase() {

    abstract fun boardsDataDao(): BoardsDataDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        private const val DATABASE_NAME: String = "anabada.db"
        const val TABLE_BOARDS_DATA: String = "boards_data"

        fun create(context: Context): AnabadaDatabase {
            return Room.databaseBuilder(context, AnabadaDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
        }

    }


//    fun provideCountriesDao(database: CountriesDatabase): CountriesDao {
//        return  database.countriesDao
//    }
}