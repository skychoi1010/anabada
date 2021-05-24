package com.example.anabada.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anabada.db.model.BoardsData

@Dao
interface BoardsDataDao {

    @Query("SELECT * FROM boards_data")
    fun findAll(): ArrayList<BoardsData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(data: ArrayList<BoardsData>)

}