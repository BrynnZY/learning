package com.example.week3activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.week3activity.databinding.ActivityMainBinding;
import com.example.week3activity.databinding.ActivitySecondBinding;

public class SecondActivity extends AppCompatActivity {
    private ActivitySecondBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.ClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.enterName.setText("");
                binding.enterSurname.setText("");
                binding.enterAge.setText("");
            }
        });

        binding.SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = getIntent();
                //Intent intent = new Intent(MainActivity.this, SecondActivity.class);

                Student student = new Student(binding.enterName.getText().toString(),
                        binding.enterSurname.getText().toString(),
                        Integer.parseInt(binding.enterAge.getText().toString()));
//
                returnIntent.putExtra("student",student);
                setResult(RESULT_OK, returnIntent);
                finish();
//                startActivity(intent);
            }
        });


//        final Intent intent = getIntent();
//        Student student = (Student) intent.getParcelableExtra("student");
//        StringBuilder sb = new StringBuilder(8);
//        sb.append("Your name: ");
//        sb.append(student.getName());
//        sb.append('\n');
//        sb.append("Your surname: ");
//        sb.append(student.getSurname());
//        sb.append('\n');
//        sb.append("Your age: ");
//        sb.append(student.getAge());
//        binding.textView.setText(sb.toString());
    }
}