package org.smart4j.chapter2.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smart4j.chapter2.model.Customer;
import org.smart4j.chapter2.service.CustomerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerServiceTest {
    private final CustomerService customerService;
    public CustomerServiceTest()
    {
        customerService = new CustomerService();
    }
    @Before
    public void init()
    {
        //TODO
    }
    @Test
    public void getCustomerListTest()
    {
        List<Customer> customers = customerService.getCustomerList();
        Assert.assertEquals(2,customers.size());
    }
    @Test
    public void getCustomerTest()
    {
        long id  = 1;
        Customer customer = customerService.getCustomer(id);
        Assert.assertNotNull(customer);
    }
    @Test
    public  void createCustomerTest()
    {
        Map<String,Object> fieldObect = new HashMap<String,Object>();
        fieldObect.put("name","rae");
        fieldObect.put("telephone","rae112");
        fieldObect.put("contact","rae");
        boolean result = customerService.createCustomer(fieldObect);
        Assert.assertTrue(result);
    }
    @Test
    public  void updateCustomerTest()
    {
        long id = 1;
        Map<String,Object> fieldObect = new HashMap<String,Object>();
        fieldObect.put("contact","rae1111");
        boolean result = customerService.updateCustomer(id,fieldObect);
        Assert.assertTrue(result);
    }
    @Test
    public  void deleteCustomerTest()
    {
        long id = 1;
        boolean result = customerService.deleteCustomer(id);
        Assert.assertTrue(result);
    }

}