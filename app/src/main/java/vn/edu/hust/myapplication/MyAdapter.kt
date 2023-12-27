package vn.edu.hust.myapplication

import android.app.AlertDialog
import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MyAdapter(private val files: ArrayList<File>): RecyclerView.Adapter<MyAdapter.ViewHolder>(){

    private lateinit var mListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class ViewHolder(val view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        val icon = view.findViewById<ImageView>(R.id.icon_view)
        val file_name = view.findViewById<TextView>(R.id.file_name)

        init {
            view.setOnClickListener {
                listener.onItemClick(view.context ,adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(layout, mListener)
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedFile = files[position]
        if (selectedFile.isFile) {
            holder.icon.setImageResource(R.drawable.baseline_insert_drive_file_24)
        }
        else if (selectedFile.isDirectory) {
            holder.icon.setImageResource(R.drawable.baseline_folder_24)
        }
        holder.file_name.text = selectedFile.name
        holder.view.setOnCreateContextMenuListener {menu, _, _ ->

            menu.setHeaderTitle("Choose!")
            menu.add(0, 0, 0, "Rename").setOnMenuItemClickListener {
                val builder = AlertDialog.Builder(holder.view.context)
                val dialogLayout = LayoutInflater.from(holder.view.context).inflate(R.layout.rename_layout, null)
                val editText = dialogLayout.findViewById<EditText>(R.id.edit_text)

                with(builder) {
                    setTitle("Rename !!")
                    setPositiveButton("OK") {dialog, which ->
                        holder.file_name.text = editText.text.toString()
                        val directory = selectedFile.parentFile
                        val newFile = File(directory, editText.text.toString())
                        selectedFile.renameTo(newFile)
                    }
                    setNegativeButton("Cancel") {dialog, which ->

                    }
                    setView(dialogLayout)
                    show()
                }
                true
            }
            menu.add(0, 0, 1, "Delete").setOnMenuItemClickListener {
                val builder = AlertDialog.Builder(holder.view.context)
                builder.setTitle("Sure ?")
                builder.setPositiveButton("OK") { dialog, which ->
                    files.removeAt(position)
                    notifyDataSetChanged()
                    selectedFile.delete()
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
                true
            }
            if (selectedFile.isFile) {
                menu.add(0, 0, 2, "Move").setOnMenuItemClickListener {

                    true
                }
            }
        }
    }
}
