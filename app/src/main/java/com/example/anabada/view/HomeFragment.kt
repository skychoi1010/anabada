package com.example.anabada.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anabada.*
import com.example.anabada.databinding.FragmentHomeBinding
import com.example.anabada.model.BoardsData
import com.example.anabada.network.BoardPageRes
import com.example.anabada.util.BaseViewBindingFragment
import com.example.anabada.view.adapter.BoardRecyclerAdapter
import com.example.anabada.view.adapter.BoardsDataAdapter
import com.example.anabada.view.adapter.BoardsLoadStateAdapter
import com.example.anabada.viewmodel.BoardViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.collections.ArrayList

class HomeFragment : BaseViewBindingFragment<FragmentHomeBinding>() {

    private var boardPageRes: BoardPageRes? = null
    private var boardsDataList = ArrayList<BoardsData>()
    private val boardViewModel: BoardViewModel by sharedViewModel()

    //mutableListOf(BoardsData(2,"skyy", "testtitle1", "contents\nnewline\nend\n", 1000, "1", false, "2020/03/16", "2020/03/16", "1", "skyyy"))
    private var boardRecyclerAdapter = BoardRecyclerAdapter(boardsDataList)
    private val boardsDataAdapter = BoardsDataAdapter()
    var pageNum = 1
    var isPageCallable = true
    private var api = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) return
        fragmentContext = context
//        initLogout()
        initView()
        initAdapter()
        initSwipeToRefresh()
//        initScrollListener()
        boardViewModel.getAllBoards()
//        boardViewModel.boardsDataList.observe(viewLifecycleOwner, Observer {
//            if (!it.isNullOrEmpty()) {
//                boardRecyclerAdapter.setDataNotify(it)
//            }
//        })
    }

    private fun initAdapter() {
        binding.rvBoard.adapter = boardsDataAdapter.withLoadStateFooter(
            footer = BoardsLoadStateAdapter { boardsDataAdapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            boardsDataAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.lSwipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            boardViewModel.boardsDataPaginated.collectLatest {
                boardsDataAdapter.submitData(it)

                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            boardsDataAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.rvBoard.smoothScrollToPosition(0) }
        }

        boardsDataAdapter.setItemClickListener(object : BoardsDataAdapter.ItemClickListener {
            override fun onClick(view: View, id: BoardsData) {
                Intent(fragmentContext, BoardDetailActivity::class.java).apply {
                    putExtra("board item", id)
                    startActivity(this)
                }
            }
        })
    }

    private fun initSwipeToRefresh() {
        binding.lSwipeRefresh.setOnRefreshListener { boardsDataAdapter.refresh() }
    }


//    private fun initLogout() {
//
//        binding.appbar.tvAppbarLoginLogout.text = "login"
//        if (SharedPreferencesManager.getUserNick(fragmentContext).isNotEmpty()) {
//            let {
//                binding.appbar.tvAppbarUserNickname.text = SharedPreferencesManager.getUserNick(fragmentContext)
//                binding.appbar.tvAppbarLoginLogout.text = "logout"
//            }
//        }
//
//        binding.appbar.tvAppbarLoginLogout.setOnClickListener {
////            api.reqLogout().enqueue(object : Callback<LogoutRes> {
////                override fun onFailure(call: Call<LogoutRes>, t: Throwable) {
////                    Toast.makeText(fragmentContext, "logout api\nFailed connection", Toast.LENGTH_SHORT).show()
////                }
////
////                override fun onResponse(call: Call<LogoutRes>, response: Response<LogoutRes>) {
////                    SharedPreferencesManager.clearUser(fragmentContext)
////                    Intent(fragmentContext, LoginActivity::class.java).apply {
////                        startActivity(this)
////                    }
////                }
////            })
//        }
//
//        boardRecyclerAdapter.setOptionsClickListener(object : BoardRecyclerAdapter.OptionsClickListener {
//
//            override fun onOptionsClick(view: View, id: BoardsData, binding: ListitemBoardBinding) {
//                //creating a popup menu
//                val popup = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
//                    PopupMenu(binding.root.context, binding.tvPrevCommentOptions, Gravity.END, 0, R.style.MyPopupMenu)
//                } else {
//                    PopupMenu(binding.root.context, binding.tvPrevCommentOptions)
//                }
//                //inflating menu from xml resource
//                popup.menuInflater.inflate(R.menu.menu_comments, popup.menu)
//                //adding click listener
//                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
//                    override fun onMenuItemClick(item: MenuItem?): Boolean {
//                        return when (item?.itemId) {
//                            R.id.edit -> {
//                                Intent(fragmentContext, BoardDetailActivity::class.java).apply {
//                                    putExtra("board item", id)
//                                    startActivity(this)
//                                }
//                                true
//                            }
//                            R.id.delete -> {
//                                Toast.makeText(fragmentContext, "delete!", Toast.LENGTH_SHORT).show()
//                                true
//                            }
//                            else -> false
//                        }
//                    }
//                })
//                //displaying the popup
//                popup.show()
//            }
//        })
//
//    }

    private fun initView() {

        binding.appbar.ivAppbarLogo.setOnClickListener {
            binding.rvBoard.smoothScrollToPosition(0)
        }

//        if (isPageCallable) {
//            pageNum = 1
//            Log.d("//////////init////////", pageNum.toString())
//            callBoard(pageNum)
//        }

        val layoutManager = LinearLayoutManager(context)
        binding.rvBoard.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context -> ContextCompat.getDrawable(context, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(it) } }
        binding.rvBoard.addItemDecoration(dividerItemDecoration)

//        boardViewModel.repoResult.observe(this) { result ->
//            when (result) {
//                is RepoSearchResult.Success -> {
//                    showEmptyList(result.data.isEmpty())
//                    adapter.submitList(result.data)
//                }
//                is RepoSearchResult.Error -> {
//                    Toast.makeText(
//                        this,
//                        "\uD83D\uDE28 Wooops $result.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
        }

//        binding.rvBoard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val totalItemCount = layoutManager.itemCount
//                val visibleItemCount = layoutManager.childCount
//                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//
//                boardViewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
//            }
//        })
//        }
//
//        boardRecyclerAdapter.setItemClickListener(object : BoardRecyclerAdapter.ItemClickListener {
//            override fun onClick(view: View, id: BoardsData) {
//                Intent(fragmentContext, BoardDetailActivity::class.java).apply {
//                    putExtra("board item", id)
//                    startActivity(this)
//                }
//            }
//        })

//        binding.lSwipeRefresh.setOnRefreshListener {
////            isPageCallable = true
////            pageNum = 1
////            boardsDataList.clear()
////            boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
////            Log.d("//////////swipe////////", pageNum.toString())
////            callBoard(pageNum)
////            binding.lSwipeRefresh.isRefreshing = false
//            boardsDataAdapter.refresh() //changed to paging adapter + load state adapter
//        }
//

    }

//    private fun initScrollListener() {
//        binding.rvBoard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            var temp: Int = 0
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                temp = 1
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (temp == 1) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    val layoutManager = binding.rvBoard.layoutManager
//                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
//                    val itemTotalCount = layoutManager.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1
//
//                    // 스크롤이 끝에 도달했는지 확인
//                    if ((lastVisibleItem > 0) && (lastVisibleItem == itemTotalCount)) {
//                        if (isPageCallable) {
//                            Log.d("/////////scroll///////", pageNum.toString())
//                            callBoard(pageNum)
//                        }
//                        Toast.makeText(fragmentContext, lastVisibleItem.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//            }
//        })
//    }


