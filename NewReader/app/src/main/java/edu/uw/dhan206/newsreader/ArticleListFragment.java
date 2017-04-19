package edu.uw.dhan206.newsreader;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.List;


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



    private void fetchSearchResults(String query) {
        String[] queryArray = query.split("-");

        String urlString = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=" + getString(R.string.api_key) +
                "&begin_date=" + queryArray[1] + "&end_date=" + queryArray[2] + "&q=" + queryArray[0];

        RequestQueue queue = VolleyRequestSingleton.getInstance(getActivity()).getRequestQueue();

        Request searchResultsRequest = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, "search results reached");
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

    public class NewsArticleAdapter extends ArrayAdapter<NewsArticle> {
        public NewsArticleAdapter(Context context, ArrayList<NewsArticle> newsArticles) {
            super(context, 0, newsArticles);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NewsArticle newsArticle = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.article_list_item, parent, false);
            }

            TextView headlineText = (TextView)convertView.findViewById(R.id.newsHeadline);
            TextView articleDate = (TextView)convertView.findViewById(R.id.newsDate);

            headlineText.setText(newsArticle.headline);
            articleDate.setText(newsArticle.getDate());

            return convertView;
        }
    }
}
