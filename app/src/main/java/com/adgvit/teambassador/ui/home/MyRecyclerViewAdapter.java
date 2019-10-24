package com.adgvit.teambassador.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.adgvit.teambassador.R;

import java.util.List;

public class MyRecyclerViewAdapter extends ArrayAdapter<UserTask>
{
    private List<UserTask> mData;
    private Context context;
    private LayoutInflater layoutInflater;
    private int layoutResource;

    public static class ViewHolder
    {
        TextView homeTextViewRecyclerView1;
        TextView homeTextViewRecyclerView2;
        TextView homeTextViewRecyclerView3;
        CardView homeCardViewRecyclerView;
        ViewHolder (View view)
        {
            this.homeTextViewRecyclerView1=view.findViewById(R.id.homeTextViewRecyclerView1);
            this.homeTextViewRecyclerView2=view.findViewById(R.id.homeTextViewRecyclerView2);
            this.homeTextViewRecyclerView3=view.findViewById(R.id.homeTextViewRecyclerView3);
            this.homeCardViewRecyclerView=view.findViewById(R.id.homeCardViewRecyclerView);
        }
    }

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, int resource, List<UserTask> data)
    {
        super(context, R.layout.recylerview_row);
        this.mData = data;
        this.context=context;
        this.layoutInflater=LayoutInflater.from(context);
        this.layoutResource=resource;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,ViewGroup parent)
    {
        View view=layoutInflater.inflate(layoutResource,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        UserTask data=mData.get(position);
        String Temp1=data.HomeTutorialTitle + " Tutorial";
        viewHolder.homeTextViewRecyclerView1.setText(Temp1);
        viewHolder.homeTextViewRecyclerView2.setText(data.HomeDaysLeft);
        viewHolder.homeTextViewRecyclerView3.setText(data.HomeTutorialTitle);
        viewHolder.homeCardViewRecyclerView.setCardBackgroundColor(data.HomeColor);
        view.setTag(viewHolder);
        return view;
    }
}
