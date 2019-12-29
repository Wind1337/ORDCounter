package wind07.ordcounter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);

        Preference preference = findPreference("orddate");
        assert preference != null;
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDatePickerDialog();
                return true;
            }
        });
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        assert getFragmentManager() != null;
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
