package com.example.anabada.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

abstract class AnabadaDatabase : RoomDatabase() {

    abstract val boardsDataDao: BoardsDataDao

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