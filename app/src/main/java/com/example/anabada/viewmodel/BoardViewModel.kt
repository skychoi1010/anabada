package com.example.anabada.viewmodel

import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.*
import com.example.anabada.CoroutineHandler
import com.example.anabada.SingleLiveEvent
import com.example.anabada.db.model.BoardsData
import com.example.anabada.network.ApiService
import com.example.anabada.repository.BoardsDataRepo
import com.example.anabada.ui.BoardsDataPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BoardViewModel(
    private val boardsDataRepo: BoardsDataRepo,
    private val api: ApiService
) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading
    private val _boardsDataList = MutableLiveData<ArrayList<BoardsData>>()
    val boardsDataList: LiveData<ArrayList<BoardsData>> = _boardsDataList

    private val queryLiveData = MutableLiveData<String>()
    val repoResult: LiveData<BoardsData> = queryLiveData.switchMap { queryString ->
        liveData {
            val repos = boardsDataRepo.getSearchResultStream(queryString).asLiveData(Dispatchers.Main)
            emitSource(repos)
        }
    }


    private val showError = SingleLiveEvent<String>()

    fun getAllBoards() {
        _showLoading.postValue(true)
        viewModelScope.launch {
            val result = boardsDataRepo.getBoard()

            _showLoading.postValue(false)
            when (result) {
                is CoroutineHandler.Success -> {
                    _boardsDataList.postValue(result.successData)
                    showError.value = null
                }
                is CoroutineHandler.Error -> showError.value = result.exception.message
            }
            boardPageRes = response.body()
            when(result){
                result.success == null -> {
                    //end
                    Toast.makeText(this@BoardActivity, "페이지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                boardPageRes?.boards?.isEmpty() == true -> {
                    //end
                    Toast.makeText(this@BoardActivity, "end of page", Toast.LENGTH_SHORT).show()
                    //boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                    this@BoardActivity.isPageCallable = false
                    boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                }
                else -> {
                    /*Toast.makeText(this@BoardActivity, "board api\nsuccess: " + boardPageRes?.success.toString() +
                            "\nresult code: " + boardPageRes?.resultCode + "\nboards: " + boardPageRes?.boards?.get(0)?.title, Toast.LENGTH_SHORT).show()*/
                    boardPageRes?.boards.also {
                        if (it != null) {
                            boardsDataList.addAll(it)
                        }
                    }
                    boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                    //callBoard(pageNum + 1, api) //재귀..
                    this@BoardActivity.isPageCallable = true
                    this@BoardActivity.pageNum = callNum + 1
                }
            }
        }
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            val immutableQuery = queryLiveData.value
            if (immutableQuery != null) {
                viewModelScope.launch {
                    repository.requestMore(immutableQuery)
                }
            }
        }
    }

    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 20)
    ) {
        BoardsDataPagingSource(api)
    }.flow
        .cachedIn(viewModelScope)

    val floww = Pager(
        PagingConfig(pageSize = 20)
    ) { // config 설정
        BoardsDataPagingSource(BoardsDataRepo()) // pagingSource 연결
    }.flow.map {
        it.map<BoardsData> { BoardsData.Item(it) }
        .cachedIn(viewModelScope) // 캐싱


//    private var currentQueryValue: String? = null
//
//    private var currentSearchResult: Flow<PagingData<com.example.anabada.db.model.BoardsData>>? = null
//
//    fun searchRepo(queryString: String): Flow<PagingData<com.example.anabada.db.model.BoardsData>> {
//        val lastResult = currentSearchResult
//        if (queryString == currentQueryValue && lastResult != null) {
//            return lastResult
//        }
//        currentQueryValue = queryString
//        val newResult: Flow<PagingData<com.example.anabada.db.model.BoardsData>> = boardsDataRepo.getSearchResultStream(queryString)
//            .cachedIn(viewModelScope)
//        currentSearchResult = newResult
//        return newResult
//    }

}