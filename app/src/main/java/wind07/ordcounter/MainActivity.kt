package wind07.ordcounter

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        calOrdDays()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onResume() {
        super.onResume()
        calOrdDays()
    }

    private fun calOrdDays (){
        val sharedPref: SharedPreferences = getSharedPreferences("wind07.ordcounter", 0)
        val orddate = sharedPref.getString("orddate", null)
        if(orddate == null)
            numOrd.text = getString(R.string.notset)
        else{
            val todaydate = Date()
            val format = SimpleDateFormat("dd/MM/yyyy")
            val days = TimeUnit.DAYS.convert(format.parse(orddate).time - todaydate.time, TimeUnit.MILLISECONDS)
            numOrd.text = days.toString()
        }
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
}
