package com.example.self_enrichment_app.view.health;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatImageButton;
import android.widget.EditText;

import com.example.self_enrichment_app.R;
import com.example.self_enrichment_app.data.model.HealthEntry;
import com.example.self_enrichment_app.view.MainActivity;
import com.example.self_enrichment_app.viewmodel.HealthEntriesViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HealthEntryFragment extends Fragment {

    private EditText ETEnterWeight, ETEnterHeight, ETEnterSysP, ETEnterDiaP, ETEnterPulse, ETEnterStepsCount;
    private AppCompatImageButton BtnCancelHealthEntry, BtnSubmitHealthEntry;
    private String date;
    private FirebaseAuth mAuth;
    private String userId;
    private HealthEntriesViewModel healthEntriesViewModel;

    public HealthEntryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setToolbarTitle(R.string.title_health);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        ETEnterWeight = view.findViewById(R.id.ETEnterWeight);
        ETEnterHeight = view.findViewById(R.id.ETEnterHeight);
        ETEnterSysP = view.findViewById(R.id.ETEnterSysP);
        ETEnterDiaP = view.findViewById(R.id.ETEnterDiaP);
        ETEnterPulse = view.findViewById(R.id.ETEnterPulse);
        ETEnterStepsCount = view.findViewById(R.id.ETEnterStepsCount);
        healthEntriesViewModel = new ViewModelProvider(this).get(HealthEntriesViewModel.class);

        Bundle bundle = getArguments();
        if (bundle!=null) {
            date = bundle.getString("date");
        }

        BtnCancelHealthEntry = view.findViewById(R.id.BtnCancelHealthEntry);
        View.OnClickListener OCLCancelHealthEntry = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.destHealth, bundle);
            }
        };
        BtnCancelHealthEntry.setOnClickListener(OCLCancelHealthEntry);

        BtnSubmitHealthEntry = view.findViewById(R.id.BtnSubmitHealthEntry);
        View.OnClickListener OCLSubmitHealthEntry = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // error handling
                boolean error_message = false;
                if (ETEnterWeight.getText().toString().isEmpty()) {
                    ETEnterWeight.setError("Weight cannot be empty!");
                    error_message = true;
                } else if (!isPositiveInteger(ETEnterWeight.getText().toString())) {
                    ETEnterWeight.setError("Weight can only be positive integer!");
                    error_message = true;
                }
                if (ETEnterHeight.getText().toString().isEmpty()) {
                    ETEnterHeight.setError("Height cannot be empty!");
                    error_message = true;
                } else if (!isPositiveInteger(ETEnterHeight.getText().toString())) {
                    ETEnterHeight.setError("Height can only be positive integer!");
                    error_message = true;
                }
                if (ETEnterSysP.getText().toString().isEmpty()) {
                    ETEnterSysP.setError("Systolic blood pressure cannot be empty!");
                    error_message = true;
                } else if (!isPositiveInteger(ETEnterSysP.getText().toString())) {
                    ETEnterSysP.setError("Systolic blood pressure can only be positive integer!");
                    error_message = true;
                }
                if (ETEnterDiaP.getText().toString().isEmpty()) {
                    ETEnterDiaP.setError("Diastolic blood pressure cannot be empty!");
                    error_message = true;
                } else if (!isPositiveInteger(ETEnterDiaP.getText().toString())) {
                    ETEnterDiaP.setError("Diastolic blood pressure can only be positive integer!");
                    error_message = true;
                }
                if (ETEnterPulse.getText().toString().isEmpty()) {
                    ETEnterPulse.setError("Blood pulse rate cannot be empty!");
                    error_message = true;
                } else if (!isPositiveInteger(ETEnterPulse.getText().toString())) {
                    ETEnterPulse.setError("Blood pulse rate can only be positive integer!");
                    error_message = true;
                }
                if (ETEnterStepsCount.getText().toString().isEmpty()) {
                    ETEnterStepsCount.setError("Steps count goal cannot be empty!");
                    error_message = true;
                } else if (!isPositiveInteger(ETEnterStepsCount.getText().toString())) {
                    ETEnterStepsCount.setError("Steps count goal can only be positive integer!");
                    error_message = true;
                }

                if (error_message){
                    return;
                }

                int newWeight = Integer.parseInt(ETEnterWeight.getText().toString());
                int newHeight = Integer.parseInt(ETEnterHeight.getText().toString());
                int newSys = Integer.parseInt(ETEnterSysP.getText().toString());
                int newDia = Integer.parseInt(ETEnterDiaP.getText().toString());
                int newPulse = Integer.parseInt(ETEnterPulse.getText().toString());
                int newStepsGoal = Integer.parseInt(ETEnterStepsCount.getText().toString());

                // Value will change to get from Firebase
                Query query = FirebaseFirestore.getInstance().collection("HealthEntries").whereEqualTo("userId",userId).whereEqualTo("date",date);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().isEmpty()){
                                Log.d("TAG", "No data");
                                HealthEntry healthEntry = new HealthEntry(date, userId, newWeight, newHeight,
                                        newSys, newDia, newPulse, 0, newStepsGoal);
                                healthEntriesViewModel.addHealthEntry(healthEntry);
                                Navigation.findNavController(view).navigate(R.id.destHealth, bundle);
                            }
                            else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", "Document Id: " + document.getId() + "==" + document.getData());
                                    healthEntriesViewModel.updateHealthEntry(document.getId(), newWeight, newHeight, newSys, newDia,
                                        newPulse, newStepsGoal);
                                    Navigation.findNavController(view).navigate(R.id.destHealth, bundle);
                                }
                            }
                        }
                    }
                });
            }
        };
        BtnSubmitHealthEntry.setOnClickListener(OCLSubmitHealthEntry);
    }

    public boolean isPositiveInteger(String textnum) {
        try {
            int num = Integer.parseInt(textnum);
            if (num <= 0) {
                return false;
            }
            else {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }
}