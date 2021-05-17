package com.example.anabada.repository

import android.content.Context
import android.util.Log
import com.example.anabada.Utils.isNetworkAvailable
import com.example.anabada.db.BoardsDataDao
import com.example.anabada.network.ApiService
import com.example.anabada.network.BoardsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BoardsDataRepo {
    suspend fun getBoard(): ArrayList<BoardsData>
}

class BoardsDataRepoImpl(
    private val api: ApiService,
    private val context: Context,
    private val dao: BoardsDataDao
) : BoardsDataRepo {
    override suspend fun getBoard(): ArrayList<BoardsData> {
        if (isNetworkAvailable(context)) {
            return try {
                val response = api.reqBoard(1)
                if (response.isSuccessful) {
                    //save the data
                    response.body().let {
                        withContext(Dispatchers.IO) { dao.add(it) }
                    }
                    handleSuccess(response)
                } else {
                    handleApiError(response)
                }
            } catch (e: Exception) {
                AppResult.Error(e)
            }
        } else {
            //check in db if the data exists
            val data = getCountriesDataFromCache()
            return if (data.isNotEmpty()) {
                Log.d("db", "from db")
                AppResult.Success(data)
            } else
            //no network
                context.noNetworkConnectivityError()
        }
    }

    private suspend fun getCountriesDataFromCache(): ArrayList<BoardsData> {
        return withContext(Dispatchers.IO) {
            dao.findAll()
        }
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