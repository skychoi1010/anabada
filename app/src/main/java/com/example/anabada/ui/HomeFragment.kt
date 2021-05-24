package com.example.anabada.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.*
import com.example.anabada.databinding.FragmentHomeBinding
import com.example.anabada.databinding.LayoutLoadingBinding
import com.example.anabada.databinding.ListitemBoardBinding
import com.example.anabada.db.model.BoardsData
import com.example.anabada.network.ApiService
import com.example.anabada.network.BoardPageRes
import com.example.anabada.network.LogoutRes
import com.example.anabada.repository.MySharedPreferences
import com.example.anabada.viewmodel.BoardViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseViewBindingFragment<FragmentHomeBinding>() {

    private var boardPageRes: BoardPageRes? = null
    private var boardsDataList = ArrayList<BoardsData>()
    private val boardViewModel: BoardViewModel by sharedViewModel()

    //mutableListOf(BoardsData(2,"skyy", "testtitle1", "contents\nnewline\nend\n", 1000, "1", false, "2020/03/16", "2020/03/16", "1", "skyyy"))
    private var boardRecyclerAdapter = BoardRecyclerAdapter(boardsDataList)
    private var boardsDataAdapter = BoardsDataAdapter(boardsDataList)
    var pageNum = 1
    var isPageCallable = true
    private val api = ApiService.create(requireContext())

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLogout()
        initView()
        initScrollListener()
        boardViewModel.getAllBoards()
        boardViewModel.boardsDataList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                boardRecyclerAdapter.setDataNotify(it)
            }
        })
    }

    private fun initLogout() {

        binding.appbar.tvAppbarLoginLogout.text = "login"
        if (MySharedPreferences.getUserNick(fragmentContext).isNotEmpty()) {
            let {
                binding.appbar.tvAppbarUserNickname.text = MySharedPreferences.getUserNick(fragmentContext)
                binding.appbar.tvAppbarLoginLogout.text = "logout"
            }
        }

        binding.appbar.tvAppbarLoginLogout.setOnClickListener {
            api.reqLogout().enqueue(object : Callback<LogoutRes> {
                override fun onFailure(call: Call<LogoutRes>, t: Throwable) {
                    Toast.makeText(fragmentContext, "logout api\nFailed connection", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<LogoutRes>, response: Response<LogoutRes>) {
                    MySharedPreferences.clearUser(fragmentContext)
                    Intent(fragmentContext, LoginActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            })
        }

        boardRecyclerAdapter.setOptionsClickListener(object : BoardRecyclerAdapter.OptionsClickListener {

            override fun onOptionsClick(view: View, id: BoardsData, binding: ListitemBoardBinding) {
                //creating a popup menu
                val popup = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    PopupMenu(binding.root.context, binding.tvPrevCommentOptions, Gravity.END, 0, R.style.MyPopupMenu)
                } else {
                    PopupMenu(binding.root.context, binding.tvPrevCommentOptions)
                }
                //inflating menu from xml resource
                popup.menuInflater.inflate(R.menu.menu_comments, popup.menu)
                //adding click listener
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        return when (item?.itemId) {
                            R.id.edit -> {
                                Intent(fragmentContext, BoardDetailActivity::class.java).apply {
                                    putExtra("board item", id)
                                    startActivity(this)
                                }
                                true
                            }
                            R.id.delete -> {
                                Toast.makeText(fragmentContext, "delete!", Toast.LENGTH_SHORT).show()
                                true
                            }
                            else -> false
                        }
                    }
                })
                //displaying the popup
                popup.show()
            }
        })

    }

    private fun initView() {

        binding.appbar.ivAppbarLogo.setOnClickListener {
            binding.rvBoard.smoothScrollToPosition(0)
        }

        if (isPageCallable) {
            pageNum = 1
            Log.d("//////////init////////", pageNum.toString())
            callBoard(pageNum)
        }

        binding.rvBoard.adapter = boardsDataAdapter.withLoadStateFooter(BoardsLoadStateAdapter(boardsDataAdapter::retry))
        val layoutManager = LinearLayoutManager(fragmentContext)
        binding.rvBoard.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(fragmentContext, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(fragmentContext, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoard.addItemDecoration(dividerItemDecoration)

        boardViewModel.repoResult.observe(this) { result ->
            when (result) {
                is RepoSearchResult.Success -> {
                    showEmptyList(result.data.isEmpty())
                    adapter.submitList(result.data)
                }
                is RepoSearchResult.Error -> {
                    Toast.makeText(
                        this,
                        "\uD83D\uDE28 Wooops $result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.rvBoard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                boardViewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
        }

        boardRecyclerAdapter.setItemClickListener(object : BoardRecyclerAdapter.ItemClickListener {
            override fun onClick(view: View, id: BoardsData) {
                Intent(fragmentContext, BoardDetailActivity::class.java).apply {
                    putExtra("board item", id)
                    startActivity(this)
                }
            }
        })

        binding.lSwipeRefresh.setOnRefreshListener {
//            isPageCallable = true
//            pageNum = 1
//            boardsDataList.clear()
//            boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
//            Log.d("//////////swipe////////", pageNum.toString())
//            callBoard(pageNum)
//            binding.lSwipeRefresh.isRefreshing = false
            boardsDataAdapter.refresh() //changed to paging adapter + load state adapter
        }

        binding.floatingActionButton.setOnClickListener {
            if (MySharedPreferences.getUserId(fragmentContext) == "no") { // need to login
                Toast.makeText(fragmentContext, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                Intent(fragmentContext, LoginActivity::class.java).run {
                    startActivity(this)
                }
            } else {
                Intent(fragmentContext, PostActivity::class.java).run {
                    startActivity(this)
                }
            }
        }
    }

    private fun initScrollListener() {
        binding.rvBoard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var temp: Int = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                temp = 1
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (temp == 1) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = binding.rvBoard.layoutManager
                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = layoutManager.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                    // 스크롤이 끝에 도달했는지 확인
                    if ((lastVisibleItem > 0) && (lastVisibleItem == itemTotalCount)) {
                        if (isPageCallable) {
                            Log.d("/////////scroll///////", pageNum.toString())
                            callBoard(pageNum)
                        }
                        Toast.makeText(fragmentContext, lastVisibleItem.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }


    }

}
