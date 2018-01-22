package org.smart.util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Helper {

    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final Logger LOGGER = LoggerFactory.getLogger(Helper.class);
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private static final ThreadLocal<Connection> CONNECTION_THREAD_LOCAL = new ThreadLocal<>();

    static {
        Properties p = PropsUtil.loadProps("config.properties");
        DRIVER = p.getProperty("jdbc.driver");
        URL = p.getProperty("jdbc.url");
        USERNAME = p.getProperty("jdbc.username");
        PASSWORD = p.getProperty("jdbc.password");
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("can't find driver", e);
        }
    }

    public static <T> List<T> queryEntityList(Class<T> tClass, String sql, Object... params) {
        List<T> lists;
        Connection connection = getConnection();
        try {
            lists = QUERY_RUNNER.query(connection, sql, new BeanListHandler<T>(tClass), params);
        } catch (SQLException e) {
            LOGGER.error("Query entity failed", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return lists;
    }

    public static <T> T queryEntity(Class<T> tClass, String sql, Object... param) {
        T entity = null;
        Connection connection = getConnection();
        try {
            entity = QUERY_RUNNER.query(connection, sql, new BeanHandler<T>(tClass), param);
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("query error", e);
        } finally {
            closeConnection();
        }
        return entity;
    }

    public static Connection getConnection() {
        Connection connection = CONNECTION_THREAD_LOCAL.get();
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                CONNECTION_THREAD_LOCAL.set(connection);
            }
        }
        return connection;
    }


    public static int executeUpdate(String sql, Object... o) {
        Connection connection = getConnection();
        int rows = 0;
        try {
            rows = QUERY_RUNNER.update(connection, sql, o);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return rows;
    }

    public static <T> boolean updateEntity(Class<T> tClass, long id, Map<String, Object> fliedMap) {
        if (CollectionUtil.isEmpty(fliedMap)) {
            LOGGER.error("Can't update entity : map is null");
            return false;
        }

        String sql = "UPDATE " + getTableName(tClass) + " SET ";
        StringBuilder colums = new StringBuilder();
        for (String filename : fliedMap.keySet()) {
            colums.append(filename).append(" = ?,");
        }
        sql += colums.substring(0, colums.lastIndexOf(",")).toString() + " WHERE id = ?";
        List<Object> lists = new ArrayList<>();
        lists.addAll(fliedMap.values());
        lists.add(id);

        Object[] params = lists.toArray();

        return executeUpdate(sql, params) == 1;
    }

    public static <T> boolean deleteEntity(Class<T> tClass, long id) {
        String sql = " DELETE FROM " + getTableName(tClass) + " WHERE id =?";
        return executeUpdate(sql, id) == 1;
    }


    public static <T> boolean insertEntity(Class<T> tClass, Map<String, Object> fliedName) {
        T entity = null;
        String sql = "INSERT INTO " + getTableName(tClass);
        StringBuilder colums = new StringBuilder("(");
        StringBuilder values = new StringBuilder(" VALUES (");
        for (String flied : fliedName.keySet()) {
            colums.append(flied + ", ");
            values.append("?, ");
        }
        colums.replace(colums.lastIndexOf(","), colums.length(), ")");
        values.replace(values.lastIndexOf(","), values.length(), ")");
        sql = sql + colums + values;
        Object[] params = fliedName.values().toArray();
        return executeUpdate(sql,params) == 1;
    }

    public static String getTableName(Class<?> entity) {
        return entity.getSimpleName();
    }

    public static void closeConnection() {
        Connection connection = CONNECTION_THREAD_LOCAL.get();
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                CONNECTION_THREAD_LOCAL.remove();
            }
        }
    }
}
