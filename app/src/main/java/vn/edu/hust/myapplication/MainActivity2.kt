package vn.edu.hust.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.IOException

class MainActivity2 : AppCompatActivity() {
    lateinit var root: String
    lateinit var myList: ArrayList<File>
    lateinit var myAdapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        root = intent.getStringExtra("dir_path").toString()
        val files = File(root).listFiles()

        val textView = findViewById<TextView>(R.id.textView)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (!files.isNullOrEmpty() && files.size != 0) {
            textView.visibility = View.INVISIBLE
            recyclerView.visibility = View.VISIBLE
            myList = ArrayList<File>()
            myList.addAll(files)
            myAdapter = MyAdapter(myList)
            recyclerView.adapter = myAdapter

            myAdapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(context: Context, position: Int) {
                    val selectedFile = files[position]
                    if (selectedFile.isFile) {
                        val intent = Intent(context, MainActivity3::class.java)
                        val path = selectedFile.absolutePath
                        intent.putExtra("file_path", path)
                        startActivity(intent)
                    } else if (selectedFile.isDirectory) {
                        val intent = Intent(context, MainActivity2::class.java)
                        val path = selectedFile.absolutePath
                        intent.putExtra("dir_path", path)
                        startActivity(intent)
                    }
                }
            })
        }
        else {
            textView.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
        }

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_folder -> {
                val builder = AlertDialog.Builder(this)
                val dialogLayout = LayoutInflater.from(this).inflate(R.layout.rename_layout, null)
                val editText = dialogLayout.findViewById<EditText>(R.id.edit_text)

                with(builder) {
                    setTitle("Name?")
                    setPositiveButton("OK") {dialog, which ->
                        val path = root +"/" + editText.text.toString()
                        createNewFolder(path)
                        myList.add(File(path))
                        myAdapter.notifyDataSetChanged()
                    }
                    setNegativeButton("Cancel", null)
                    setView(dialogLayout)
                    show()
                }
                Toast.makeText(this, "add folder", Toast.LENGTH_LONG).show()
            }
            R.id.add_text -> {
                val builder = AlertDialog.Builder(this)
                val dialogLayout = LayoutInflater.from(this).inflate(R.layout.rename_layout, null)
                val fileName = dialogLayout.findViewById<EditText>(R.id.edit_text)

                with(builder) {
                    setTitle("Name?")
                    setPositiveButton("OK") {dialog, which ->
                        val myBuilder = AlertDialog.Builder(context)
                        val myDialog = LayoutInflater.from(context).inflate(R.layout.edit_text_layout, null)
                        val content = myDialog.findViewById<EditText>(R.id.my_content)
                        with(myBuilder) {
                            setTitle("Content?")
                            setPositiveButton("Save") {dialog, which ->
                                val path = root + "/" + fileName.text.toString() + ".txt"
                                createAndSaveTextFile(fileName.text.toString(), content.text.toString())

                                myList.add(File(path))
                                myAdapter.notifyDataSetChanged()
                            }
                            setNegativeButton("Cancel", null)
                            setView(myDialog)
                            show()
                        }
                    }
                    setNegativeButton("Cancel", null)
                    setView(dialogLayout)
                    show()
                }
                Toast.makeText(this, "add text", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createAndSaveTextFile(filePath:String, content: String) {
        try {
            val file = File(filePath)
            file.createNewFile()
            val outputStream = file.outputStream()
            val writer = outputStream.writer()
            writer.write(content)
            writer.close()
            Toast.makeText(this, "Tạo và lưu tệp tin thành công", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Đã xảy ra lỗi khi tạo và lưu tệp tin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewFolder(filePath: String) {
        val file = File(filePath)
        try {
            file.mkdir()
        }catch (e: IOException) {
            e.printStackTrace()
        }
    }
}