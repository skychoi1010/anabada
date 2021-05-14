package com.example.anabada.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anabada.db.AnabadaDatabase

@Entity(tableName = AnabadaDatabase.TABLE_BOARDS_DATA)
class BoardsData (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var price: Int,
    var author: String,
    var thumbImg: String,
    var detailImg: String,
    var isMine: Boolean,
    var date: String,
    var commentCount: Int
)

//@Entity(tableName = "BoardsData")
//class BoardsDetail (
//    @PrimaryKey(autoGenerate = true)
//    var id: Int,
//    var title: String,
//    var price: Int,
//    var author: String,
//    var thumbImg: String,
//    var detailImg: String,
//    var isMine: Boolean,
//    var date: String,
//    var commentCount: Int
//)