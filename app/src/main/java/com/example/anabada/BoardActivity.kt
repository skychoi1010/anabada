package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.databinding.ActivityBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.text.format.DateFormat
import kotlin.random.Random
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class BoardActivity : AppCompatActivity() {

    private var boardPageRes: BoardPageRes? = null
    private var boardsDataList = ArrayList<BoardsData>()

    //mutableListOf(BoardsData(2,"skyy", "testtitle1", "contents\nnewline\nend\n", 1000, "1", false, "2020/03/16", "2020/03/16", "1", "skyyy"))
    private var boardRecyclerAdapter = BoardRecyclerAdapter(boardsDataList)
    var pageNum = 1
    var isPageCallable = true
    private val api = ApiService.create(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLogout(binding)
        initView(binding)
        initScrollListener(binding)
    }

    private fun initLogout(binding: ActivityBoardBinding) {

        binding.appbar.tvAppbarLoginLogout.text = "login"
        if (MySharedPreferences.getUserNick(this).isNotEmpty()) {
            let {
                binding.appbar.tvAppbarUserNickname.text = MySharedPreferences.getUserNick(this)
                binding.appbar.tvAppbarLoginLogout.text = "logout"
            }
        }

        binding.appbar.tvAppbarLoginLogout.setOnClickListener {
            api.reqLogout().enqueue(object : Callback<LogoutRes> {
                override fun onFailure(call: Call<LogoutRes>, t: Throwable) {
                    Toast.makeText(this@BoardActivity, "logout api\nFailed connection", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<LogoutRes>, response: Response<LogoutRes>) {
                    MySharedPreferences.clearUser(this@BoardActivity)
                    Intent(this@BoardActivity, LoginActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            })
        }

    }
    /*
    public  void onBackPressed()  {
        long tempTime  =  System.currentTimeMillis();   
        long intervalTime  =  tempTime - backPressedTime;   
        if (0 <= intervalTime  &&  FINISH_INTERVAL_TIME >= intervalTime)   {       
            super.onBackPressed();   
        }   else {            
            backPressedTime  =  tempTime;     
            Toast.makeText(getApplicationContext(),  "한번 더 누르면 종료됩니다.",  Toast.LENGTH_SHORT).show();   
        }
    }
     */

    private var time: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - time >= 1000) {
            time = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else if (System.currentTimeMillis() - time < 1000) {
            finishAffinity()
            System.runFinalization()
            exitProcess(0)
        }
    }

    private fun initView(binding: ActivityBoardBinding) {

        binding.appbar.ivAppbarLogo.setOnClickListener {
            binding.rvBoard.smoothScrollToPosition(0)
        }

        if (isPageCallable) {
            pageNum = 1
            Log.d("//////////init////////", pageNum.toString())
            callBoard(pageNum)
        }

        binding.rvBoard.adapter = boardRecyclerAdapter
        binding.rvBoard.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this@BoardActivity, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this@BoardActivity, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoard.addItemDecoration(dividerItemDecoration)

        boardRecyclerAdapter.setItemClickListener(object : BoardRecyclerAdapter.ItemClickListener {
            override fun onClick(view: View, item: BoardsData) {
                Intent(this@BoardActivity, BoardDetailActivity::class.java).apply {
                    putExtra("board item", item)
                    startActivity(this)
                }
            }
        })

        binding.lSwipeRefresh.setOnRefreshListener {
            isPageCallable = true
            pageNum = 1
            boardsDataList.clear()
            boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
            Log.d("//////////swipe////////", pageNum.toString())
            callBoard(pageNum)
            binding.lSwipeRefresh.isRefreshing = false
        }

        binding.floatingActionButton.setOnClickListener {
            Intent(this@BoardActivity, PostActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun initScrollListener(binding: ActivityBoardBinding) {
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
                        Toast.makeText(this@BoardActivity, lastVisibleItem.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }

    private fun callBoard(callNum: Int) {
        api.reqBoard(callNum).enqueue(object : Callback<BoardPageRes> {
            override fun onFailure(call: Call<BoardPageRes>, t: Throwable) {
                Toast.makeText(
                        this@BoardActivity,
                        "board api\nFailed connection",
                        Toast.LENGTH_SHORT
                ).show()
                //end
            }

            override fun onResponse(call: Call<BoardPageRes>, response: Response<BoardPageRes>) {
                boardPageRes = response.body()
                when {
                    boardPageRes?.success == null -> {
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
        })
    }

    ///////timestamp to date


    // 날짜만 타임스탬프 변환 2020-01-01 - timestamp
    fun String.convertDateToTimestamp(): Long =
            SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).parse(this).time

    fun Long.convertTimestampToDate(): String = DateFormat.format("yyyy-MM-dd", this).toString()

    fun Long.convertTimestampToPointFullDate(): String =
            DateFormat.format("yyyy.MM.dd", this).toString()


    // 날짜,시간,분 포함된 타임스탬프 변환 2020-01-01-22-30 - timestamp
    fun String.convertDateFullToTimestamp(): Long =
            SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.KOREA).parse(this).time

    fun Long.convertTimestampToDateFull(): String =
            DateFormat.format("yyyy-MM-dd-HH:mm", this).toString()

    fun Long.convertCurrentTimestampToDateTimestamp(): Long =
            this.convertTimestampToDate().convertDateToTimestamp()

    // timestamp -> 13:40
    fun Long.convertTimestampToTime(): String = DateFormat.format("HH:mm", this).toString()

    //fun Long.convertTimesToTimestamp(): Long = DateFormat.format("HH:mm", this).toString()

    // 시간 한자리면 앞에 0 붙여주어 변환
    fun String.convertHourDoubleDigit(): String = if (this.length < 2) "0$this" else this

    // 분 한자리면 앞에 0 붙여주어 반환
    fun String.convertMinuteDoubleDigit(): String = if (this.length < 2) "0$this" else this

    // 한자리 숫자면 두자리로 변환
    fun String.convertSingleToDoubleDigit(): String = if (this.length < 2) "0$this" else this

    fun Long.convertTimestampToHour(): Int = DateFormat.format("HH", this).toString().toInt()

    fun Long.convertTimestampToMinute(): Int = DateFormat.format("mm", this).toString().toInt()

    fun Int.convertNextHour(): Int = if (this == 23) 0 else this + 1

    fun Int.convertNextMinute(): Int = if (this == 59) 0 else this + 1

    // 현재 Year
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    // 현재 Month
    fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    // 현재 Day
    fun getCurrentDay(): Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // timestamp -> year
    fun Long.convertTimestampToYear(): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = this
        return cal[Calendar.YEAR]
    }

    // timestamp -> month
    fun Long.convertTimestampToMonth(): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = this
        return cal[Calendar.MONTH] + 1
    }

    // timestamp -> day
    fun Long.convertTimestampToDay(): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = this
        return cal[Calendar.DAY_OF_MONTH]
    }


    // 1시간 뒤 타임스탬프
    fun Long.convertNextHourTimestamp(): Long = this + (60 * 60 * 1000)

    // 2020, 1, 20 -> timestamp
    fun convertDateToTimestamp(_year: Int, _month: Int, _day: Int): Long {
        val month = _month.toString().convertSingleToDoubleDigit().toInt()
        val day = _day.toString().convertSingleToDoubleDigit().toInt()
        val date = "$_year-$month-$day"
        return date.convertDateToTimestamp()
    }

    // timestamp -> 9.6~9.8
    fun convertTimestampToTerm(startTimestamp: Long, endTimestamp: Long): String {
        return DateFormat.format("MM-dd", startTimestamp)
                .toString() + "~" + DateFormat.format("MM-dd", endTimestamp).toString()
    }

    // FCM 메시지로 사용
    fun convertTimeToFcmMessage(date: Long, startTime: Long): String =
            date.convertTimestampToDate() + " " + startTime.convertTimestampToTime() + "에 회의실 예약이 있습니다."

    fun combineTimestamp(x: Long, y: Long) = (x.toString() + y.toString()).toLong()

    // 랜덤키값
    fun getRandomKey(): Long = Random(System.currentTimeMillis()).nextLong(100000, 999999)

    // 타임스탬프
    fun getTimestamp(): Long = System.currentTimeMillis()

}
