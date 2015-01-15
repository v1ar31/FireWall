package sqldb;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by v1ar on 15.01.15.
 */
public abstract class DBAdapter {
    public DBAdapter () { }

    protected abstract int insert(String str) throws SQLException;

    protected abstract int delete(String str) throws SQLException;

    protected abstract ResultSet select(String str) throws SQLException;

    protected abstract int update(String str) throws SQLException;

}
