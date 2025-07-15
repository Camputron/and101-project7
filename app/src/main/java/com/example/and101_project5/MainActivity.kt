package com.example.and101_project5

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.BinaryHttpResponseHandler
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import okhttp3.Response
import androidx.core.graphics.drawable.toDrawable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bt = findViewById<Button>(R.id.btNext)
        var idx = 0
        val monsters = arrayOf("ditto", "bulbasaur", "treecko", "exeggcute", "pikachu", "gengar", "palkia", "slowbro", "metagross", "dialga")
        bt.setOnClickListener {
            if (idx >= monsters.size) {
                idx = 0
            }
            displayMonster(monsters[idx++])
        }
    }

    private fun displayMonster(name: String) {
        val iv = findViewById<ImageView>(R.id.iv)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvType = findViewById<TextView>(R.id.tvType)
        val client = AsyncHttpClient()
        val params = RequestParams()
        client["https://pokeapi.co/api/v2/pokemon/${name}", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(p0: Int, p1: Headers?, json: JSON) {
                val sprites = json.jsonObject.getJSONObject("sprites")
                val spriteUrl = sprites.getString("front_default")
                val name = json.jsonObject.getString("name")
                val typeArr = json.jsonObject.getJSONArray("types")
                var type = ""
                for (i in 0 until typeArr.length()) {
                    type += typeArr.getJSONObject(i).getJSONObject("type").getString("name")
                    if (i < typeArr.length() - 1) {
                        type += ", "
                    }
                }

                tvName.text = name
                tvType.text = type
                client[spriteUrl, params, object : BinaryHttpResponseHandler() {
                    override fun onSuccess(p0: Int, p1: Headers?, res: Response) {
                        val stream = res.body?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val drawable = bitmap.toDrawable(resources)
                        drawable.isFilterBitmap = false
                        drawable.setAntiAlias(false)
                        iv.setImageDrawable(drawable)
                    }
                    override fun onFailure(p0: Int, p1: Headers?, p2: String?, p3: Throwable?) {
                        println("Failed to fetch image!")
                    }
                }]
            }

            override fun onFailure(p0: Int, p1: Headers?, p2: String?, p3: Throwable?) {
                println("OOF")
            }
        }]
    }
}