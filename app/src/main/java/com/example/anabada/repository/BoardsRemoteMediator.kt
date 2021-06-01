package com.example.anabada.repository

import android.content.Context
import android.widget.Toast
import androidx.paging.*
import androidx.room.withTransaction
import com.example.anabada.repository.local.AnabadaDatabase
import com.example.anabada.repository.local.BoardsDataDao
import com.example.anabada.repository.local.RemoteKeysDao
import com.example.anabada.db.model.BoardsData
import com.example.anabada.db.model.RemoteKeys
import com.example.anabada.network.ApiService
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException


const val BOARDS_PAGING_START_INDEX = 1
const val BOARDS_PAGE_SIZE = 20

@OptIn(ExperimentalPagingApi::class)
class BoardsRemoteMediator(
    private val db: AnabadaDatabase,
    private val api: ApiService,
    private val context: Context
) : RemoteMediator<Int, BoardsData>() {

    private val boardsDataDao: BoardsDataDao = db.boardsDataDao()
    private val remoteKeysDao: RemoteKeysDao = db.remoteKeysDao()

    override suspend fun initialize(): InitializeAction {
//        val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
//        return if (System.currentTimeMillis() - db.lastUpdated() >= cacheTimeout)
//        {
//            // Cached data is up-to-date, so there is no need to re-fetch
//            // from the network.
//            InitializeAction.SKIP_INITIAL_REFRESH
//        } else {
//            // Need to refresh cached data from network; returning
//            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
//            // APPEND and PREPEND from running until REFRESH succeeds.
//            InitializeAction.LAUNCH_INITIAL_REFRESH
//        }
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, BoardsData>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
//        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
//            ?.let { repo ->
//                // Get the remote keys of the last item retrieved
//                db.remoteKeysDao().remoteKeysRepoId(repo.id)
//            }
        return state.lastItemOrNull()?.let { repo->
            db.withTransaction {
                db.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, BoardsData>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                db.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, BoardsData>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                db.withTransaction{
                    db.remoteKeysDao().remoteKeysRepoId(repoId)
                }
            }
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, BoardsData>): MediatorResult {

        try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: BOARDS_PAGING_START_INDEX
    //                null
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
    //                val remoteKeys = getRemoteKeyForFirstItem(state)
    //                // If remoteKeys is null, that means the refresh result is not in the database yet.
    //                // We can return Success with `endOfPaginationReached = false` because Paging
    //                // will call this method again if RemoteKeys becomes non-null.
    //                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
    //                // the end of pagination for prepend.
    //                val prevKey = remoteKeys?.prevKey
    //                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
    //                prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                            ?: throw InvalidObjectException("Result is empty")
                    remoteKeys.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                    // If remoteKeys is null, that means the refresh result is not in the database yet.
                    // We can return Success with endOfPaginationReached = false because Paging
                    // will call this method again if RemoteKeys becomes non-null.
                    // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                    // the end of pagination for append.
    //                val nextKey = remoteKeys?.nextKey
    //                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
    //                nextKey
                }
            }

            val response = api.reqBoard(page) //TODO
            Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show()
            val repos = response.body()?.boards as ArrayList<BoardsData>
            val endOfPaginationReached = repos.size < state.config.pageSize
//            val endOfPaginationReached = repos.isEmpty()
            db.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    boardsDataDao.deleteBoardItems()
                }
                val prevKey = if (page == BOARDS_PAGING_START_INDEX) null else page.minus(1)
                val nextKey = if (endOfPaginationReached) null else page.plus(1)
                val keys = repos.map {
                    RemoteKeys(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                remoteKeysDao.insertAll(keys)
                boardsDataDao.insertAll(repos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, BoardsData>
//    ): MediatorResult {
//        return try {
//            // The network load method takes an optional after=<user.id>
//            // parameter. For every page after the first, pass the last user
//            // ID to let it continue from where it left off. For REFRESH,
//            // pass null to load the first page.
//            val loadKey = when (loadType) {
//                LoadType.REFRESH -> null
//                // In this example, you never need to prepend, since REFRESH
//                // will always load the first page in the list. Immediately
//                // return, reporting end of pagination.
//                LoadType.PREPEND ->
//                    return MediatorResult.Success(endOfPaginationReached = true)
//                LoadType.APPEND -> {
//                    val lastItem = state.lastItemOrNull()
//
//                    // You must explicitly check if the last item is null when
//                    // appending, since passing null to networkService is only
//                    // valid for initial load. If lastItem is null it means no
//                    // items were loaded after the initial REFRESH and there are
//                    // no more items to load.
//                    if (lastItem == null) {
//                        return MediatorResult.Success(
//                            endOfPaginationReached = true
//                        )
//                    }
//
//                    lastItem.id
//                }
//            }
//
//            // Suspending network load via Retrofit. This doesn't need to be
//            // wrapped in a withContext(Dispatcher.IO) { ... } block since
//            // Retrofit's Coroutine CallAdapter dispatches on a worker
//            // thread.
//            val response = loadKey?.let { api.reqBoard(it) }
//
//            database.withTransaction {
//                if (loadType == LoadType.REFRESH) {
//                    userDao.deleteByQuery(query)
//                }
//
//                // Insert new users into database, which invalidates the
//                // current PagingData, allowing Paging to present the updates
//                // in the DB.
//                dao.insertAll(response?.body()?.boards)
//            }
//
//            MediatorResult.Success(
//                endOfPaginationReached = response.nextKey == null
//            )
//        } catch (e: IOException) {
//            MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            MediatorResult.Error(e)
//        }
//    }

}