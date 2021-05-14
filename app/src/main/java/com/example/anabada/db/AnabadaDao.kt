package com.example.anabada.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anabada.db.model.BoardsDataLocal

@Dao
interface BoardsDataDao {

    @Query("SELECT * FROM boards_data")
    fun findAll(): BoardsDataLocal

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(data: BoardsDataLocal)

}