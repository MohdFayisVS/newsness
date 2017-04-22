package edu.uw.dhan206.newsreader;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewFragment extends Fragment {

    private static final String TAG = "PreviewFragment";
    private static final String HEADLINE_KEY = "ArticleHeadlineKey";
    private static final String DATE_KEY = "ArticleDateKey";
    private static final String SNIPPET_KEY = "ArticleSnippetKey";
    private static final String IMG_KEY = "ArticleImageKey";
    private static final String URL_KEY = "ArticleUrlKey";

    private OnShowFullArticleListener callback;

    Bundle bundle;
    ImageView articleImage;

    public interface OnShowFullArticleListener {
        public void showFullArticleInModal(String Url);
    }

    public PreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnShowFullArticleListener) context;
        }catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnShowFullArticleListener");
        }
    }

    public static PreviewFragment newInstance(NewsArticle article) {
        Bundle args = new Bundle();
        PreviewFragment fragment = new PreviewFragment();
        args.putString(HEADLINE_KEY, article.headline);
        args.putString(DATE_KEY, article.getDate());
        args.putString(SNIPPET_KEY, article.snippet);
        args.putString(IMG_KEY, article.imageUrl);
        args.putString(URL_KEY, article.webUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_preview, container, false);
        articleImage = (ImageView)rootView.findViewById(R.id.previewImage);

        bundle = getArguments();
        if (bundle != null) {
            TextView previewHeadline = (TextView)rootView.findViewById(R.id.previewHeadline);
            previewHeadline.setText(bundle.getString(HEADLINE_KEY));

            TextView previewDate = (TextView)rootView.findViewById(R.id.previewDate);
            previewDate.setText(bundle.getString(DATE_KEY));

            TextView previewSnippet = (TextView)rootView.findViewById(R.id.previewSnippet);
            previewSnippet.setText(bundle.getString(SNIPPET_KEY));

            fetchAndReplaceImage(bundle.getString(IMG_KEY));

            Button previewFullArticle = (Button)rootView.findViewById(R.id.previewFullArticle);
            Linkify.addLinks(previewFullArticle, Linkify.WEB_URLS);
            previewFullArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.showFullArticleInModal(bundle.getString(URL_KEY));
                }
            });
        }

        return rootView;
    }

    // fetches the article's image and replaces the default image
    public void fetchAndReplaceImage(String imgSrc) {
        if(imgSrc != null && !imgSrc.isEmpty()) {
            ImageLoader imageLoader = VolleyRequestSingleton.getInstance(getActivity()).getImageLoader();
            imageLoader.get(imgSrc, ImageLoader.getImageListener(articleImage, 0, 0));
        }
    }
}
