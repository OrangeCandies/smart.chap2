package org.smart;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smart.model.Customer;
import org.smart.service.CustomerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerServiceTest {
    private final CustomerService customerService = new CustomerService();




    @Before
    public void init(){

    }

    @Test
    public void getCustomerListTest(){
        List<Customer> customerList = customerService.getCustomerList();
      //  customerList.forEach((e)-> System.out.println(e));
        Assert.assertEquals(2,customerList.size());
    }

    @Test
    public void getCustomer(){
        Customer customer = customerService.getCustomer(1L);
        System.out.println(customer);
        Assert.assertNotNull(customer);
    }

    @Test
    public void createrCustomer(){
        Map<String,Object> feild = new HashMap<String, Object>();
        feild.put("name","CustomerTest");
        feild.put("telephone","T12312312");
        feild.put("contact","Jeff");
        boolean result = customerService.createCustomer(feild);
        Assert.assertTrue(result);
    }


    @Test
    public void delelteCustomer(){
        long id = 1L;
        boolean b = customerService.deleteCustomer(id);
        Assert.assertTrue(b);
    }

    @Test
    public void updateCustomer() {
        long id =1L;
        Map<String,Object> feild = new HashMap<String, Object>();
        feild.put("remark","updateTestOne");
        boolean result = customerService.updateCustomer(id,feild);
        Assert.assertTrue(result);
    }
}
