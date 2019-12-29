package wind07.ordcounter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_settings, SettingsFragment())
            .commit()
    }
}
