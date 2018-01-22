package org.smart.service;

import org.smart.model.Customer;
import org.smart.util.Helper;

import java.util.List;
import java.util.Map;

public class CustomerService {


    public List<Customer> getCustomerList(){
        List<Customer> customers = null;
        String sql = "SELECT * FROM custom";
        customers = Helper.queryEntityList(Customer.class,sql);
        return  customers;
    }

    public Customer getCustomer(long id){
        String sql = "SELECT * FROM custom WHERE id = ?";
        Customer customer = Helper.queryEntity(Customer.class,sql,id);
        return customer;
    }

    public boolean createCustomer(Map<String,Object> feildMap) {
        return  false;
    }

    public boolean updateCustomer(long id,Map<String,Object> feildMap) {
       return Helper.updateEntity(Customer.class,id,feildMap);
    }

    public boolean deleteCustomer(long id){
        return false;
    }



}
