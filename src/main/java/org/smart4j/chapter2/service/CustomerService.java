package org.smart4j.chapter2.service;

import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.helper.DatabaseHelper;
import org.smart4j.chapter2.model.Customer;
import org.smart4j.chapter2.utils.PropsUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class CustomerService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    public List<Customer> getCustomerList()
    {
        Connection conn = null;
        try {
            List<Customer> customerList = new ArrayList<Customer>();
            String sql = "select * from customer";
            conn = DatabaseHelper.getConnection();
            return  DatabaseHelper.queryEntityList(Customer.class,sql,conn);
        } catch (Exception e) {
            LOGGER.error("execute sql error",e);
            return  null;
        }
        finally {
            DatabaseHelper.closeConnection(conn);
        }
    }
    public Customer getCustomer(long id)
    {
        //TODO
        return null;
    }
    public boolean createCustomer(Map<String,Object> fieldMap)
    {
        //TODO
        return false;
    }
    public boolean updateCustomer(long id,Map<String,Object> fieldMap)
    {
        //TODO
        return false;
    }
    public boolean deleteCustomer(long id)
    {
        //TODO
        return false;
    }

}