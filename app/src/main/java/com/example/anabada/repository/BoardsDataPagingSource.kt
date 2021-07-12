package com.example.anabada.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anabada.model.BoardsData
import com.example.anabada.network.ApiService
import retrofit2.HttpException
import java.io.IOException

//const val BOARDS_PAGING_START_INDEX = 1
const val NETWORK_PAGE_SIZE = 1

class BoardsDataPagingSource(
    private val api: ApiService
) : PagingSource<Int, BoardsData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardsData> {
        val position = params.key ?: BOARDS_PAGING_START_INDEX
        return try {
            val response = api.reqBoard(position)
            val repos = response.body()?.boards as ArrayList<BoardsData>
            val prevKey = if (position == BOARDS_PAGING_START_INDEX) null else position - 1
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = repos,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, BoardsData>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}