package org.smart4j.chapter2.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.service.CustomerService;
import org.smart4j.chapter2.utils.CollectionUtil;
import org.smart4j.chapter2.utils.PropsUtil;

import javax.activation.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class DatabaseHelper {

    private static final org.slf4j.Logger LOGGER;
    private static final QueryRunner QUERY_RUNNER;
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;
    private static final BasicDataSource DATA_SOURCE;

    static {
        LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
        QUERY_RUNNER = new QueryRunner();
        CONNECTION_HOLDER = new ThreadLocal<Connection>();


        Properties properties = PropsUtil.loadProps("config.properties");
        String DRIVER = properties.getProperty("jdbc.driver");
        String URL = properties.getProperty("jdbc.url");
        String USERNAME = properties.getProperty("jdbc.username");
        String PASSWORD = properties.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
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
            conn = DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            LOGGER.error("get connection erro",e);
            throw new RuntimeException(e);
        }finally {
            CONNECTION_HOLDER.set(conn);
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

    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (Exception e) {
            LOGGER.error("execute query failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public static int executeUpdate(String sql, Object... params) {
        int rows = 0;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    public static <T> T queryEntity(Class<T> entityClass,String sql,Object... params)
    {
        T entity = null;
        try {
            entity = QUERY_RUNNER.query(getConnection(),sql,new BeanHandler<T>(entityClass),params);
        } catch (SQLException e) {
            LOGGER.error("query entity error",e);
        }
        return entity;
    }

    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not insert entity: fieldMap is empty");
            return false;
        }

        String sql = "INSERT INTO " + getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += columns + " VALUES " + values;

        Object[] params = fieldMap.values().toArray();

        return executeUpdate(sql, params) == 1;
    }

    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not update entity: fieldMap is empty");
            return false;
        }

        String sql = "UPDATE " + getTableName(entityClass) + " SET ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append(" = ?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id = ?";

        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();

        return executeUpdate(sql, params) == 1;
    }

    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE id = ?";
        return executeUpdate(sql, id) == 1;
    }

    public static void executeSqlFile(String filePath) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String sql;
            while ((sql = reader.readLine()) != null) {
                executeUpdate(sql);
            }
        } catch (Exception e) {
            LOGGER.error("execute sql file failure", e);
            throw new RuntimeException(e);
        }
    }

    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

}