package com.example.week3activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.week3activity.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.textView.setText("Click to edit your information");

        binding.EditInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivityForResult(intent,1);
            }
        });


//        binding.ClearBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                binding.enterName.setText("");
//                binding.enterSurname.setText("");
//                binding.enterAge.setText("");
//            }
//        });
//
//        binding.SubmitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//
//                Student student = new Student(binding.enterName.getText().toString(),
//                        binding.enterSurname.getText().toString(),
//                        Integer.parseInt(binding.enterAge.getText().toString()));
//
//                intent.putExtra("student",student);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                Student student = data.getParcelableExtra("student");
                binding.textView.setText(student.StrStudent());
            }
        }
    }
}