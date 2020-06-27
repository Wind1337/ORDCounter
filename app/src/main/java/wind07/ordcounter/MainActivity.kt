@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)

package wind07.ordcounter

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {
    lateinit var todayQuote: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        calOrdDays()
        calProgress()
        todayQuote = "The quote is still initialising"
        getQuote()

        fab.setOnClickListener{ view ->
            Snackbar.make(view, todayQuote, 6000)
                .setAction("Action", null).show()
        }
    }

    override fun onResume() {
        super.onResume()
        errorNoDates.text = ""
        calOrdDays()
        calProgress()
    }

    private fun calOrdDays (){
        val sharedPref: SharedPreferences = getSharedPreferences("wind07.ordcounter", 0)
        val orddate = sharedPref.getString("orddate", null)
        if(orddate == null)
        {
            errorNoDates.setBackgroundResource(R.drawable.txtborder)
            numOrd.text = getString(R.string.notset)
            errorNoDates.text = getString(R.string.no_date_set)
        }
        else{
            errorNoDates.setBackgroundResource(0)
            val todaydate = Date()
            val format = SimpleDateFormat("dd/MM/yyyy")
            val days = TimeUnit.DAYS.convert(format.parse(orddate).time - todaydate.time, TimeUnit.MILLISECONDS)
            numOrd.text = days.toString()
        }
    }

    private fun calProgress(){
        val sharedPref: SharedPreferences = getSharedPreferences("wind07.ordcounter", 0)
        val enlistdate = sharedPref.getString("enlistdate", null)
        val todaydate = Date()
        val orddate = sharedPref.getString("orddate", null)
        if(orddate == null)
        {
            numOrd.text = getString(R.string.notset)
            errorNoDates.text = getString(R.string.no_date_set)
        }
        else if (enlistdate == null){
            errorNoDates.text = "Click here to set an enlistment date"
            errorNoDates.setBackgroundResource(R.drawable.txtborder)
        }
        else{
            errorNoDates.setBackgroundResource(0)
            val format = SimpleDateFormat("dd/MM/yyyy")
            val ordDays = (TimeUnit.DAYS.convert(format.parse(orddate).time - todaydate.time, TimeUnit.MILLISECONDS)).toDouble()
            val servicedays = (TimeUnit.DAYS.convert(format.parse(orddate).time - format.parse(enlistdate).time, TimeUnit.MILLISECONDS)).toDouble()
            val serviceprogress = (ceil(100 -((ordDays/servicedays) * 100))).toInt()
            ObjectAnimator.ofInt(progressBar, "progress", serviceprogress)
                .setDuration(600)
                .start()
            ordPercent.text = ("$serviceprogress% completed")
        }

    }

    private fun getQuote() {
        val apiParams = RequestParams()
        apiParams.put("language", "en")
        APIWrapper.get("qod", apiParams, object:JsonHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                Log.d("qotdJSONDebug", "look: " + response!!.toString())
                var obj = response
                val contents = response.getJSONObject("contents")
                val quotes = contents.getJSONArray("quotes")
                val quotesObj = quotes.getJSONObject(0)
                val qotd = quotesObj.getString("quote")
                val author = quotesObj.getString("author")
                Log.d("qotdJSONDebug", "$qotd - $author")
                val combinedQuote = "$qotd - $author"
                todayQuote = combinedQuote
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>?, e: Throwable, response: JSONObject?)
            {
                Log.d("qotdJSONDebug", "FAIL: " + response!!.toString())
                val errorMessage = "There was an issue with retrieving today's quote"
                todayQuote = errorMessage
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    fun errorNoDates(view: View) {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }
}
