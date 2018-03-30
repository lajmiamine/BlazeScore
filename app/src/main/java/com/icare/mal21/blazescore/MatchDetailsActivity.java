package com.icare.mal21.blazescore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.icare.mal21.blazescore.bareclasses.Comment;
import com.icare.mal21.blazescore.bareclasses.Commentaries;
import com.icare.mal21.blazescore.bareclasses.Match;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchDetailsActivity extends AppCompatActivity {

    TextView home_team_name,home_team_score, time_match, away_team_score, away_team_name, latest_update;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private String urlPost;
    private String HomeTeam, HomeTeamScore, HomeTeamID, AwayTeam, AwayTeamScore, AwayTeamID, Timer, MatchID;
    private ProgressDialog progressDialog;
    private CommentariesAdapter recyclerViewAdapter;
    private List<Commentaries> commentaries;
    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        Intent intent = getIntent();
        HomeTeam = intent.getStringExtra("HomeTeam");
        HomeTeamScore = intent.getStringExtra("HomeTeamScore");
        AwayTeam = intent.getStringExtra("AwayTeam");
        AwayTeamScore = intent.getStringExtra("AwayTeamScore");
        Timer = intent.getStringExtra("Timer");
        MatchID = intent.getStringExtra("MatchID");

        home_team_name = (TextView) findViewById(R.id.home_team_name);
        home_team_name.setText(HomeTeam);
        home_team_score = (TextView) findViewById(R.id.home_team_score);
        home_team_score.setText(HomeTeamScore);
        time_match = (TextView) findViewById(R.id.time_match);
        time_match.setText(Timer);
        away_team_name = (TextView) findViewById(R.id.away_team_name);
        away_team_name.setText(AwayTeam);
        away_team_score = (TextView) findViewById(R.id.away_team_score);
        away_team_score.setText(AwayTeamScore);
        latest_update = (TextView) findViewById(R.id.latest_update);

        recyclerViewDesign();

    }

    private void recyclerViewDesign() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMatcheScorers);

        // Divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(android.R.drawable.divider_horizontal_bright)));

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        //String competition = sharedPreferences.getString("Competition","ALL");

        urlPost="http://api.football-api.com/2.0/commentaries/" +
                MatchID +
                "?Authorization=565ec012251f932ea400000184dcb74622bb4ce6618b8f80546ebab5";

        Log.e("urlPost",urlPost);
        PostFetcher fetcher = new PostFetcher();
        fetcher.execute();

    }

    private class PostFetcher extends AsyncTask<Void, Void, String> {
        private static final String TAG = "PostFetcher";
        //public static final String SERVER_URL = "http://kylewbanks.com/rest/posts.json";

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MatchDetailsActivity.this);
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
                        JsonParser parser = new JsonParser();
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            builder.append(line + "\n");
                        }
                        JSONObject jo = new JSONObject(builder.toString());
                        JSONArray ja = jo.getJSONArray("comments");
                        Log.e("comment",ja.toString());
                        comments = Arrays.asList(gson.fromJson(ja.toString(), Comment[].class));
                        //Log.e("commentaries size",""+commentaries.size());
                        //List<Comment> comments = (List<Comment>) commentaries.get(0).comments;
                        content.close();

                        handlePostsList(comments);
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

    private void handlePostsList(final List<Comment> comments) {
        this.comments = comments;
        Log.e("Comments size", String.valueOf(comments.size()));

        final List<Comment> goals = new ArrayList<>();

        for (int j=0;j<comments.size();j++){
            if (comments.get(j).isgoal.equals("1")){
                goals.add(comments.get(j));
            }
        }

        Log.e("Goals size", String.valueOf(goals.size()));

        runOnUiThread(new Runnable() {
            public SharedPreferences sharedPreferences;

            @Override
            public void run() {
                boolean important=false;
                int i=comments.size();
                while (important==false || i==1){
                    //Log.e("important",comments.get(i-1).important);
                    if (comments.get(i-1).important.equals("1")){
                        latest_update.setText(comments.get(i).comment);
                        latest_update.setTextColor(Color.RED);
                        important=true;
                    }
                    i--;
                }

                sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);

                recyclerViewAdapter = new CommentariesAdapter(getBaseContext(), goals, HomeTeam, AwayTeam);
                recyclerView.setAdapter(recyclerViewAdapter);

            }
        });
    }

    private void failedLoadingPosts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to load information.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
