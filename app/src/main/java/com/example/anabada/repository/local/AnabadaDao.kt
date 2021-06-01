package com.example.anabada.repository.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anabada.db.model.BoardsData
import com.example.anabada.db.model.RemoteKeys

@Dao
interface BoardsDataDao {

    @Query("SELECT * FROM boards_data ORDER BY boards_data.date DESC")
    fun observeBoardsPaginated(): PagingSource<Int, BoardsData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: ArrayList<BoardsData>)

    @Query("DELETE FROM boards_data")
    fun deleteBoardItems(): Int

}

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE repoId = :repoId")
    suspend fun remoteKeysRepoId(repoId: Int): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}