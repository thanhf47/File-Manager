package vn.edu.hust.myapplication

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.text.FieldPosition


class MainActivity : AppCompatActivity() {

    lateinit var externalStorageDir: File
    lateinit var myAdapter: MyAdapter
    lateinit var myList: ArrayList<File>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT < 30) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission Denied")
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
            } else
                Log.v("TAG", "Permission Granted")
        } else {
            if (!Environment.isExternalStorageManager()) {
                Log.v("TAG", "Permission Denied")
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            } else {
                Log.v("TAG", "Permission Granted")
            }
        }

        externalStorageDir = Environment.getExternalStorageDirectory()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val files: Array<File> = externalStorageDir.listFiles()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1234) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission granted!")
            }
            else Log.v("TAG", "Permission denied!")
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
                        val path = externalStorageDir.path +"/" + editText.text.toString()
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
                                val path = externalStorageDir.path + "/" + fileName.text.toString() + ".txt"
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