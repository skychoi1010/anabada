package com.example.anabada.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.anabada.CoroutineHandler
import com.example.anabada.Utils.handleApiError
import com.example.anabada.Utils.handleApiSuccess
import com.example.anabada.Utils.isNetworkAvailable
import com.example.anabada.repository.local.AnabadaDatabase
import com.example.anabada.db.BoardsDataDao
import com.example.anabada.db.model.BoardsData
import com.example.anabada.network.ApiService
import com.example.anabada.network.BoardPageRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface BoardsDataRepo {
//    suspend fun getBoard(): CoroutineHandler<BoardPageRes>
    fun getSearchResultStream(query: String): Flow<PagingData<BoardsData>>
    fun observeBoardsDataFromDB(): Flow<PagingData<BoardsData>>
}

class BoardsDataRepoImpl(
    private val api: ApiService,
    private val context: Context,
    private val db: AnabadaDatabase
) : BoardsDataRepo {

    private val boardsDataDao: BoardsDataDao = db.boardsDataDao()
    private val pagingSourceFactory = boardsDataDao.findAll()

    fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 20, enablePlaceholders = true)
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun observeBoardsDataFromDB(): Flow<PagingData<BoardsData>> {
        return Pager(
            config = PagingConfig(NETWORK_PAGE_SIZE), //TODO PAGE SIZE
            remoteMediator = BoardsRemoteMediator(db = db, api = api)
        ) {
            db.boardsDataDao().findAll()
        }.flow
    }

    @ExperimentalPagingApi
    fun letDoggoImagesFlowDb(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<BoardsData>> {
        if (db == null) throw IllegalStateException("Database is not initialized")
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { BoardsDataPagingSource(api) },
            remoteMediator = BoardsRemoteMediator(db, api)
        ).flow
    }

//    @OptIn(ExperimentalPagingApi::class)
//    override fun postsOfSubreddit(subReddit: String, pageSize: Int) = Pager(
//        config = PagingConfig(pageSize),
//        pagingSourceFactory = { BoardsDataPagingSource(api) },
//        remoteMediator = BoardsRemoteMediator(db = db, api = api)
//    ).flow

    fun letDoggoImagesFlow(pageSize: Int): Flow<PagingData<BoardsData>> {
        return Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { BoardsDataPagingSource(api) }
        ).flow
    }

//    override suspend fun getBoard(): CoroutineHandler<BoardPageRes> {
//        if (isNetworkAvailable(context)) {
//            try {
//                val response = api.reqBoard(1)
//                if (response.isSuccessful) {
//                    response.body()
//                    //save the data
//                    response.body()?.boards.let {
//                        withContext(Dispatchers.IO) {
//                            if (it != null) {
//                                dao.add(it)
//                            }
//                        }
//                    }
//                    return handleApiSuccess(response)
//                } else {
//                    Toast.makeText(context, "board api\nFailed connection", Toast.LENGTH_SHORT).show()
//                    Log.d("///BoardsDataRepo", "board api Failed connection")
//                    return handleApiError(response)
//                }
//            } catch (e: Exception) {
//                return CoroutineHandler.Error(e)
//            }
//        } else {
//            //check in db if the data exists
//            val data = getDataFromCache()
//            return run {
//                Log.d("db", "from db")
//                CoroutineHandler.Success(BoardPageRes(true, "DB", data))
//            }
//            //                context.noNetworkConnectivityError()
//        }
//    }

//    private suspend fun getDataFromCache(): ArrayList<BoardsData> {
//        return withContext(Dispatchers.IO) {
//            dao.findAll()
//        }
//    }

    override fun getSearchResultStream(query: String): Flow<PagingData<BoardsData>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BoardsDataPagingSource(api) }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 3 * 20 //current page size must be initial loading page times three (??)
    }

    /*
    This is another way of implementing where the source of data is db and api but we can always fetch from db
    which will be updated with the latest data from api and also change findAll() return type to
    LiveData<List<CountriesData>>
    */
    /* val data = dao.findAll()

     suspend fun getAllCountriesData() {
         withContext(Dispatchers.IO) {
             val response = api.getAllCountries()
             response.body()?.let {
                 withContext(Dispatchers.IO) { dao.add(it) }
             }
         }
     }*/

}