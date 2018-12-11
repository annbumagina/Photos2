package com.example.ann.photos2

import android.app.Activity
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_image_detail.*
import kotlinx.android.synthetic.main.image_list.*
import org.json.JSONObject
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.Callback
import retrofit2.Response


const val EXTRA_URL = "com.example.ann.a.extra.URL"
const val EXTRA_DESC = "com.example.ann.a.extra.DESC"
const val EXTRA_LIST = "com.example.ann.a.extra.LIST"
const val EXTRA_IDS = "com.example.ann.a.extra.IDS"
const val EXTRA_TITLE = "com.example.ann.a.extra.TITLE"


class ImageList: Fragment() {
    private val LOG_TAG = ImageList::class.java.simpleName
    private var title: ArrayList<String> = ArrayList()
    private var links: ArrayList<String> = ArrayList()
    private var ids: ArrayList<Int> = ArrayList()
    private var have = false
    private var description: ArrayList<String> = ArrayList()
    lateinit var moshi: Moshi
    private val handler = Handler(Looper.getMainLooper())
    lateinit var api: PhotosApi
    lateinit var dbHelper: DBHelper

    val onClick = { id: Int ->
        if (activity?.findViewById<View>(R.id.fragment_content) != null) {
            val bundle = Bundle()
            bundle.putInt(EXTRA_ID, id)
            bundle.putString(EXTRA_URL, links[id])
            bundle.putString(EXTRA_DESC, description[id])
            bundle.putString(EXTRA_TITLE, title[id])
            bundle.putInt(EXTRA_IDS, ids[id])
            val fragobj = ImageDetailFragment()
            fragobj.setArguments(bundle)

            val transaction = getFragmentManager()!!.beginTransaction()
            transaction.replace(R.id.fragment_content, fragobj)
            transaction.commit()
        } else {
            val intent = Intent(context, ImageDetailActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_URL, links[id])
            intent.putExtra(EXTRA_DESC, description[id])
            intent.putExtra(EXTRA_TITLE, title[id])
            intent.putExtra(EXTRA_IDS, ids[id])
            startActivity(intent)
        }
    }
    var adapter: ListAdapter = ListAdapter(onClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(LOG_TAG, "onCreateView: " + {if (savedInstanceState == null) "null" else "saved"}())
        return inflater.inflate(R.layout.image_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(LOG_TAG, "onActivityCreated: ")
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.adapter = adapter
        moshi = Moshi.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/services/feeds/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        api = retrofit.create<PhotosApi>(PhotosApi::class.java)

        search.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val expr = search.text.toString()
                    val photos = api.getPhotos(expr)
                    photos.enqueue(object : Callback<Photos> {
                        override fun onResponse(call: Call<Photos>, response: Response<Photos>) {
                            if (response.isSuccessful() && response.body() != null) {
                                handler.post {
                                    setJSON(response.body()!!)
                                }
                            }
                        }

                        override fun onFailure(call: Call<Photos>, t: Throwable) {
                        }
                    })
                    return true
                }
                return false
            }
        })

        dbHelper = DBHelper(context!!)
        myPhotos.setOnClickListener {
            val db = dbHelper.getWritableDatabase()
            //db.execSQL("DELETE FROM " + "mytable")
            val c = db.query("mytable", null, null, null, null, null, null)
            title.clear()
            links.clear()
            description.clear()
            ids.clear()
            if (c.moveToFirst())
            {
                val idColIndex = c.getColumnIndex("id")
                val titleColIndex = c.getColumnIndex("title")
                val linkColIndex = c.getColumnIndex("link")
                val descColIndex = c.getColumnIndex("desc")
                var i = 0
                do
                {
                    title.add(c.getString(titleColIndex))
                    links.add(c.getString(linkColIndex))
                    description.add(c.getString(descColIndex))
                    ids.add(c.getInt(idColIndex))
                    adapter.setElement(i, title[i])
                    i++
                } while (c.moveToNext())

            }
            c.close()
            db.close()
        }

        if (savedInstanceState != null && savedInstanceState.getStringArrayList(EXTRA_LIST) != null) {
            title = savedInstanceState.getStringArrayList(EXTRA_LIST)
            links = savedInstanceState.getStringArrayList(EXTRA_URL)
            description = savedInstanceState.getStringArrayList(EXTRA_DESC)
            ids = savedInstanceState.getIntegerArrayList(EXTRA_IDS)
            for (i in 0 .. title.size - 1) {
                adapter.setElement(i, title[i])
            }
            have = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (have) {
            outState.putStringArrayList(EXTRA_LIST, title)
            outState.putStringArrayList(EXTRA_URL, links)
            outState.putStringArrayList(EXTRA_DESC, description)
            outState.putIntegerArrayList(EXTRA_IDS, ids)
        }
        Log.d(LOG_TAG, "onSaveInstanceState: ")
        super.onSaveInstanceState(outState)
    }

    fun setJSON(result: Photos) {
        val items = result.items
        title.clear()
        links.clear()
        description.clear()
        ids.clear()
        for (i in 0 .. items.size - 1) {
            links.add(items[i].media.m.substring(0, items[i].media.m.length - 5) + "c.jpg")
            description.add(items[i].tags)
            title.add(items[i].title)
            adapter.setElement(i, title[i])
            ids.add(-1)
        }
        have = true
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy: ")
        super.onDestroy()
    }

    override fun onResume() {
        Log.d(LOG_TAG, "onResume: ")
        super.onResume()
    }

    override fun onStop() {
        Log.d(LOG_TAG, "onStop: ")
        super.onStop()
    }
}