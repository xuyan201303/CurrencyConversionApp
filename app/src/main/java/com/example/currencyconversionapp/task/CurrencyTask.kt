package com.example.currencyconversionapp.task

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.example.currencyconversionapp.common.Utils
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class CurrencyTask(private val context: Context) : AsyncTask<String, String, JSONObject>() {
    override fun doInBackground(vararg params: String?): JSONObject? {
        //ここでAPIを叩きます。バックグラウンドで処理する内容です。
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        val buffer: StringBuffer

        try {
            val url = URL(params[0])
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val statusCode = connection.responseCode
            if (statusCode == HttpURLConnection.HTTP_OK) {
                Log.v("task_test", "2_start!!!")
                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                buffer = StringBuffer()
                var line: String?
                while (true) {
                    line = reader.readLine()
                    if (line == null) {
                        break
                    }
                    buffer.append(line)
                }

                val jsonText = buffer.toString()
                return JSONObject(jsonText)
            } else if (statusCode == HttpURLConnection.HTTP_NOT_MODIFIED) {

            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //finallyで接続を切断してあげましょう。
        finally {
            connection?.disconnect()
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //失敗した時はnullやエラーコードなどを返しましょう。
        return null
    }

    override fun onPostExecute(result: JSONObject?) {
        super.onPostExecute(result)
        result ?: return

        Utils.setPreCurrency(context, result)
    }
}
