package com.example.anabada.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.anabada.CoroutineHandler
import com.example.anabada.Utils.handleApiError
import com.example.anabada.Utils.handleApiSuccess
import com.example.anabada.Utils.isNetworkAvailable
import com.example.anabada.db.BoardsDataDao
import com.example.anabada.db.model.BoardsData
import com.example.anabada.network.ApiService
import com.example.anabada.network.BoardPageRes
import com.example.anabada.ui.BoardsDataPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface BoardsDataRepo {
    suspend fun getBoard(): CoroutineHandler<BoardPageRes>
    fun getSearchResultStream(query: String): Flow<PagingData<com.example.anabada.db.model.BoardsData>>
}

class BoardsDataRepoImpl(
    private val api: ApiService,
    private val context: Context,
    private val dao: BoardsDataDao
) : BoardsDataRepo {
    override suspend fun getBoard(): CoroutineHandler<BoardPageRes> {
        if (isNetworkAvailable(context)) {
            try {
                val response = api.reqBoard(1)
                if (response.isSuccessful) {
                    response.body()
                    //save the data
                    response.body()?.boards.let {
                        withContext(Dispatchers.IO) {
                            if (it != null) {
                                dao.add(it)
                            }
                        }
                    }
                    return handleApiSuccess(response)
                } else {
                    Toast.makeText(context, "board api\nFailed connection", Toast.LENGTH_SHORT).show()
                    Log.d("///BoardsDataRepo", "board api Failed connection")
                    return handleApiError(response)
                }
            } catch (e: Exception) {
                return CoroutineHandler.Error(e)
            }
        } else {
            //check in db if the data exists
            val data = getDataFromCache()
            return run {
                Log.d("db", "from db")
                CoroutineHandler.Success(BoardPageRes(true, "DB", data))
            }
            //                context.noNetworkConnectivityError()
        }
    }

    private suspend fun getDataFromCache(): ArrayList<BoardsData> {
        return withContext(Dispatchers.IO) {
            dao.findAll()
        }
    }

    override fun getSearchResultStream(query: String): Flow<PagingData<BoardsData>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BoardsDataPagingSource(api, query) }
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