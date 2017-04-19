package edu.uw.dhan206.newsreader;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private String fromDate;
    private String toDate;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        Button startDate = (Button) rootView.findViewById(R.id.startDate);
        Button endDate = (Button) rootView.findViewById(R.id.endDate);
        Calendar today = Calendar.getInstance();
        String todayFormatted = new SimpleDateFormat("yyyyMMdd").format(today.getTime());
        endDate.setText(todayFormatted);
        today.add(Calendar.DAY_OF_YEAR, -7); // a week before
        String weekAgoFormatted = new SimpleDateFormat("yyyyMMdd").format(today.getTime());
        startDate.setText(weekAgoFormatted);

        return rootView;
    }


}
