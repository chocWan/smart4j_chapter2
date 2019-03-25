package org.smart4j.chapter2.helper;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.service.CustomerService;
import org.smart4j.chapter2.utils.PropsUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DatabaseHelper {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<Connection>();


    static {
        Properties properties = PropsUtil.loadProps("config.properties");
        DRIVER = properties.getProperty("jdbc.driver");
        URL = properties.getProperty("jdbc.url");
        USERNAME = properties.getProperty("jdbc.username");
        PASSWORD = properties.getProperty("jdbc.password");
        try {
            Class.forName(DRIVER);
        }catch (ClassNotFoundException e)
        {
            LOGGER.error("can not load jdbc driver",e);
        }
    }

    public static Connection getConnection()
    {
        Connection conn = CONNECTION_HOLDER.get();
        try {
            conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection erro",e);
        }
        return conn;
    }

    public static void closeConnection()
    {
        Connection conn = CONNECTION_HOLDER.get();
        try {
            if(conn != null)
            {
                conn.close();
            }
        } catch (SQLException e) {
            LOGGER.error("close connection erro",e);
        }
    }

    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql,Object... params)
    {
        List<T> entityList = null;
        try {
            entityList = QUERY_RUNNER.query(getConnection(),sql,new BeanListHandler<T>(entityClass),params);
        } catch (SQLException e) {
            LOGGER.error("query list error",e);
        }finally {
            closeConnection();
        }
        return entityList;
    }

}