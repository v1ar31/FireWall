package sqldb;

import java.sql.*;


public class DBAdapter {
    protected final String NAME_JDBC_CLASS = "org.sqlite.JDBC";
    protected final String NAME_DB = "jdbc:sqlite:resources/blockList.sqlite3";
    protected static volatile DBAdapter instance;

    protected Connection connection;
    protected final Statement statement;

    public static DBAdapter getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            synchronized (DBAdapter.class) {
                if (instance == null) {
                    instance = new DBAdapter();
                }
            }
        }
        return instance;
    }

    public DBAdapter() throws ClassNotFoundException, SQLException {
        Class.forName(NAME_JDBC_CLASS);
        connection = DriverManager.getConnection(NAME_DB);
        statement = connection.createStatement();
    }

    public void insert(String str) throws SQLException {
        synchronized (statement) {
            statement.execute(str);
        }
    }

    public void delete(String str) throws SQLException {
        synchronized (statement) {
            statement.execute(str);
        }
    }

    public ResultSet select(String str) throws SQLException {
        ResultSet resSet = null;
        synchronized (statement) {
            resSet = statement.executeQuery(str);
        }
        return resSet;
    }

    public void update(String str) throws SQLException {
        synchronized (statement) {
            statement.execute(str);
        }
    }
}
