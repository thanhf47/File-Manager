package vn.edu.hust.myapplication

import android.annotation.SuppressLint
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.w3c.dom.Text
import java.io.File
import java.io.InputStreamReader

class MainActivity3 : AppCompatActivity() {

    lateinit var file_path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val textView = findViewById<TextView>(R.id.text_view)
        val imageView = findViewById<ImageView>(R.id.image_view)
        file_path = intent.getStringExtra("file_path").toString()
        val file = File(file_path)
        if (file.extension == "txt") {
            textView.visibility = View.VISIBLE
            imageView.visibility = View.INVISIBLE
            val reader = file.inputStream().reader()
            val content = reader.readText()
            reader.close()

            textView.setText(content)
        }
        else if (file.extension in arrayOf("bmp", "jpg", "jpeg", "png")) {
            textView.visibility = View.INVISIBLE
            imageView.visibility = View.VISIBLE

            Glide.with(this)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()
                .into(imageView)
        }

    }
}