package com.example.anabada

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnabadaDao {

    @Query("SELECT * FROM BoardsDataLocal")
    fun findAll(): ArrayList<BoardsData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(boardsData: ArrayList<BoardsData>)

}