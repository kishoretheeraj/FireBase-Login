package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class dash extends AppCompatActivity {
    TextView verifymsg;
    Button verifybtn;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        verifymsg=findViewById(R.id.verifymsg);
        verifybtn=findViewById(R.id.verifybtn);
        fauth=FirebaseAuth.getInstance();


        FirebaseUser user=fauth.getCurrentUser();

        if(!user.isEmailVerified())
        {
            verifymsg.setVisibility(View.VISIBLE);
            verifybtn.setVisibility(View.VISIBLE);
            verifybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    FirebaseUser user=fauth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(),"Verfication Email has been sent successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(dash.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }


    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}
