package sqldb;

import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class DBAdapter {
    protected Connection connection;

    public DBAdapter() {}

    public void init(String className, String dbName) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        connection = DriverManager.getConnection(dbName);
    }

    public ResultSet selectQuery(String sqlQuery) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sqlQuery);
    }

    /**
     * select [field, ..] from [name_table]
     */
    public ResultSet select(List<String> fields, String nameTable) throws SQLException {
        String sqlQuery = String.format("SELECT %s FROM %s", arrayToStr(fields), nameTable );
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        return preparedStatement.executeQuery();
    }

    /**
     * select * from [table_name] where [field] is [value] and..
     */
    public ResultSet selectAllWith(String nameTable, List<Pair<String, String>> set) throws SQLException {
        String sqlQuery = String.format("SELECT * FROM %s WHERE %s", nameTable, setToStr(set, " AND ", " is "));
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        int i = 1;
        for (Pair<String, String> pair: set) {
            preparedStatement.setString(i++, pair.getValue());
        }
        return preparedStatement.executeQuery();
    }

    /**
     * select * from [table_name]
     */
    public ResultSet selectAll(String nameTable) throws SQLException {
            String sqlQuery = String.format("SELECT * FROM %s", nameTable);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            return preparedStatement.executeQuery();
    }

    /**
     * delete from [table_name] where [field] = [value]
     */
    public void delete(String nameTable, String field, String value) throws SQLException {
        String sqlQuery = String.format("DELETE FROM %s WHERE %s = ?", nameTable, field);
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        int i = 1;
        preparedStatement.setString(i, value);
        preparedStatement.executeUpdate();
    }

    /**
     * insert into [name_table] ([column], ..) values ([value], ..)
     * @param columns [column], ..
     * @param values [value], ..
     * @throws SQLException
     */
    public void insert(String nameTable, List<String> columns, List<String> values) throws SQLException {
        String sqlQuery = String.format("INSERT INTO %s (%s) VALUES (%s)", nameTable, arrayToStr(columns),
                generatePrepare(values.size()));
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        int i = 1;
        for (String val: values) {
            preparedStatement.setString(i++, val);
        }
        preparedStatement.executeUpdate();
    }

    /**
     * update [name_table] set [[field = value], ..] where id = [id]
     * @param set [field = value], ..
     * @param id = [id]
     * @throws SQLException
     */
    public void update(String nameTable, List<Pair<String, String>> set, String id) throws SQLException {
        String sqlQuery = String.format("UPDATE %s SET %s WHERE id = ?", nameTable, setToStr(set, ", ", " = "));
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        int i = 1;
        for (Pair<String, String> pair: set) {
            preparedStatement.setString(i++, pair.getValue());
        }
        preparedStatement.setString(i, id);
        preparedStatement.executeUpdate();
    }

    /**
     * numbers of "?, ?, .."
     * @param size numbers of ?
     */
    private String generatePrepare(int size) {
        String str = "";
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                str += ", ";
            }
            str += "?";
        }
        return str;
    }

    /**
     * transform array [arr1, arr2, ..] to str "arr1, arr2, .."
     */
    private String arrayToStr(List<String> arr) {
        String str = "";
        for (int i = 0; i < arr.size(); i++) {
            if (i != 0) {
                str += ", ";
            }
            str += arr.get(i);
        }
        return str;
    }

    /**
     * transform a set <field ,*> to a line of request of SET field = ?, ..
     */
    private String setToStr(List<Pair<String, String>> set, String seper, String equel) {
        String set2Str = "";
        for (int i = 0; i < set.size(); i++) {
            if (i != 0) {
                set2Str += seper;
            }
            set2Str += set.get(i).getKey() + equel + "?";
        }
        return set2Str;
    }

    public static ArrayList<String> initArrayList(String ... strings) {
        ArrayList<String> ret = new ArrayList<>();
        Collections.addAll(ret, strings);
        return ret;
    }
}
