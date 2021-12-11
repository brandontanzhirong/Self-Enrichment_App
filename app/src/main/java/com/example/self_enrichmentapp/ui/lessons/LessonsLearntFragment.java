package com.example.self_enrichmentapp.ui.lessons;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.self_enrichmentapp.R;
import com.example.self_enrichmentapp.data.model.LessonPost;

import java.util.ArrayList;
import java.util.List;

public class LessonsLearntFragment extends Fragment {

    private LessonsLearntViewModel mViewModel;
    private RecyclerView rvPosts;
    private LessonsLearntAdapter lessonsLearntAdapter;
    private List<LessonPost> items;

    public static LessonsLearntFragment newInstance() {
        return new LessonsLearntFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lessonslearnt, container, false);
        rvPosts = view.findViewById(R.id.RVPosts);
        lessonsLearntAdapter = new LessonsLearntAdapter(getActivity(),items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setAdapter(lessonsLearntAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LessonsLearntViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        items.add(new LessonPost("I am a good guy",1,2));
        items.add(new LessonPost("I am a good guy",1,2));
        items.add(new LessonPost("I am a good guy",1,2));
        items.add(new LessonPost("I am a good guy",1,2));
    }
}