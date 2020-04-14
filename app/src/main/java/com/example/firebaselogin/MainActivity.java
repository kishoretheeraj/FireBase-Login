package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mfullname,memail,mphone,mpassword;
    Button mregisterbtn;
    TextView mloginbtn;
    FirebaseAuth fauth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mfullname=findViewById(R.id.fullname);
        memail=findViewById(R.id.email);
        mphone=findViewById(R.id.phone);
        mpassword=findViewById(R.id.password);
        mpassword.setTypeface(Typeface.DEFAULT);
        mpassword.setTransformationMethod(new PasswordTransformationMethod());
        mregisterbtn=findViewById(R.id.register);
        mloginbtn=findViewById(R.id.login);

        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBar);

        if(fauth.getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(),dash.class));
            finish();
        }


        mregisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final String email=memail.getText().toString().trim();
                String password=mpassword.getText().toString().trim();
                 final String fullname=mfullname.getText().toString();
                final String phone=mphone.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    memail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password))
                {
                    mpassword.setError("Password is required");
                    return;
                }
                if(password.length()<6)
                {
                    mpassword.setError("password should be greater than six letters");
                    return;
                }


                //registration of user\

                fauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())

                        {
                            userid=fauth.getCurrentUser().getUid();
                            DocumentReference documentReference=fstore.collection("users").document(userid);
                            Map<String,Object> user=new HashMap<>();
                            user.put("fName",fullname);
                            user.put("email",email);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("tag","onSuccess: User profile is created for"+userid);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("tag","onFailure: "+e.toString());
                                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            //send verification link
                            FirebaseUser user1=fauth.getCurrentUser();
                            user1.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this,"Verfication Email has been sent successfully", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });


                            Toast.makeText(MainActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Login.class));
                            progressBar.setVisibility(View.VISIBLE);
                            ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 100);
                            animation.setDuration (10);
                            animation.setInterpolator (new DecelerateInterpolator());
                            animation.start();

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Error!!!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });

                mloginbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),Login.class));

                    }
                });







    }
}
