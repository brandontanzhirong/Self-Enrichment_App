package com.example.self_enrichment_app.view.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.self_enrichment_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ReviewAppActivity extends AppCompatActivity {


    FirebaseAuth mauth;
    FirebaseFirestore db;
    DocumentReference documentReference;
    String userID;

    RatingBar ratingBar;
    TextView review, rateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_app);

        ratingBar = (RatingBar) findViewById(R.id.reviewAppRatingBar);
        review = (TextView) findViewById(R.id. reviewAppEditDescription);
        rateCount = (TextView) findViewById(R.id.reviewAppRateCount);

        //Initialise auth and firestore
        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mauth.getCurrentUser().getUid();
        documentReference = db.collection("Users").document(userID);

        //Create a text view for rating bar (beside the rating bar)
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating = ratingBar.getRating();

                if(rating <= 1 && rating > 0){
                    rateCount.setText("Worst " + rateCount + "/5");
                }else if (rating <= 2 && rating > 1){
                    rateCount.setText(("Bad " + rating + "/5"));
                }else if (rating <= 3 && rating > 2){
                    rateCount.setText(("Average " + rating + "/5"));
                }else if (rating <= 4 && rating > 3){
                    rateCount.setText(("Good " + rating + "/5"));
                }else if (rating <= 5 && rating > 4){
                    rateCount.setText(("Best " + rating + "/5"));
                }
            }
        });

        //Cancel and close
        ImageButton closeBtn = (ImageButton) findViewById(R.id.reviewAppCloseButton);

        View.OnClickListener OCLCloseBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReviewAppActivity.this, ProfileActivity.class));
                finish();
            }
        };
        closeBtn.setOnClickListener(OCLCloseBtn);

        //Save and done
        ImageButton doneBtn = (ImageButton) findViewById(R.id.reviewAppDoneButton);

        View.OnClickListener OCLDoneBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reviewApp = review.getText().toString().trim();
                String rateValue = rateCount.getText().toString().trim();


                //Empty check
                if(reviewApp.isEmpty()){
                    Toast.makeText(ReviewAppActivity.this, "Your review is empty.", Toast.LENGTH_SHORT).show();

                }else if(rateValue.isEmpty()){
                    Toast.makeText(ReviewAppActivity.this, "Your rating is empty.", Toast.LENGTH_SHORT).show();

                } else{
                    //Update the current document in firestore
                    documentReference.update("rating", rateValue,"review", reviewApp).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ReviewAppActivity.this, "Your review is completed.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReviewAppActivity.this, "Error occurs. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        };
        doneBtn.setOnClickListener(OCLDoneBtn);

    }
}