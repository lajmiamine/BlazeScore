package com.icare.mal21.blazescore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icare.mal21.blazescore.bareclasses.Match;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mal21 on 23/08/2016.
 */
public class FragmentMatches extends android.support.v4.app.Fragment {

    private View view;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private String urlPost;
    private ProgressDialog progressDialog;
    private List<Match> matches;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int recyclerViewPaddingTop;
    private MatchesAdapter recyclerViewAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        // Get shared preferences
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        getActivity().setTitle("BlazeScore");

        // Setup RecyclerView News
        recyclerViewDesign(view);

        // Setup swipe to refresh
        swipeToRefresh(view);

        return view;
    }

    private void recyclerViewDesign(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMatches);

        // Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(android.R.drawable.divider_horizontal_bright)));

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        String competition = sharedPreferences.getString("Competition","ALL");

        urlPost="http://api.football-api.com/2.0/matches?from_date=" +
                android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                "&to_date=" +
                android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                "&Authorization=" +
                "565ec012251f932ea400000184dcb74622bb4ce6618b8f80546ebab5";

        switch (competition){
            case "All":
                urlPost="http://api.football-api.com/2.0/matches?from_date=" +
                        android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                        "&to_date=" +
                        android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                        "&Authorization=" +
                        "565ec012251f932ea400000184dcb74622bb4ce6618b8f80546ebab5";
                Log.e("urlpost",urlPost);
                break;
            case "ENGLAND":
                urlPost="http://api.football-api.com/2.0/matches?comp_id=1204&from_date=" +
                        android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                        "&to_date=" +
                        android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                        "&Authorization=" +
                        "565ec012251f932ea400000184dcb74622bb4ce6618b8f80546ebab5";
                Log.e("urlpost",urlPost);
                break;
            case "SPAIN":
                urlPost="http://api.football-api.com/2.0/matches?comp_id=1399&from_date=" +
                        android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                        "&to_date=" +
                        android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                        "&Authorization=" +
                        "565ec012251f932ea400000184dcb74622bb4ce6618b8f80546ebab5";
                Log.e("urlpost",urlPost);
                break;
            case "ITALY":
                urlPost="http://api.football-api.com/2.0/matches?comp_id=1269&from_date=" +
                        android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                        "&to_date=" +
                        android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()) +
                        "&Authorization=" +
                        "565ec012251f932ea400000184dcb74622bb4ce6618b8f80546ebab5";
                Log.e("urlpost",urlPost);
                break;
        }

        Log.e("urlpost",urlPost);
        PostFetcher fetcher = new PostFetcher();
        fetcher.execute();

    }

    private class PostFetcher extends AsyncTask<Void, Void, String> {
        private static final String TAG = "PostFetcher";
        //public static final String SERVER_URL = "http://kylewbanks.com/rest/posts.json";

        @Override
        protected void onPreExecute() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Fetching data...");
                    progressDialog.show();
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //Create an HTTP client
                HttpClient client = new DefaultHttpClient();
                HttpGet post = new HttpGet(urlPost);

                //Perform the request and check the status code
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    try {
                        //Read the server response and attempt to parse it as JSON
                        Reader reader = new InputStreamReader(content);

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                        Gson gson = gsonBuilder.create();
                        List<Match> matchArrayList = new ArrayList<>();
                        matchArrayList = Arrays.asList(gson.fromJson(reader, Match[].class));
                        content.close();
                        Log.e("Matches size", String.valueOf(matchArrayList.size()));

                        handlePostsList(matchArrayList);
                    } catch (Exception ex) {
                        Log.e(TAG, "Failed to parse JSON due to: " + ex);
                        failedLoadingPosts();
                    }
                } else {
                    Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                    failedLoadingPosts();
                }
            } catch(Exception ex) {
                Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
                failedLoadingPosts();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
        }
    }

    private void handlePostsList(final List<Match> matches) {
        this.matches = matches;
        Log.e("Matches size", String.valueOf(matches.size()));

        getActivity().runOnUiThread(new Runnable() {
            //public SharedPreferences haredPreferences;

            @Override
            public void run() {

                sharedPreferences = getContext().getSharedPreferences("VALUES", Context.MODE_PRIVATE);

                recyclerViewAdapter = new MatchesAdapter(getActivity(), matches);
                recyclerView.setAdapter(recyclerViewAdapter);

                swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void failedLoadingPosts() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to load information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void swipeToRefresh(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        int start = recyclerViewPaddingTop - convertToPx(48), end = recyclerViewPaddingTop + convertToPx(16);
        swipeRefreshLayout.setProgressViewOffset(true, start, end);
        TypedValue typedValueColorPrimary = new TypedValue();
        TypedValue typedValueColorAccent = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValueColorPrimary, true);
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, typedValueColorAccent, true);
        final int colorPrimary = typedValueColorPrimary.data, colorAccent = typedValueColorAccent.data;
        swipeRefreshLayout.setColorSchemeColors(colorPrimary, colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                matches =new ArrayList<>();
                PostFetcher fetcher = new PostFetcher();
                fetcher.execute();
            }
        });
    }

    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
