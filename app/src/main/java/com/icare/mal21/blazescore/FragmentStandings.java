package com.icare.mal21.blazescore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icare.mal21.blazescore.bareclasses.Match;
import com.icare.mal21.blazescore.bareclasses.TeamStanding;

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
public class FragmentStandings extends android.support.v4.app.Fragment {

    private View view;
    private SharedPreferences sharedPreferences;
    private TableLayout tableLayout_standings;
    private String urlPost;
    private ProgressDialog progressDialog;
    private List<TeamStanding> teamStanding;
    public String StandingsCompetition="1204";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_standings, container, false);

        getActivity().setTitle("Standings");

        setUpStandingsTable(view);

        return view;
    }

    private void setUpStandingsTable(View view) {

        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        StandingsCompetition = sharedPreferences.getString("StandingsCompetition","1204");


        urlPost="http://api.football-api.com/2.0/standings/" +
                StandingsCompetition + "?Authorization=565ec012251f932ea400000184dcb74622bb4ce6618b8f80546ebab5";

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
                        List<TeamStanding> teamStandings = new ArrayList<>();
                        teamStandings = Arrays.asList(gson.fromJson(reader, TeamStanding[].class));
                        content.close();
                        Log.e("Matches size", String.valueOf(teamStandings.size()));

                        handlePostsList(teamStandings);
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

    private void failedLoadingPosts() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to load information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePostsList(final List<TeamStanding> teamStanding) {
        this.teamStanding = teamStanding;

        tableLayout_standings = (TableLayout) view.findViewById(R.id.table_standings);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TableRow tr = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_row_item, null);

                TextView position;
                position = (TextView) tr.findViewById(R.id.position);
                position.setText("#");

                TextView team_name;
                team_name = (TextView) tr.findViewById(R.id.team);
                team_name.setText(" Team");

                TextView MP;
                MP = (TextView) tr.findViewById(R.id.MP);
                MP.setText("MP");

                TextView W;
                W = (TextView) tr.findViewById(R.id.W);
                W.setText("W");

                TextView D;
                D = (TextView) tr.findViewById(R.id.D);
                D.setText("D");

                TextView L;
                L = (TextView) tr.findViewById(R.id.L);
                L.setText("L");

                TextView GS;
                GS = (TextView) tr.findViewById(R.id.GS);
                GS.setText("GS");

                TextView GA;
                GA = (TextView) tr.findViewById(R.id.GA);
                GA.setText("GA");

                TextView GD;
                GD = (TextView) tr.findViewById(R.id.GD);
                GD.setText("GD");

                TextView P;
                P = (TextView) tr.findViewById(R.id.P);
                P.setText("P");

                tableLayout_standings.addView(tr);

                for (int i=0;i<teamStanding.size();i++){
                    TableRow tr1 = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.table_row_item, null);

                    TextView position1;
                    position1 = (TextView) tr1.findViewById(R.id.position);
                    position1.setText(teamStanding.get(i).position);

                    TextView team_name1;
                    team_name1 = (TextView) tr1.findViewById(R.id.team);
                    team_name1.setText(" "+teamStanding.get(i).team_name);

                    TextView MP1;
                    MP1 = (TextView) tr1.findViewById(R.id.MP);
                    MP1.setText(teamStanding.get(i).overall_gp);

                    TextView W1;
                    W1 = (TextView) tr1.findViewById(R.id.W);
                    W1.setText(" "+teamStanding.get(i).overall_w);

                    TextView D1;
                    D1 = (TextView) tr1.findViewById(R.id.D);
                    D1.setText(" "+teamStanding.get(i).overall_d);

                    TextView L1;
                    L1 = (TextView) tr1.findViewById(R.id.L);
                    L1.setText(" "+teamStanding.get(i).overall_l);

                    TextView GS1;
                    GS1 = (TextView) tr1.findViewById(R.id.GS);
                    GS1.setText(" "+teamStanding.get(i).overall_gs);

                    TextView GA1;
                    GA1 = (TextView) tr1.findViewById(R.id.GA);
                    GA1.setText(" "+teamStanding.get(i).overall_ga);

                    TextView GD1;
                    GD1 = (TextView) tr1.findViewById(R.id.GD);
                    GD1.setText(" "+teamStanding.get(i).gd);

                    TextView P1;
                    P1 = (TextView) tr1.findViewById(R.id.P);
                    P1.setText(teamStanding.get(i).points);

                    tableLayout_standings.addView(tr1);

                    // If you use context menu it should be registered for each table row
                    registerForContextMenu(tr1);
                }

                ImageView competitionImg = (ImageView) view.findViewById(R.id.competitionImg);

                switch (StandingsCompetition){
                    case "1204": competitionImg.setImageResource(R.drawable.premierleagueoriginal);
                        break;
                    case "1269": competitionImg.setImageResource(R.drawable.serieaoriginal);
                        break;
                    case "1399": competitionImg.setImageResource(R.drawable.laligaoriginal);
                        break;
                }
            }
        });
    }
}
