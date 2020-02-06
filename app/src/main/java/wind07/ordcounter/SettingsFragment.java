package wind07.ordcounter;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    public String type;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
        String buildNum = Integer.toString(BuildConfig.VERSION_CODE);
        String versionNum = BuildConfig.VERSION_NAME;
        Preference prefVerNum = findPreference("vernum");
        if (prefVerNum != null) {
            prefVerNum.setSummary(versionNum);
        }
        Preference prefBuildNum = findPreference("buildnum");
        if (prefBuildNum != null) {
            prefBuildNum.setSummary(buildNum);
        }

        ordPrefListener();
        enlistPrefListener();
    }

    public void ordPrefListener(){
        Preference preference = findPreference("orddate");
        assert preference != null;
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                type = "orddate";
                showDatePickerDialog();
                return true;
            }
        });
    }

    public void enlistPrefListener(){
        Preference preference = findPreference("enlistdate");
        assert preference != null;
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                type = "enlistdate";
                showDatePickerDialog();
                return true;
            }
        });
    }

    public void showDatePickerDialog() {
        if(type == "orddate") {
            DialogFragment newFragment = new DatePickerFragmentOrd();
            assert getFragmentManager() != null;
            newFragment.show(getFragmentManager(), "datePicker");
        }
        else{
            DialogFragment newFragment = new DatePickerFragmentEnlist();
            assert getFragmentManager() != null;
            newFragment.show(getFragmentManager(), "datePicker");
        }
    }
}
