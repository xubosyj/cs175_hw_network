package xubo.translator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.gson.GsonBuilder
import okhttp3.*
import xubo.translator.api.JsonRootBean
import xubo.translator.ui.SearchEditText
import java.io.IOException




class MainActivity : AppCompatActivity() {
    var requestBtn: Button? = null
    var showText: TextView? = null
    var query: String? = null

    val client: OkHttpClient = OkHttpClient
        .Builder()
        .build()

    val gson = GsonBuilder().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestBtn = findViewById(R.id.btn)
        showText = findViewById(R.id.show_text)

        requestBtn?.setOnClickListener {
            showText?.text = ""
            click()
        }


        val searchEdit = findViewById<SearchEditText>(R.id.search)

        searchEdit.addTextChangedListener(object : SearchEditText.Listener {
            override fun onChanged(content: String) {
                query = content
            }
        })


    }

    fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "wuzhi")
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun click() {
        val url = "https://dict.youdao.com/jsonapi?q=" + query
        request(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showText?.text = e.message
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()

                val wordBean = gson.fromJson(bodyString, JsonRootBean::class.java)


                showText?.post { showText?.text = "${showText?.text.toString()} \n\n\n" +
                        "Query: ${wordBean.web_trans.web_translation[0].key} \n" +
                        "Value0: ${wordBean.web_trans.web_translation[0].trans[0].value} \n" +
                        "Value1: ${wordBean.web_trans.web_translation[0].trans[1].value} \n" +
                        "Value2: ${wordBean.web_trans.web_translation[0].trans[2].value} \n"
                                        }

//                showText?.text = "${showText?.text.toString()} \n\n\n" +
//                        "Query: ${wordBean.web_trans.web_translation[0].key} \n" +
//                        "Value1: ${wordBean.web_trans.web_translation[0].trans[0].value} "
            }
        })
    }
}