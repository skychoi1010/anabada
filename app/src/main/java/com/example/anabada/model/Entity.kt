package com.example.anabada.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anabada.db.AnabadaDatabase
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = AnabadaDatabase.TABLE_BOARDS_DATA)
data class BoardsData (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var price: Int,
    var author: String,
    var thumbImg: String,
    var detailImg: String,
    val isMine: Boolean,
    var date: String,
    var commentCount: Int
): Parcelable

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

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val repoId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)