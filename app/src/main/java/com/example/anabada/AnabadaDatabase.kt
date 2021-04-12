package com.example.anabada

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        BoardsDataLocal::class
    ]
)

abstract class AnabadaDatabase : RoomDatabase() {

    abstract val anabadaDao: AnabadaDao

}
