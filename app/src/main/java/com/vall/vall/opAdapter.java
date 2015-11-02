package com.vall.vall;


/**
 * Created by enim on 1/7/15.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class opAdapter extends RecyclerView.Adapter<opAdapter.ViewHolder> {
    private List<QBUser> opponents;


    public opAdapter(List<QBUser> users) {
        this.opponents = users;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView opponentsName;
        public List<QBUser> selected = new ArrayList<>();

        public ViewHolder(View v) {
            super(v);
            opponentsName = (TextView) v.findViewById(R.id.opponentsName);
        }
    }

//    public List<QBUser> getSelected() {
//        return selected;
//    }


    public QBUser getItem(int position) {
        return opponents.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return opponents.size();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public opAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.opponents, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final QBUser user = opponents.get(position);
        holder.opponentsName.setText(user.getFullName());

    }

}


