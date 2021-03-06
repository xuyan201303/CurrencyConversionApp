package com.example.currencyconversionapp.ui.activity

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconversionapp.R
import com.example.currencyconversionapp.common.Utils
import com.example.currencyconversionapp.task.CurrencyTask
import com.example.currencyconversionapp.task.RateTask
import com.example.currencyconversionapp.ui.adapter.RateListAdapter
//import com.example.currencyconversionapp.ui.adapter.RateListAdapter.RateListListener
import kotlinx.android.synthetic.main.activity_rate_mian.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList


class RateListActivity : AppCompatActivity() {

    private var currencyTask: CurrencyTask? = null
    private var rateTask: RateTask? = null
    private val time = 30 * 6 * 10000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_mian)

        findViewById<EditText>(R.id.money).setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> {
                }
                false -> {
                    val inputMethodManager: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        v.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )

                    setRateListAdapter(money?.text.toString())
                }
            }
        }
        callAsynchronousTask()

        setRecyclerView()
    }

    private fun setRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rateList)

        // 表示設定
        val manager = LinearLayoutManager(this)
        manager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = manager
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                // 間隔を設定
                outRect.set(0, 0, 0, 10)
            }
        })
        recyclerView.bringToFront()

        setRateListAdapter("")
    }

    private fun setRateListAdapter(money: String) {

        var jsonArray: JSONArray? = null
        val data = getSharedPreferences("dataPreference", Context.MODE_PRIVATE)
            .getString(Utils.DATA_PRE_KEY, "")

        if (!data.equals("")) {
            jsonArray = JSONArray(data)
        }

        jsonArray ?: return

        val list = ArrayList<String>()
        val len = jsonArray.length()
        for (i in 0 until len) {
            list.add(jsonArray.get(i).toString())
        }

        rateList?.adapter = RateListAdapter(this, jsonArray, money)
//        , object : RateListListener {
//            override fun onItemClick(currencies: String) {
////                baseChip?.text = currencies
//
//            }
//        })
    }

    private fun callAsynchronousTask() {
        val handler = Handler()
        val timer = Timer()

        currencyTask = CurrencyTask(this@RateListActivity)
        currencyTask?.execute("${Utils.BASE_URL}${Utils.LIST}?access_key=${Utils.ACCESS_KEY}")

        val doAsynchronousTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    try {
                        rateTask = RateTask(this@RateListActivity)
                        rateTask?.execute("${Utils.BASE_URL}${Utils.LIVE}?access_key=${Utils.ACCESS_KEY}")
                    } catch (e: Exception) {
                        // TODO Auto-generated catch block
                    }
                }
            }
        }
        timer.schedule(doAsynchronousTask, 0, time)
    }
}
