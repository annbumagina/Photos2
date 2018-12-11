package com.example.ann.photos2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_detail.*
import android.support.v4.content.ContextCompat
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase







class ImageDetailFragment: Fragment() {

    private var desc: String? = null
    private var link: String? = null
    private var i: Int = 0
    private var ids: Int = 0
    private var title: String? = null
    lateinit var dbHelper: DBHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (arguments != null) {
            desc = arguments!!.getString(EXTRA_DESC)
            link = arguments!!.getString(EXTRA_URL)
            i = arguments!!.getInt(EXTRA_ID)
            ids = arguments!!.getInt(EXTRA_IDS)
            title = arguments!!.getString(EXTRA_TITLE)
        }
        return inflater.inflate(R.layout.activity_image_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (desc != null) {
            textView2.text = desc
            Picasso.get().load(link).into(imageDetailView)
            dbHelper = DBHelper(context!!)
            if (ids >= 0) {
                btn.text = "Remove"
            } else {
                btn.text = "Add"
            }
            btn.setOnClickListener {
                val db = dbHelper.writableDatabase
                if (ids >= 0) {
                    db.delete("mytable", "id = " + ids, null);
                } else {
                    val cv = ContentValues()
                    cv.put("title", title)
                    cv.put("desc", desc)
                    cv.put("link", link)
                    db.insert("mytable", null, cv)
                }
                dbHelper.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}