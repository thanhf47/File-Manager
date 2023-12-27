package vn.edu.hust.myapplication

import android.content.Context

interface OnItemClickListener {
    fun onItemClick(context: Context, position: Int)
}