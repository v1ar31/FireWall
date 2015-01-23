package sqldb;


import java.sql.DriverManager;
import java.sql.SQLException;

public class DBAdapterSQLite extends DBAdapter {
    protected final String NAME_JDBC_CLASS = "org.sqlite.JDBC";
    protected final String NAME_DB = "jdbc:sqlite:resources/blockList.sqlite3";

    private static volatile DBAdapter instance;

    public static DBAdapter getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            synchronized (DBAdapter.class) {
                if (instance == null) {
                    instance = new DBAdapterSQLite();
                }
            }
        }
        return instance;
    }

    public DBAdapterSQLite() throws ClassNotFoundException, SQLException {
        super();
        Class.forName(NAME_JDBC_CLASS);
        connection = DriverManager.getConnection(NAME_DB);
    }

}
