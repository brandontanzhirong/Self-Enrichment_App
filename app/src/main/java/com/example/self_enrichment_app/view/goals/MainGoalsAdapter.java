package com.example.self_enrichment_app.view.goals;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.self_enrichment_app.R;
import com.example.self_enrichment_app.data.model.MainGoals;
import com.example.self_enrichment_app.data.model.SubGoals;
import com.example.self_enrichment_app.view.general.GlobalVariable;
import com.example.self_enrichment_app.viewmodel.GoalsTrackerViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import soup.neumorphism.NeumorphCardView;

public class MainGoalsAdapter extends FirestoreRecyclerAdapter<MainGoals,MainGoalsAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Context context;
    private boolean edit,completed;
    private SubGoalsAdapter subGoalsAdapter;
    private GoalsTrackerViewModel goalsTrackerViewModel;
    private NavController navController;
    private FirebaseAuth mAuth;
    private String userId;
    private int numGoals;

    public MainGoalsAdapter(@NonNull FirestoreRecyclerOptions<MainGoals> options,boolean edit,boolean completed,NavController navController) {
        super(options);
        this.edit=edit;
        this.completed=completed;
        this.navController=navController;
    }

    @NonNull
    @Override
    public MainGoalsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        this.layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardview_maingoals_goalstracker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainGoalsAdapter.ViewHolder holder, int position, @NonNull MainGoals mainGoals) {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        mAuth =FirebaseAuth.getInstance();
        userId =mAuth.getUid();
        goalsTrackerViewModel = new ViewModelProvider((AppCompatActivity)context).get(GoalsTrackerViewModel.class);
        holder.ETMainGoal.setText(mainGoals.getGoal());
        if (edit){
            holder.ETMainGoal.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        else{
            holder.ETMainGoal.setInputType(InputType.TYPE_NULL);
        }
        holder.ETMainGoal.setTextIsSelectable(edit);
        holder.btnUpdateMainGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalsTrackerViewModel.updateMainGoals(mainGoals.getMainPostId(),holder.ETMainGoal.getText().toString());
                Toast.makeText(context,"Main goal edited.",Toast.LENGTH_SHORT);
            }
        });
        holder.ETMainGoal.setClickable(edit);
        holder.CBMainGoal.setChecked(mainGoals.isCompleted());
        holder.CBMainGoal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection("Users").document(userId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                            public void onComplete (@NonNull Task < DocumentSnapshot > task) {
                                String TAG="Debug";
                                if (task.isSuccessful()) {
                                    if (task.getResult().get("numGoals")!=null) {
                                        numGoals = Integer.valueOf(task.getResult().get("numGoals").toString());
                                        if (!completed) {
                                            numGoals+=1;
                                            goalsTrackerViewModel.updateMainGoalsCompletion(mainGoals.getMainPostId(),holder.CBMainGoal.isChecked(), userId, numGoals);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("numGoals", Integer.toString(numGoals));
                                            if (numGoals==1 || numGoals==3 || numGoals%5==0) {
                                                navController.navigate(R.id.action_destGoals_to_destGoalsMilestoneFragment, bundle);
                                            }
                                        }
                                        else{
                                            numGoals-=1;
                                            goalsTrackerViewModel.updateMainGoalsCompletion(mainGoals.getMainPostId(),holder.CBMainGoal.isChecked(), userId, numGoals);
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
            }
        });
        if (edit){
            //holder.ETMainGoal.setBackground(android.R.drawable.edit_text);
            holder.btnUpdateMainGoal.setVisibility(View.VISIBLE);
            holder.btnDeleteMainGoal.setVisibility(View.VISIBLE);
            holder.CBMainGoal.setVisibility(View.GONE);
        }
        else{
            holder.ETMainGoal.setBackground(null);
            holder.btnUpdateMainGoal.setVisibility(View.GONE);
            holder.btnDeleteMainGoal.setVisibility(View.GONE);
            holder.CBMainGoal.setVisibility(View.VISIBLE);
        }
        if (edit || completed){
            holder.addSubGoalsCardView.setVisibility(View.GONE);
        }
        else{
            holder.addSubGoalsCardView.setVisibility(View.VISIBLE);
        }
        List<String> subGoals = mainGoals.getSubGoals();
        List<Boolean> subGoalsCompletion = mainGoals.getSubGoalsCompletion();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.rvSubGoals.setLayoutManager(layoutManager);
        ArrayList<SubGoals> subGoalsArrayList=new ArrayList<>();
        if (subGoals!=null) {
            for (int i = 0; i < subGoals.size(); i++) {
                subGoalsArrayList.add(new SubGoals(subGoals.get(i), subGoalsCompletion.get(i)));
            }
            subGoalsAdapter = new SubGoalsAdapter(context, subGoalsArrayList, edit, mainGoals.getMainPostId(), navController);
            subGoalsAdapter.registerAdapterDataObserver( new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    holder.rvSubGoals.scrollToPosition(0);
                }
            });
            holder.rvSubGoals.setAdapter(subGoalsAdapter);
        }
        holder.btnAddSubGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.ETNewSubGoals.getText().toString().isEmpty()){
                    holder.ETNewSubGoals.setError("The subgoal must have a description!");
                }
                else{
                    SubGoals newSubGoal=new SubGoals(holder.ETNewSubGoals.getText().toString(),false);
                    subGoalsArrayList.add(newSubGoal);
                    goalsTrackerViewModel.addSubGoals(mainGoals.getMainPostId(),subGoalsArrayList);
                    holder.ETNewSubGoals.getText().clear();
                    subGoalsAdapter.notifyItemInserted(subGoalsArrayList.size()-1);
                    holder.rvSubGoals.setAdapter(subGoalsAdapter);
                }
            }
        });
        holder.btnDeleteMainGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder((AppCompatActivity)context);
                builder.setCancelable(true);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete this goal?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goalsTrackerViewModel.deleteMainGoals(mainGoals.getMainPostId());
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("edit", true);
                                navController.navigate(R.id.action_destGoals_self,bundle);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
        }});
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        EditText ETMainGoal, ETNewSubGoals;
        Button btnUpdateMainGoal, btnAddSubGoal, btnDeleteMainGoal;
        CheckBox CBMainGoal;
        RecyclerView rvSubGoals;
        NeumorphCardView addSubGoalsCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addSubGoalsCardView=itemView.findViewById(R.id.neumorphCardViewAddSubGoal);
            ETMainGoal = itemView.findViewById(R.id.ETMainGoal);
            ETNewSubGoals=itemView.findViewById(R.id.ETNewSubGoals);
            btnUpdateMainGoal = itemView.findViewById(R.id.btnUpdateMainGoal);
            btnAddSubGoal=itemView.findViewById(R.id.btnAddSubGoal);
            btnDeleteMainGoal=itemView.findViewById(R.id.btnDeleteMainGoal);
            CBMainGoal = itemView.findViewById(R.id.CBMainGoal);
            rvSubGoals = itemView.findViewById(R.id.rvSubGoals);
        }
    }

}
