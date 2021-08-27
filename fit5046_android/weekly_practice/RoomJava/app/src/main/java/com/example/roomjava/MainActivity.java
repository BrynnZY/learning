package com.example.roomjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.roomjava.databinding.ActivityMainBinding;
import com.example.roomjava.entity.Customer;
import com.example.roomjava.viewmodel.CustomerViewModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CustomerViewModel customerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.idTextField.setPlaceholderText("This is only used for Edit");

        customerViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(CustomerViewModel.class);
        customerViewModel.getAllCustomers().observe(this, new Observer<List<Customer>>() {
            @Override
            public void onChanged(@Nullable final List<Customer> customers) {
                String allCustomers = "";
                for (Customer temp: customers){
                    String customerDetails = (temp.uid + " " + temp.firstName + " " + temp.lastName + " " + temp.salary);
                    allCustomers = allCustomers + System.getProperty("line.separator") + customerDetails;
                }
                binding.textViewRead.setText("All data: " + allCustomers);
            }
        });
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameTextField.getEditText().getText().toString();
                String surname = binding.surnameTextField.getEditText().getText().toString();
                String strSalary = binding.salaryTextField.getEditText().getText().toString();

                if ((!name.isEmpty() && name != null) && (!surname.isEmpty() && strSalary != null) && (!strSalary.isEmpty() && surname != null)){
                    double salary = Double.parseDouble(strSalary);
                    Customer customer = new Customer(name, surname, salary);
                    customerViewModel.insert(customer);
                    binding.textViewAdd.setText("Added Record: " + name + " " + surname + " " + salary);
                }
            }
        });
        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerViewModel.deleteAll();
                binding.textViewDelete.setText("All data was deleted");
            }
        });
        binding.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.nameTextField.getEditText().setText("");
                binding.surnameTextField.getEditText().setText("");
                binding.salaryTextField.getEditText().setText("");
            }
        });
        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strId = binding.idTextField.getEditText().getText().toString();
                int id = 0;
                if (!strId.isEmpty() && strId != null){
                    id = Integer.parseInt(strId);
                    String name = binding.nameTextField.getEditText().getText().toString();
                    String surname = binding.surnameTextField.getEditText().getText().toString();
                    String strSalary = binding.salaryTextField.getEditText().getText().toString();

                    if ((!name.isEmpty() && name != null) && (!surname.isEmpty() && surname != null) && (!strSalary.isEmpty() && strSalary != null)){
                        double salary = Double.parseDouble(strSalary);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            CompletableFuture<Customer> customerCompletableFuture = customerViewModel.findByIDFuture(id);
                            customerCompletableFuture.thenApply(customer -> {
                                if (customer != null){
                                    customer.firstName = name;
                                    customer.lastName = surname;
                                    customer.salary = salary;
                                    customerViewModel.update(customer);
                                    binding.textViewUpdate.setText("Update was successful for ID: " + customer.uid);
                                }
                                else {
                                    binding.textViewUpdate.setText("Id does not exist");
                                }
                                return customer;
                            });
                        }
                    }
                }
            }
        });
    }
}