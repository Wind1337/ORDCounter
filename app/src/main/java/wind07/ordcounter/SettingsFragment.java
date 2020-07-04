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
        SharedPreferences sharedPref = getActivity().getSharedPreferences("wind07.ordcounter", 0);
        final String enlistdate = sharedPref.getString("enlistdate", null);
        final String orddate = sharedPref.getString("orddate", null);
        Preference prefVerNum = findPreference("vernum");
        if (prefVerNum != null) {
            prefVerNum.setSummary(versionNum);
        }
        Preference prefBuildNum = findPreference("buildnum");
        if (prefBuildNum != null) {
            prefBuildNum.setSummary(buildNum);
        }
        Preference enlistdatePreference = findPreference("enlistdate");
        Preference orddatePreference = findPreference("orddate");
        if (enlistdatePreference != null) {
            enlistdatePreference.setSummaryProvider(new Preference.SummaryProvider<Preference>() {
                @Override
                public CharSequence provideSummary(Preference preference) {
                    if (enlistdate == null){
                        return getString(R.string.pref_sum_no_enlist_date_set);
                    }
                    return enlistdate;
                }
            });
        }
        if (orddatePreference != null) {
            orddatePreference.setSummaryProvider(new Preference.SummaryProvider<Preference>() {
                @Override
                public CharSequence provideSummary(Preference preference) {
                    if (orddate == null){
                        return getString(R.string.pref_sum_no_ord_date_set);
                    }
                    return orddate;
                }
            });
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
        if(type.equals("orddate")) {
            DialogFragment newFragment = new DatePickerFragmentOrd();
            newFragment.show(getParentFragmentManager(), "datePicker");
        }
        else{
            DialogFragment newFragment = new DatePickerFragmentEnlist();
            newFragment.show(getParentFragmentManager(), "datePicker");
        }
    }
}
