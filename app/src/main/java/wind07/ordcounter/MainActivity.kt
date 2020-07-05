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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {
    lateinit var todayQuote: String
    private lateinit var quoteCAA: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val sharedPref: SharedPreferences = getSharedPreferences("wind07.ordcounter", 0)
        val cachedQuoteCAA = sharedPref.getString("quoteCAA", null)
        val cachedQuote = sharedPref.getString("cachedQuote", null)
        when (cachedQuote) {
            null -> {
                Log.d("INFO", "Quote not found. Fetching quote.")
                todayQuote = "The quote is still initialising"
                getQuote()
            }
            else -> {
                quoteCAA = LocalDate.parse(cachedQuoteCAA)
                val today = LocalDate.now()
                when {
                    quoteCAA != today -> {
                        Log.d("INFO", "Quote out of date. Fetching quote.")
                        todayQuote = "The quote is still initialising"
                        getQuote()
                    }
                    else -> {
                        Log.d("INFO", "Valid cached quote found! Using cached quote!")
                        todayQuote = cachedQuote
                    }
                }
            }
        }
        calOrdDays()
        calProgress()
        fab.setOnClickListener{ view ->
            Snackbar.make(view, todayQuote, 6000).also{
                val snackView = it.view
                val textView = snackView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.maxLines = 5
            }
                .setAction("Action", null)
                .show()
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
        when (orddate) {
            null -> {
                errorNoDates.setBackgroundResource(R.drawable.txtborder)
                numOrd.text = getString(R.string.notset)
                errorNoDates.text = getString(R.string.no_dates_set)
            }
            else -> {
                errorNoDates.setBackgroundResource(0)
                val todaydate = Date()
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val days = TimeUnit.DAYS.convert(
                    format.parse(orddate).time - todaydate.time,
                    TimeUnit.MILLISECONDS
                )
                numOrd.text = days.toString()
            }
        }
    }

    private fun calProgress(){
        val sharedPref: SharedPreferences = getSharedPreferences("wind07.ordcounter", 0)
        val enlistdate = sharedPref.getString("enlistdate", null)
        val todaydate = Date()
        val orddate = sharedPref.getString("orddate", null)
        when {
            orddate == null -> {
                numOrd.text = getString(R.string.notset)
                errorNoDates.text = getString(R.string.no_dates_set)
            }
            enlistdate == null -> {
                errorNoDates.text = getString(R.string.no_enlist_date_set)
                errorNoDates.setBackgroundResource(R.drawable.txtborder)
            }
            else -> {
                errorNoDates.setBackgroundResource(0)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val ordDays = (TimeUnit.DAYS.convert(format.parse(orddate).time - todaydate.time, TimeUnit.MILLISECONDS)).toDouble()
                val servicedays = (TimeUnit.DAYS.convert(format.parse(orddate).time - format.parse(enlistdate).time, TimeUnit.MILLISECONDS)).toDouble()
                val serviceprogress = (ceil(100 -((ordDays/servicedays) * 100))).toInt()
                ObjectAnimator.ofInt(progressBar, "progress", serviceprogress)
                    .setDuration(600)
                    .start()
                ordPercent.text = ("$serviceprogress% completed")
            }
        }

    }

    private fun getQuote() {
        val apiParams = RequestParams()
        apiParams.put("language", "en")
        APIWrapper.get("qod", apiParams, object:JsonHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                Log.d("qotdJSONDebug", "SUCCESS: " + response!!.toString())
                val contents = response.getJSONObject("contents")
                val quotes = contents.getJSONArray("quotes")
                val quotesObj = quotes.getJSONObject(0)
                val qotd = quotesObj.getString("quote")
                val author = quotesObj.getString("author")
                Log.d("qotdJSONDebug", "$qotd - $author")
                val combinedQuote = "$qotd - $author"
                todayQuote = combinedQuote
                val caa = LocalDate.now()
                storeQuote(todayQuote, caa)
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>?, e: Throwable, response: JSONObject?)
            {
                try{
                    Log.d("qotdJSONDebug", "FAIL: " + response!!.toString())
                    val contents = response.getJSONObject("error")
                    val errorCode = contents.getString("code")
                    todayQuote = if (errorCode == "429") {
                        val errorMessage = "You are currently rate-limited by the API server. Please try again in 1 hour."
                        errorMessage
                    }
                    else {
                        val errorMessage = "There was an issue with retrieving today's quote (Unhandled error code)"
                        errorMessage
                    }
                }
                catch (e: NullPointerException){
                    Log.d("qotdJSONDebug", "FAIL: null response received")
                    val errorMessage = "There was an issue with retrieving today's quote (null response received)"
                    todayQuote = errorMessage
                }
            }
        })
    }

    private fun storeQuote(todayQuote: String, quoteCAA: LocalDate){
        val quoteDate = quoteCAA.toString()
        val sharedPref: SharedPreferences = getSharedPreferences("wind07.ordcounter", 0)
        val editor = sharedPref.edit()
        editor.putString("cachedQuote", todayQuote)
        editor.putString("quoteCAA", quoteDate)
        editor.apply()
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
