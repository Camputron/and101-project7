package com.example.and101_project5

import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.BinaryHttpResponseHandler
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import okhttp3.Response
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(
    private val dataSet: Array<String>,
    private val displayMonster: (String, TextView, TextView, ImageView) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: CardView = view.findViewById(R.id.cardView)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvType: TextView = view.findViewById(R.id.tvType)
        val iv: ImageView = view.findViewById(R.id.iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.text_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val monster = dataSet[position]
        println("Display $monster")
        displayMonster(monster, holder.tvName, holder.tvType, holder.iv)
    }

    override fun getItemCount() = dataSet.size
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val monsters = arrayOf("ditto", "bulbasaur", "treecko", "exeggcute", "pikachu", "gengar", "palkia", "slowbro", "metagross", "dialga")
        val customAdapter = CustomAdapter(monsters, ::displayMonster)
        val recyclerView: RecyclerView = findViewById(R.id.reycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }

    private fun displayMonster(monster: String, tvName: TextView, tvType: TextView, iv: ImageView) {
        val client = AsyncHttpClient()
        val params = RequestParams()
        client["https://pokeapi.co/api/v2/pokemon/${monster}", params, object : JsonHttpResponseHandler() {
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