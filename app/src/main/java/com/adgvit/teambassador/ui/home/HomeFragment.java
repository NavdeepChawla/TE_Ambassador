package com.adgvit.teambassador.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.adgvit.teambassador.LogInActivity;
import com.adgvit.teambassador.MainActivity;
import com.adgvit.teambassador.NavigationActivity;
import com.adgvit.teambassador.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.adgvit.teambassador.NavigationActivity.drawer;
import static com.adgvit.teambassador.NavigationActivity.navigationView;
import static com.adgvit.teambassador.NavigationActivity.toolbar;


public class HomeFragment extends Fragment {
    private final List<UserTask> homeTaskList = new ArrayList<>();
    private final List<String> colorTask = new ArrayList<>();
    private int position = 0;
    private int colorpos = 0;
    private int prog;
    private int mprogress;
    private LinearLayout linearLayout;
    private ConstraintLayout constraints;
    private AVLoadingIndicatorView loadingbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final String homeUserEmail= LogInActivity.tempEmail;
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(homeUserEmail);
        final DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference().child("Task").child(homeUserEmail);
        final TextView Username = view.findViewById(R.id.homeTextViewName);
        linearLayout = view.findViewById(R.id.homeRecyclerView);
        constraints=view.findViewById(R.id.homeConstraintLayout);
        loadingbar=view.findViewById(R.id.homeLoadingBar);
        final MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getContext(), R.layout.recylerview_row, homeTaskList);
        final TextView level = view.findViewById(R.id.homeTextViewLevel);
        final TextView completed = view.findViewById(R.id.homeTextViewProgressBarCompleted);
        final TextView remaining = view.findViewById(R.id.homeProgressBarTextViewRemaining);
        final ProgressBar homeProgressBar = view.findViewById(R.id.homeProgressBar);
        final ArrayList<TaskClass> taskList=new ArrayList<>();
        hideUI();

        colorTask.add("#FB0303");
        colorTask.add("#084DF1");
        colorTask.add("#25F509");
        colorTask.add("#02FAB7");

        final CountDownTimer countDownTimer =new CountDownTimer(2000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                homeProgressBar.setProgress(0);
                homeProgressBar.setMax(mprogress);
                homeProgressBar.setProgress(prog);
                showUI();
            }
        };

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (final DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String Values=ds.getKey();
                    assert Values != null;
                    if("Name".equalsIgnoreCase(Values))
                    {
                        String tempUserName = Objects.requireNonNull(ds.getValue()).toString();
                        Username.setText(tempUserName);
                        NavigationActivity.navBarUserName.setText(tempUserName);
                    }
                    else if("Progress".equalsIgnoreCase(Values))
                    {
                        String tempProgress2 = Objects.requireNonNull(ds.getValue()).toString();
                        String tempProgress=tempProgress2+"xp";
                        completed.setText(tempProgress);
                        prog = Integer.parseInt(tempProgress2);
                    }
                    else if("Level".equalsIgnoreCase(Values))
                    {
                        String tempLevel = "Level " + Objects.requireNonNull(ds.getValue()).toString();
                        level.setText(tempLevel);
                        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Level").child(ds.getValue().toString());
                        tempRef.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String maxprogress = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                String maxprogress2=maxprogress+"xp";
                                remaining.setText(maxprogress2);
                                mprogress = Integer.parseInt(maxprogress);
                                countDownTimer.start();
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        keyRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                colorpos=0;
                if(linearLayout.getChildCount() > 0)
                {
                    linearLayout.removeAllViews();
                }
                for (final DataSnapshot ds : dataSnapshot.getChildren())
                {

                    final TaskClass tempTask = ds.getValue(TaskClass.class);
                    taskList.add(tempTask);

                    final String taskName = ds.getKey();

                    TextView homeNoTask = view.findViewById(R.id.homeNoTask);
                    homeNoTask.setVisibility(View.INVISIBLE);

                    final int ran = colorpos % colorTask.size();
                    assert tempTask != null;
                    UserTask Task = new UserTask(taskName,tempTask.getDaysLeft(), Color.parseColor(colorTask.get(ran)));
                    colorpos++;
                    homeTaskList.add(Task);
                    final View item = adapter.getView(position++, null, null);
                    item.setTag(position);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
                            intent.putExtra("TaskName", taskName);
                            intent.putExtra("ColorCode",colorTask.get(ran));
                            intent.putExtra("TaskDaysLeft",tempTask.getDaysLeft());
                            intent.putExtra("TaskDescription",tempTask.getDescription());
                            intent.putExtra("TaskStatus",tempTask.getStatus());
                            startActivity(intent);
                        }
                    });
                    linearLayout.addView(item);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }
    public void hideUI()
    {
        Float alpha = 0.2f;
        //toolbar.setAlpha(alpha);
        constraints.setAlpha(alpha);
        linearLayout.setAlpha(alpha);
        //navigationView.setAlpha(alpha);
        //drawer.setAlpha(alpha);
        navigationView.setEnabled(false);
        drawer.setEnabled(false);
        linearLayout.setEnabled(false);
        toolbar.setEnabled(false);
        loadingbar.setVisibility(View.VISIBLE);
    }

    public void showUI()
    {
        Float alpha = 1.0f;
        constraints.setAlpha(alpha);
        linearLayout.setAlpha(alpha);
        navigationView.setEnabled(true);
        //toolbar.setAlpha(alpha);
        //drawer.setAlpha(alpha);
        //navigationView.setAlpha(alpha);
        drawer.setEnabled(true);
        toolbar.setEnabled(true);
        linearLayout.setEnabled(true);
        loadingbar.setVisibility(View.INVISIBLE);
    }
}





                    /*
                    if ("progress".equalsIgnoreCase(a)) {
                        DatabaseReference tempRef = keyRef.child(a);
                        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String tempProgress = dataSnapshot.getValue().toString() + "xp";
                                String tempProgress2 = dataSnapshot.getValue().toString();
                                completed.setText(tempProgress);
                                prog = Integer.parseInt(tempProgress2);
                                System.out.println(prog);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else if ("level".equalsIgnoreCase(a)) {
                        String tempLevel = "Level " + ds.getValue().toString();
                        level.setText(tempLevel);
                        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Level").child(ds.getValue().toString());
                        tempRef.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String maxprogress = dataSnapshot.getValue().toString() + "xp";
                                String maxprogress2 = dataSnapshot.getValue().toString();
                                remaining.setText(maxprogress);
                                mprogress = Integer.parseInt(maxprogress2);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                            {

                            }
                        });
                    } */