package sqldb;

import java.sql.ResultSet;

/**
 * Created by v1ar on 15.01.15.
 */
public abstract class DBAdapter {
    public DBAdapter () { }

    protected abstract int insert(String str);

    protected abstract int delete(String str);

    protected abstract ResultSet select(String str);

    protected abstract int update(String str);

}
