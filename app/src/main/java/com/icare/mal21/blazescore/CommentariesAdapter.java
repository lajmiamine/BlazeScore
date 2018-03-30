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

import com.icare.mal21.blazescore.bareclasses.Comment;
import com.icare.mal21.blazescore.bareclasses.Commentaries;
import com.icare.mal21.blazescore.bareclasses.Match;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentariesAdapter extends RecyclerView.Adapter<CommentariesAdapter.ViewHolder>{

    private List<Comment> comment;
    String HomeTeam, AwayTeam;
    Context context;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show();
        }
    };


    // Adapter's Constructor
    public CommentariesAdapter(Context context, List<Comment> comments, String HomeTeam, String AwayTeam) {
        this.comment = comments;
        this.HomeTeam=HomeTeam;
        this.AwayTeam=AwayTeam;
        //Log.e("Commentaries size", String.valueOf(comments.size()));
        this.context = context;
    }

    // Create new views. This is invoked by the layout manager.
    @Override
    public CommentariesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal_scored, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        final TextView home_team_goal = (TextView) holder.view.findViewById(R.id.home_team_goal);
        final TextView away_team_goal = (TextView) holder.view.findViewById(R.id.away_team_goal);
        Pattern p = Pattern.compile("\\((.*?)\\)",Pattern.DOTALL);
        Matcher m = p.matcher(comment.get(position).comment);
        while (m.find()) {
            Log.e("entre parentheses", m.group(1));
            if (m.group(1).equals(HomeTeam)){
                String[] s = comment.get(position).comment.split("\\.");
                home_team_goal.setText(comment.get(position).minute+" "+s[1]);
                away_team_goal.setVisibility(View.INVISIBLE);
            } else {
                String[] s = comment.get(position).comment.split("\\.");
                away_team_goal.setText(comment.get(position).minute+" "+s[1]);
                home_team_goal.setVisibility(View.INVISIBLE);
            }
        }

        //final ImageView imageViewImage = (ImageView) holder.view.findViewById(R.id.imageViewImage);
        m.find();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return comment.size();
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
