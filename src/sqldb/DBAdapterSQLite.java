package sqldb;


import java.sql.SQLException;

public class DBAdapterSQLite extends DBAdapter {
    protected final static String NAME_JDBC_CLASS = "org.sqlite.JDBC";
    protected final static String NAME_DB = "jdbc:sqlite:resources/blockList.sqlite3";

    private static volatile DBAdapter instance;

    public static DBAdapter getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            synchronized (DBAdapter.class) {
                if (instance == null) {
                    instance = new DBAdapterSQLite();
                    instance.init(NAME_JDBC_CLASS, NAME_DB);
                }
            }
        }
        return instance;
    }

    public DBAdapterSQLite() {
        super();
    }



}
