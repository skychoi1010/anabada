package com.example.anabada

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "BoardsDataLocal")
@Parcelize
class BoardsDataLocal (
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
) : Parcelable
