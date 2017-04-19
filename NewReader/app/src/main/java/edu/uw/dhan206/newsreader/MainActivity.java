package edu.uw.dhan206.newsreader;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements ArticleListFragment.OnArticleSelectedListener {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SearchFragment searchFragment = SearchFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rightFragment, searchFragment, "SearchFragment")
                .addToBackStack(null)
                .commit();

        ArticleListFragment topStoriesFragment = ArticleListFragment.newInstance(null, "Top Stories");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.leftFragment, topStoriesFragment, "ArticleListFragment")
                .addToBackStack(null)
                .commit();
    }



    public void handleSearchClick(View v) {
        EditText query = (EditText)findViewById(R.id.searchText);
        String searchTerm = query.getText().toString();
        Button startDate = (Button)findViewById(R.id.startDate);
        Button endDate = (Button)findViewById(R.id.endDate);
        searchTerm = searchTerm + "-" + startDate.getText().toString() + "-" + endDate.getText().toString();
        ArticleListFragment fragment = ArticleListFragment.newInstance(searchTerm, "Search Results");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.leftFragment, fragment, "ArticleListFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onArticleSelected(NewsArticle article) {

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Log.v(TAG, "" + year + "" + (month + 1) + "" + day);
        }
    }
}
