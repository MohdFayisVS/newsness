package edu.uw.dhan206.newsreader;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dhan206 on 4/20/17.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "DatePickerFragment";

    public static final String BUTTON_KEY = "button_key";
    public static final String DATE_KEY = "date_key";

    private OnDatePickedListener callback;

    public interface OnDatePickedListener {
        public void updateDate(String date, String button);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnDatePickedListener) context;
        }catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDatePickedListener");
        }
    }

    public static DatePickerFragment newInstance(String date, String button) {
        Bundle args = new Bundle();
        args.putString(BUTTON_KEY, button);
        args.putString(DATE_KEY, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        Bundle bundle = getArguments();
        String dateString = bundle.getString(DATE_KEY);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        Date date = new Date();
        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    // When a date is selected, update the appropriate button
    public void onDateSet(DatePicker view, int year, int month, int day) {
        callback.updateDate("" + year + "" + (month + 1) + "" + day, getArguments().getString(BUTTON_KEY));
    }
}

