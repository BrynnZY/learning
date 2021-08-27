package com.example.paindiaryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.example.paindiaryapplication.databinding.ActivitySigninBinding;
import com.example.paindiaryapplication.viewmodel.PainViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class Signin extends AppCompatActivity {
    private ActivitySigninBinding signinBinding;
    //firebase
    private FirebaseAuth auth;
    //viewmodel
    private PainViewModel painViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signinBinding = ActivitySigninBinding.inflate(getLayoutInflater());
        View view = signinBinding.getRoot();
        setContentView(view);

        //firebase
        auth = FirebaseAuth.getInstance();

        signinBinding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    signinBinding.enterPsd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    signinBinding.enterPsd.setInputType(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        signinBinding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!signinBinding.enterEmail.getText().toString().equals("") &&
                        !signinBinding.enterPsd.getText().toString().equals("")){
                    auth.signInWithEmailAndPassword(signinBinding.enterEmail.getText().toString(),
                            signinBinding.enterPsd.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                signinBinding.messageText.setText("Sign in successful");
                                Intent intent = new Intent(Signin.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            signinBinding.messageText.setText(e.getMessage());
                            signinBinding.enterPsd.setText("");
                            //signinBinding.enterEmail.setText("");
                        }
                    });
                }
                else {
                    signinBinding.messageText.setText("Email or Password missing! Please Enter again");
                }
            }
        });

        signinBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signin.this, Signup.class);
                startActivity(intent);
            }
        });
    }
}