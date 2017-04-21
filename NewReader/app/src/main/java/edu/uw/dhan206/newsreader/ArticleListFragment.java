package edu.uw.dhan206.newsreader;


import android.content.Context;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static edu.uw.dhan206.newsreader.R.id.endDate;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment {

    private static final String TAG = "RecentListFragment";
    private static final String SEARCH_PARAM_KEY = "search_term";
    private static final String LIST_TYPE_KEY = "list_type";

    private NewsArticleAdapter adapter;

    private OnArticleSelectedListener callback;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    public interface OnArticleSelectedListener {
        public void onArticleSelected(NewsArticle article);
        public boolean onArticleLongClicked(NewsArticle article);
    }

    public static ArticleListFragment newInstance(String searchTerm, String listType) {
        Bundle args = new Bundle();
        ArticleListFragment fragment = new ArticleListFragment();
        args.putString(SEARCH_PARAM_KEY, searchTerm);
        args.putString(LIST_TYPE_KEY, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnArticleSelectedListener)context;
        }catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_article_list, container, false);

        adapter = new NewsArticleAdapter(this.getActivity(), new ArrayList<NewsArticle>());

        ListView articleListView = (ListView) rootView.findViewById(R.id.articleList);
        articleListView.setAdapter(adapter);
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsArticle article = (NewsArticle) parent.getItemAtPosition(position);
                callback.onArticleSelected(article);
            }
        });

        articleListView.setLongClickable(true);
        articleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                NewsArticle article = (NewsArticle) parent.getItemAtPosition(position);
                return callback.onArticleLongClicked(article);
            }
        });

        if (getArguments() != null) {
            String listType = getArguments().getString(LIST_TYPE_KEY);
            TextView listTitle = (TextView)rootView.findViewById(R.id.articleListTitle);
            listTitle.setText(listType);
            if (listType == "Top Stories") {
                fetchTopStories();
            } else if (listType == "Search Results") {
                fetchSearchResults(getArguments().getString(SEARCH_PARAM_KEY));
            }
        }

        return rootView;
    }

    // fetches the top stories
    private void fetchTopStories() {
        String urlString = "https://api.nytimes.com/svc/topstories/v2/home.json?api-key=" + getString(R.string.api_key);

        RequestQueue queue = VolleyRequestSingleton.getInstance(getActivity()).getRequestQueue();

        Request topStoriesRequest = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        adapter.clear();
                        List<NewsArticle> topStories = NewsArticle.parseNYTTopStories(response);
                        for (NewsArticle story : topStories) {
                            adapter.add(story);
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "Error: " + error.toString());
                    }
                }
        );
        queue.add(topStoriesRequest);
    }

    // fetches the results of a user search query + date range
    private void fetchSearchResults(String query) {
        String[] queryArray = query.split("-");

        String beginDateQuery = formatDateForQuerying(queryArray[1]);
        String endDateQuery = formatDateForQuerying(queryArray[2]);

        String urlString = "";
        try {
            urlString = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=" + getString(R.string.api_key) +
                    "&begin_date=" + beginDateQuery + "&end_date=" + endDateQuery + "&q=" + URLEncoder.encode(queryArray[0], "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            Log.e(TAG, uee.toString());
            return;
        }
        Log.v(TAG, "url: " + urlString);
        RequestQueue queue = VolleyRequestSingleton.getInstance(getActivity()).getRequestQueue();

        Request searchResultsRequest = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        adapter.clear();
                        List<NewsArticle> resultStories = NewsArticle.parseNYTSearch(response);
                        for (NewsArticle story : resultStories) {
                            adapter.add(story);
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "Error: " + error.toString());
                    }
                }
        );
        queue.add(searchResultsRequest);
    }

    // formats date from "EEE, d MMM yyyy" to "yyyyMMdd" for NYTimes API request query
    public String formatDateForQuerying(String dateString) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        Date temp = new Date();
        try {
            temp = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("yyyyMMdd").format(temp);
    }

    // NewsArticleAdapter for list view purposes
    public class NewsArticleAdapter extends ArrayAdapter<NewsArticle> {
        public NewsArticleAdapter(Context context, ArrayList<NewsArticle> newsArticles) {
            super(context, 0, newsArticles);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            NewsArticle newsArticle = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.article_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.headlineText = (TextView)convertView.findViewById(R.id.newsHeadline);
                viewHolder.articleDate = (TextView)convertView.findViewById(R.id.newsDate);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.headlineText.setText(newsArticle.headline);
            viewHolder.articleDate.setText(newsArticle.getDate());

            return convertView;
        }
    }

    // Viewholder
    private static class ViewHolder {
        TextView headlineText;
        TextView articleDate;
    }
}
