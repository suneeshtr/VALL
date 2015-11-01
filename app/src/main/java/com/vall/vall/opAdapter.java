package com.vall.vall;


/**
 * Created by enim on 1/7/15.
 */

        import android.graphics.Color;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;

public class opAdapter extends RecyclerView.Adapter<opAdapter.ViewHolder> {
    private ArrayList<String> time,medic,clr,skp,take,dose,shape;

    public opAdapter(ArrayList<String> tim, ArrayList<String> med, ArrayList<String> db_dose, ArrayList<String> db_color, ArrayList<String> db_skipt, ArrayList<String> db_take, ArrayList<String> _shape) {
        time = tim;
        medic = med;
        clr = db_color;
        skp = db_skipt;
        take = db_take;
        dose = db_dose;
        shape = _shape;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView medicine;
        public TextView check;
        public TextView info;
        public ImageView cross;

        public ViewHolder(View v) {
            super(v);

            medicine = (TextView) v.findViewById(R.id.opponentsName);
            check = (TextView) v.findViewById(R.id.opponentsCheckBox);
            cross = (ImageView) v.findViewById(R.id.icon);


        }
    }

    public void add(int position, String item) {
        time.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        int position = time.indexOf(item);
        time.remove(position);
        notifyItemRemoved(position);
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
        final Integer name = Integer.valueOf(time.get(position));

        String time= name /60+":"+name%60;

        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("h:mma");
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(time);
            time = _12HourSDF.format(_24HourDt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String mDose;
        mDose = dose.get(position)+" DOSE";
        String notes = time + " | " + mDose + " | " + clr.get(position) + " " + shape.get(position);
//        SpannableStringBuilder sb = new SpannableStringBuilder(notes);
//        Pattern p = Pattern.compile(clr.get(position) + " " + shape.get(position), Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(notes);
//        while (m.find()){
//            sb.setSpan(new ForegroundColorSpan(Color.parseColor(clr.get(position).replaceAll("\\s",""))), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        }
        holder.info.setText(notes);

        holder.cross.setVisibility(View.INVISIBLE);
        holder.check.setVisibility(View.INVISIBLE);

        if (!Boolean.parseBoolean(skp.get(position))){
            holder.cross.setVisibility(View.VISIBLE);
            holder.check.setTextColor(Color.parseColor("#FFED1E26"));
            holder.check.setText("MISSED");
            holder.check.setVisibility(View.VISIBLE);
        }
        if (Boolean.parseBoolean(take.get(position))) {
            holder.cross.setVisibility(View.VISIBLE);
            holder.check.setTextColor(Color.parseColor("#ffd5e553"));
            holder.check.setText("TAKEN");
            holder.check.setVisibility(View.VISIBLE);
        }
        String medName = medic.get(position);
        holder.medicine.setText(medName.substring(0, 1).toUpperCase() + medName.substring(1));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return time.size();
    }

}


