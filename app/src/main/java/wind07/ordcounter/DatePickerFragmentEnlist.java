package wind07.ordcounter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragmentEnlist extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year;
        int month;
        int day;
        String date;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("wind07.ordcounter", 0);
        date = sharedPref.getString("enlistdate", null);

        if (date == null) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        else{
            String [] dateParts = date.split("/");
            day = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]);
            month -= 1;
            year = Integer.parseInt(dateParts[2]);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        SettingsFragment settingsFragment = new SettingsFragment();
        String type = settingsFragment.type;
        month += 1;
        String date = (day + "/" + month + "/" + year);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("wind07.ordcounter", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("enlistdate", date);
        editor.apply();
    }
}
