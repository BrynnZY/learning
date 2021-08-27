package com.example.roomjava.repository;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.roomjava.dao.CustomerDAO;
import com.example.roomjava.database.CustomerDatabase;
import com.example.roomjava.entity.Customer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CustomerRepository {
    private CustomerDAO customerDAO;
    private LiveData<List<Customer>> allCustomers;

    public CustomerRepository(Application application){
        CustomerDatabase db = CustomerDatabase.getInstance(application);
        customerDAO = db.customerDAO();
        allCustomers = customerDAO.getAll();
    }
    public LiveData<List<Customer>> getAllCustomers(){
        return allCustomers;
    }

    public void insert(final Customer customer){
        CustomerDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                customerDAO.insert(customer);
            }
        });
    }
    public void deleteAll(){
        CustomerDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                customerDAO.deleteAll();
            }
        });
    }
    public void delete(final Customer customer){
        CustomerDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                customerDAO.delete(customer);
            }
        });
    }
    public void updateCustomer(final Customer customer){
        CustomerDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                customerDAO.updateCustomer(customer);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<Customer> findByIDFuture(final int customerId){
        return CompletableFuture.supplyAsync(new Supplier<Customer>() {
            @Override
            public Customer get() {
                return customerDAO.findByID(customerId);
            }
        }, CustomerDatabase.databaseWriterExecutor);
    }
}
