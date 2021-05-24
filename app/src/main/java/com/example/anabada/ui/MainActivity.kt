package com.example.anabada.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.anabada.BaseViewBindingActivity
import com.example.anabada.R
import com.example.anabada.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : BaseViewBindingActivity<ActivityMainBinding>({ ActivityMainBinding.inflate(it)}) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(binding)
    }

    private fun initView(binding: ActivityMainBinding) {
        binding.bottomNav.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_home -> {
                    Intent(this, HomeFragment::class.java).apply {
                        startActivity(this)
                    }
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        pager?.adapter = MainPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        pager?.offscreenPageLimit = 4
        pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNav?.selectedItemId = R.id.tab_home
                    1 -> bottomNav?.selectedItemId = R.id.tab_trends
                    2 -> bottomNav?.selectedItemId = R.id.tab_post
                    3 -> bottomNav?.selectedItemId = R.id.tab_chat
                    4 -> bottomNav?.selectedItemId = R.id.tab_mypage
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        bottomNav?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_home -> {
                    pager?.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.tab_trends -> {
                    pager?.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.tab_post -> {
                    pager?.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
                R.id.tab_chat -> {
                    pager?.currentItem = 3
                    return@OnNavigationItemSelectedListener true
                }
                R.id.tab_mypage -> {
                    pager?.currentItem = 4
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private var time: Long = 0
    override fun onBackPressed() {
        if (pager?.currentItem == 0) {
            if (System.currentTimeMillis() - time >= 1000) {
                time = System.currentTimeMillis()
                Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            } else if (System.currentTimeMillis() - time < 1000) {
                finishAffinity()
                System.runFinalization()
                exitProcess(0)
            }
        }
    }

    private inner class MainPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {

        private val fragments = listOf(
                HomeFragment()
//                TrendsFragment(),
//                RewardFragment(),
//                LotteryFragment(),
//                MenuFragment()
        )

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }
//
//    private inner class FinishActivityBroadcastReceiver : BroadcastReceiver() {
//
//        val filter: IntentFilter
//            get() {
//                val filter = IntentFilter()
//                filter.addAction(BROADCAST_FINISH_MAIN_ACTIVITY)
//                return filter
//            }
//
//        override fun onReceive(context: Context, intent: Intent) {
//            if (intent.action != null) {
//                if (intent.action == BROADCAST_FINISH_MAIN_ACTIVITY) {
//                    finish()
//                }
//            }
//        }
//    }
//
//    private inner class ViewPagerGotoReceiver : BroadcastReceiver() {
//
//        val filter: IntentFilter
//            get() {
//                val filter = IntentFilter()
//                filter.addAction(AppConstants.ACTION_TAB_GOTO_TRENDS)
//                filter.addAction(AppConstants.ACTION_TAB_GOTO_REWARDS)
//                filter.addAction(AppConstants.ACTION_TAB_GOTO_LOTTERY)
//                return filter
//            }
//
//        override fun onReceive(context: Context, intent: Intent) {
//
//            when(intent.action){
//                AppConstants.ACTION_TAB_GOTO_TRENDS -> pager?.currentItem = 1
//                AppConstants.ACTION_TAB_GOTO_REWARDS -> pager?.currentItem = 2
//                AppConstants.ACTION_TAB_GOTO_LOTTERY -> pager?.currentItem = 3
//            }
//        }
//    }
//
//    fun addMenuPageHistory(pageIndex: Int) {
//        menuPageHistory.push(pageIndex)
//    }
//
//    fun menuHistoryClear() {
//        if (!menuPageHistory.empty()) {
//            menuPageHistory.clear()
//        }
//    }
}

