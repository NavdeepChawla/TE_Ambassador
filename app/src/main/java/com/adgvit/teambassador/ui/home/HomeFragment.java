package com.adgvit.teambassador.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.adgvit.teambassador.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment
{
    final List<UserTask> homeTaskList=new ArrayList<>();
    final List<String> colorTask =new ArrayList<>();
    int position=0;
    int colorpos=0;
    int prog;
    int mprogress;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("EMAIL", Context.MODE_PRIVATE);
        final String homeUserEmail=prefs.getString("Email","");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(homeUserEmail).child("Name");
        final DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference().child("Tasks").child(homeUserEmail);

        final TextView Username=view.findViewById(R.id.homeTextViewName);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tempUserName=dataSnapshot.getValue().toString();
                Username.setText(tempUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final LinearLayout linearLayout=view.findViewById(R.id.homeRecyclerView);
        final MyRecyclerViewAdapter adapter =new MyRecyclerViewAdapter(getContext(),R.layout.recylerview_row,homeTaskList);
        final TextView level=view.findViewById(R.id.homeTextViewLevel);
        final TextView completed=view.findViewById(R.id.homeTextViewProgressBarCompleted);
        final TextView remaining=view.findViewById(R.id.homeProgressBarTextViewRemaining);
        final ProgressBar homeProgressBar=view.findViewById(R.id.homeProgressBar);
        colorTask.add("#FB0303");
        colorTask.add("#084DF1");
        colorTask.add("#25F509");
        colorTask.add("#02FAB7");
        keyRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (final DataSnapshot ds : dataSnapshot.getChildren())
                {
                    final String a=ds.getKey();
                    System.out.println(a);
                    if("progress".equalsIgnoreCase(a))
                    {
                        DatabaseReference tempRef=keyRef.child(a);
                        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String tempProgress=dataSnapshot.getValue().toString()+"xp";
                                String tempProgress2=dataSnapshot.getValue().toString();
                                completed.setText(tempProgress);
                                prog= Integer.parseInt(tempProgress2);
                                System.out.println(prog);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else if ("level".equalsIgnoreCase(a))
                    {
                        String tempLevel="Level "+ds.getValue().toString();
                        level.setText(tempLevel);
                        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Level").child(ds.getValue().toString());
                        tempRef.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String maxprogress=dataSnapshot.getValue().toString()+"xp";
                                String maxprogress2=dataSnapshot.getValue().toString();
                                remaining.setText(maxprogress);
                                mprogress= Integer.parseInt(maxprogress2);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else
                        {
                            assert a != null;
                            DatabaseReference tempRef = keyRef.child(a).child("DaysLeft");
                            tempRef.addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        int ran=colorpos%colorTask.size();
                                        UserTask tempTask = new UserTask(a,dataSnapshot.getValue().toString(), Color.parseColor(colorTask.get(ran)));
                                        colorpos++;
                                        homeTaskList.add(tempTask);
                                        final View item=adapter.getView(position++,null,null);
                                        item.setTag(position);
                                        final int a=position;
                                        item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Toast.makeText(getContext(), Integer.toString(a), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        linearLayout.addView(item);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {
                                    }

                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
        final ProgressDialog progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMax(100);
        progressDoalog.setTitle("Loading");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDoalog.show();
        final Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDoalog.incrementProgressBy(10);
            }
        };
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                try {
                    while (progressDoalog.getProgress() <= progressDoalog.getMax())
                    {
                        Thread.sleep(400);
                        handle.sendMessage(handle.obtainMessage());
                        if (progressDoalog.getProgress() == progressDoalog.getMax())
                        {
                            homeProgressBar.setProgress(0);
                            homeProgressBar.setMax(mprogress);
                            homeProgressBar.setProgress(prog);
                            progressDoalog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}