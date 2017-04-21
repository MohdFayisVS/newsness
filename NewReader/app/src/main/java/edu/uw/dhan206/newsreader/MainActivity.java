package edu.uw.dhan206.newsreader;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static edu.uw.dhan206.newsreader.R.id.endDate;
import static edu.uw.dhan206.newsreader.R.id.startDate;

public class MainActivity extends AppCompatActivity implements ArticleListFragment.OnArticleSelectedListener,
        PreviewFragment.OnShowFullArticleListener, SearchFragment.OnDatePickerListener, DatePickerFragment.OnDatePickedListener {

    private static final String TAG = "MainActivity";

    SearchFragment searchFragment;
    ArticleListFragment topStoriesFragment;


    // On create show recent articles and search fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        searchFragment = SearchFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rightFragment, searchFragment, "SearchFragment")
                .commit();

        topStoriesFragment = ArticleListFragment.newInstance(null, "Top Stories");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.leftFragment, topStoriesFragment, "ArticleListFragment")
                .commit();
    }

    // Handles the search button click found in the Search Fragment
    public void handleSearchClick(View v) {
        EditText query = (EditText)findViewById(R.id.searchText);
        String searchTerm = query.getText().toString();
        Button startDate = (Button)findViewById(R.id.startDate);
        Button endDate = (Button)findViewById(R.id.endDate);
        searchTerm = searchTerm + "-" + startDate.getText().toString() + "-" + endDate.getText().toString();
        ArticleListFragment searchResultFragment = ArticleListFragment.newInstance(searchTerm, "Search Results");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.leftFragment, searchResultFragment, "ArticleListFragment")
                .addToBackStack(null)
                .commit();
    }

    // Creates the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Options menu logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.searchMenu:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.rightFragment, searchFragment, "SearchFragment")
                        .addToBackStack(null)
                        .commit();

                return true;
            case R.id.topStoryMenu:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.leftFragment, topStoriesFragment, "ArticleListFragment")
                        .addToBackStack(null)
                        .commit();

                FrameLayout rightFragment = (FrameLayout)findViewById(R.id.rightFragment);
                rightFragment.removeAllViews();

                return true;
            default:
                Log.v(TAG, "Defaulted");
                return super.onOptionsItemSelected(item);
        }
    }

    // handles the click action
    // @param article, the NewsARticle that was clicked
    @Override
    public void onArticleSelected(NewsArticle article) {
        PreviewFragment previewFragment = PreviewFragment.newInstance(article);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rightFragment, previewFragment, "PreviewFragment")
                .addToBackStack(null)
                .commit();
    }

    // handles the long click action
    // @param article, the NewsArticle that was long clicked
    @Override
    public boolean onArticleLongClicked(NewsArticle article) {
        showFullArticleInModal(article.webUrl);
        return true;
    }

    // show article in a webview modal
    // @param url, the url of the article
    @Override
    public void showFullArticleInModal(String url) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        WebView wv = new WebView(this);
        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    // show the datePicker dialog with the appropriate dates as default
    // @param button, either the startDate or endDate button
    // @param date, the current date stored in the button
    @Override
    public void showDatePickerDialog(String button, String date) {
        DialogFragment datePickerFragment = DatePickerFragment.newInstance(button, date);
        datePickerFragment.show(getSupportFragmentManager(), "DatePickerFragment");
    }

    // Updates the appropriate date button when a new date is selected
    // @param date, the string date in format "yyyyMdd"
    // @param button, either the startDate or endDate button
    @Override
    public void updateDate(String date, String button) {
        String formattedDate = formatDateForDisplaying(date);
        Button dateButton;
        if (button == "startDate") { // startDate Button
            dateButton = (Button)findViewById(R.id.startDate);
        } else { // endDate Button
            dateButton = (Button)findViewById(R.id.endDate);
        }
        dateButton.setText(formattedDate);
    }

    // formats date from "yyyyMdd" to "EEE, d MMM yyyy" for displaying purposes
    public String formatDateForDisplaying(String dateString) {
        Log.v(TAG, dateString);
        DateFormat df = new SimpleDateFormat("yyyyMdd");
        Date temp = new Date();
        try {
            temp = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("EEE, d MMM yyyy").format(temp);
    }
}
