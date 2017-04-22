package edu.uw.dhan206.newsreader;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private OnDatePickerListener callback;

    public Button startDate;
    public Button endDate;

    public SearchFragment() {
        // Required empty public constructor
    }

    public interface OnDatePickerListener {
        public void showDatePickerDialog(String button, String date);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnDatePickerListener) context;
        }catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDatePickerListener");
        }
    }

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // set appropriate dates to the button text
        startDate = (Button) rootView.findViewById(R.id.startDate);
        endDate = (Button) rootView.findViewById(R.id.endDate);
        Calendar today = Calendar.getInstance();
        final String todayFormatted = new SimpleDateFormat("EEE, d MMM yyyy").format(today.getTime());
        endDate.setText(todayFormatted);
        today.add(Calendar.DAY_OF_YEAR, -7); // a week before
        final String weekAgoFormatted = new SimpleDateFormat("EEE, d MMM yyyy").format(today.getTime());
        startDate.setText(weekAgoFormatted);

        // startDate button click listener
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.showDatePickerDialog(weekAgoFormatted, "startDate");
            }
        });

        // endDate button click listener
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.showDatePickerDialog(todayFormatted, "endDate");
            }
        });

        return rootView;
    }
}
