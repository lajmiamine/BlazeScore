package com.icare.mal21.blazescore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icare.mal21.blazescore.bareclasses.Match;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder>{

    private List<Match> matches;
    Context context;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show();
        }
    };


    // Adapter's Constructor
    public MatchesAdapter(Context context, List<Match> matches) {
        this.matches = matches;
        Log.e("Matches size", String.valueOf(matches.size()));
        this.context = context;
    }

    // Create new views. This is invoked by the layout manager.
    @Override
    public MatchesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        //final ImageView imageViewImage = (ImageView) holder.view.findViewById(R.id.imageViewImage);
        final TextView home_team_name = (TextView) holder.view.findViewById(R.id.home_team_name);
        home_team_name.setText(matches.get(position).localteam_name+"");
        final TextView home_team_score = (TextView) holder.view.findViewById(R.id.home_team_score);
        home_team_score.setText(matches.get(position).localteam_score+"");

        ////////////////////////////////////////////

        final TextView away_team_name = (TextView) holder.view.findViewById(R.id.away_team_name);
        away_team_name.setText(matches.get(position).visitorteam_name+"");
        final TextView away_team_score = (TextView) holder.view.findViewById(R.id.away_team_score);
        away_team_score.setText(matches.get(position).visitorteam_score+"");

        ////////////////////////////////////////////

        try {
            if (!matches.get(position).localteam_score.equals("?") && !matches.get(position).localteam_score.equals("")){
                if (Integer.parseInt(matches.get(position).localteam_score)>Integer.parseInt(matches.get(position).visitorteam_score)){
                    home_team_score.setTextColor(Color.BLUE);
                    away_team_score.setTextColor(Color.RED);
                } else if(Integer.parseInt(matches.get(position).localteam_score)<Integer.parseInt(matches.get(position).visitorteam_score)){
                    home_team_score.setTextColor(Color.RED);
                    away_team_score.setTextColor(Color.BLUE);
                }
            }else if (matches.get(position).localteam_score.equals("")){
                home_team_score.setText("?");
            }

        } catch (Exception e){
            home_team_score.setText("0");
            away_team_score.setText("");
        }

        try {
            if (!matches.get(position).visitorteam_score.equals("?") && !matches.get(position).visitorteam_score.equals("")){
                if (Integer.parseInt(matches.get(position).localteam_score)>Integer.parseInt(matches.get(position).visitorteam_score)){
                    home_team_score.setTextColor(Color.BLUE);
                    away_team_score.setTextColor(Color.RED);
                } else if(Integer.parseInt(matches.get(position).localteam_score)<Integer.parseInt(matches.get(position).visitorteam_score)){
                    home_team_score.setTextColor(Color.RED);
                    away_team_score.setTextColor(Color.BLUE);
                }
            }else if (matches.get(position).visitorteam_score.equals("")){
                away_team_score.setText("?");
            }

        } catch (Exception e){
            home_team_score.setText("0");
            away_team_score.setText("");
        }


        ////////////////////////////////////////////
        final TextView time_match = (TextView) holder.view.findViewById(R.id.time_match);

        if (matches.get(position).status.equals("HT")){
            time_match.setText("HT");
        } else if (matches.get(position).status.equals("FT")){
            time_match.setText("FT");
        }

        if (!matches.get(position).timer.equals("")){
            time_match.setText(matches.get(position).timer+"'");
        } else if (matches.get(position).timer.equals("")&&matches.get(position).ht_score.equals("")){
            time_match.setText(matches.get(position).time+"");
        }

        ////////////////////////////////////////////

        CardView cardView = (CardView)  holder.view.findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(context, MatchDetailsActivity.class);
                myIntent.putExtra("HomeTeam",matches.get(position).localteam_name);
                myIntent.putExtra("HomeTeamScore",matches.get(position).localteam_score);
                //myIntent.putExtra("HomeTeamID",matches.get(position).localteam_id);
                myIntent.putExtra("AwayTeam",matches.get(position).visitorteam_name);
                myIntent.putExtra("AwayTeamScore",matches.get(position).visitorteam_score);
                //myIntent.putExtra("AwayTeamID",matches.get(position).visitorteam_id);
                myIntent.putExtra("Timer",time_match.getText().toString());
                myIntent.putExtra("MatchID",""+matches.get(position).id);
                context.startActivity(myIntent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return matches.size();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }
}
