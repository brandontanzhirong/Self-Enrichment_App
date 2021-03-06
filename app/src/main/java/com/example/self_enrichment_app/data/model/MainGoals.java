package com.example.self_enrichment_app.data.model;

import android.util.Log;

import com.google.firebase.firestore.DocumentId;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainGoals {
    private String goal, userId;
    private boolean completed;
    private List<String> subGoals;
    private List<Boolean> subGoalsCompletion;
    private Long createdAt;
    @DocumentId
    private String mainPostId;

    public MainGoals() {
    }

    public MainGoals(String userId, String goal){
        this.userId=userId;
        this.goal=goal;
        this.completed=false;
        this.subGoals=new ArrayList();
        this.subGoalsCompletion=new ArrayList();
        this.createdAt = System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getMainPostId(){return this.mainPostId;}

    public String getGoal(){return this.goal;}

    public void setGoal(String goal){this.goal=goal;}

    public boolean isCompleted() {return completed; }

    public void setCompleted(boolean completed) {this.completed = completed;}

    public List<String> getSubGoals() {return subGoals;}

    public List<Boolean> getSubGoalsCompletion() {return subGoalsCompletion;}

}

