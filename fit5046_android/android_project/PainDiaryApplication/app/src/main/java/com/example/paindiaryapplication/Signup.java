package com.example.paindiaryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.paindiaryapplication.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class Signup extends AppCompatActivity {
    private ActivitySignupBinding signupBinding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = signupBinding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();

        signupBinding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    signupBinding.enterPsd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    signupBinding.enterPsdAgain.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    signupBinding.enterPsd.setInputType(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                    signupBinding.enterPsdAgain.setInputType(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        signupBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!signupBinding.enterEmail.getText().toString().equals("") &&
                        !signupBinding.enterPsd.getText().toString().equals("")&&
                        !signupBinding.enterPsdAgain.getText().toString().equals("")){
                    if (signupBinding.enterPsd.getText().toString()
                            .equals(signupBinding.enterPsdAgain.getText().toString())){
                        auth.createUserWithEmailAndPassword(signupBinding.enterEmail.getText().toString(),
                                signupBinding.enterPsd.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        signupBinding.messageText.setText("Sign up successful");
                                        Intent intent = new Intent(Signup.this, Signin.class);
                                        startActivity(intent);
                                    }else {
                                        signupBinding.messageText.setText(task.getException().getMessage());
                                        signupBinding.enterPsd.setText("");
                                        signupBinding.enterPsdAgain.setText("");
                                    }
                                }
                        });
                    }else {
                        signupBinding.messageText.setText("Password confirmation doesn't match Password");
                        signupBinding.enterPsd.setText("");
                        signupBinding.enterPsdAgain.setText("");
                    }
                }else {
                    signupBinding.messageText.setText("Email or Password missing! Please Enter again");
                }
            }
        });
    }
}