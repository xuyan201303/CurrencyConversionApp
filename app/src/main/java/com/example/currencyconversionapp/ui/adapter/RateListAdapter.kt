package com.example.currencyconversionapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconversionapp.R
import org.json.JSONArray
import java.text.DecimalFormat

class RateListAdapter(
    context: Context,
    private val jsonArray: JSONArray,
    private val money: String
//    private val listener: RateListListener
) :
    RecyclerView.Adapter<RateListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        inflater.inflate(
            R.layout.activity_rate_list,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currenciesList = ArrayList<String>()
        val sourceList = ArrayList<String>()
        for (i in 0 until jsonArray.length()) {
            val data = jsonArray.getJSONObject(i)
            val currencies = data.getString("key")
            var source = data.getString("value")

            when (source) {
                "1.48E-4" -> source = "1.48"
            }

            val sourceDouble = source.toDouble()
            val  df = DecimalFormat("0.00")

            currenciesList.add(currencies)
            sourceList.add(df.format(sourceDouble).toString())
        }

        holder.currenciesView.text = currenciesList[position]
        holder.sourceView.text = sourceList[position]

        when (money) {
            "" -> {
            }
            else -> {
                val amountAfterConversion = (holder.sourceView.text.toString().toDouble()
                        * money.toInt())
                val  df = DecimalFormat("0.00")
                holder.changeFeeView.text = df.format(amountAfterConversion).toString()
            }
        }


        holder.itemView.setOnClickListener {
//            listener.onItemClick(holder.currenciesView.text.toString())
        }
    }

    override fun getItemCount(): Int = jsonArray.length()

//    interface RateListListener {
//        fun onItemClick(currencies: String)
//    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currenciesView: TextView = itemView.findViewById(R.id.currencies)
        val sourceView: TextView = itemView.findViewById(R.id.source)
        val changeFeeView: TextView = itemView.findViewById(R.id.changeFee)
    }

}