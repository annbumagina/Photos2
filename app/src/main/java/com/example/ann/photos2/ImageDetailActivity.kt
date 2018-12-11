package com.example.ann.photos2

import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_image_detail.*
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.squareup.picasso.Picasso

const val EXTRA_ID = "com.example.ann.a.extra.ID"

class ImageDetailActivity: AppCompatActivity() {

    private val LOG_TAG = ImageDetailActivity::class.java.simpleName
    lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onCreate Image Detail: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)
        if (intent.getStringExtra(EXTRA_DESC) != null) {
            textView2.text = intent.getStringExtra(EXTRA_DESC)
            Picasso.get().load(intent.getStringExtra(EXTRA_URL)).into(imageDetailView);
            val ids = intent.getIntExtra(EXTRA_IDS, -1)
            dbHelper = DBHelper(this)
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
                    cv.put("title", intent.getStringExtra(EXTRA_TITLE))
                    cv.put("desc", intent.getStringExtra(EXTRA_DESC))
                    cv.put("link", intent.getStringExtra(EXTRA_URL))
                    db.insert("mytable", null, cv)
                }
                dbHelper.close()
            }
        }
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy: ")
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            val intent = NavUtils.getParentActivityIntent(this)
            intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            NavUtils.navigateUpTo(this, intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}