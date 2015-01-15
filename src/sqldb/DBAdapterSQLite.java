package sqldb;

import java.sql.*;

/**
 * Created by v1ar on 15.01.15.
 */
public class DBAdapterSQLite extends DBAdapter {

    public Connection connection;
    public final Statement statement;

    public DBAdapterSQLite(String serv) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(serv);
        statement = connection.createStatement();
    }

    @Override
    protected int insert(String str) throws SQLException {
        synchronized (statement) {
            statement.execute(str);
        }
        return 0;
    }

    @Override
    protected int delete(String str) throws SQLException {
        synchronized (statement) {
            statement.execute(str);
        }
        return 0;
    }

    @Override
    protected ResultSet select(String str) throws SQLException {
        ResultSet resSet;
        synchronized (statement) {
            resSet = statement.executeQuery(str);
        }
        return resSet;
    }

    @Override
    protected int update(String str) throws SQLException {
        synchronized (statement) {
            statement.execute(str);
        }
        return 0;
    }
}
