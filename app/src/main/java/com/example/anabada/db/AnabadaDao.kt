package com.example.anabada.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anabada.network.BoardsData

@Dao
interface BoardsDataDao {

    @Query("SELECT * FROM BoardsData")
    fun findAll(): ArrayList<BoardsData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(data: ArrayList<BoardsData>)
}