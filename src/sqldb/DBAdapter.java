package sqldb;

import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public abstract class DBAdapter {
    protected Connection connection;

    public DBAdapter() {}

    public void init(String className, String dbName) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        connection = DriverManager.getConnection(dbName);
    }

    public ResultSet selectQuery(String sqlQuery) throws SQLException {
        synchronized (connection) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sqlQuery);
            return rs;
        }
    }

    /**
     * select [field, ..] from [name_table]
     * @param fields
     * @param nameTable
     * @return
     * @throws SQLException
     */
    public ResultSet select(List<String> fields, String nameTable) throws SQLException {


        synchronized (connection) {
            String sqlQuery = String.format("SELECT %s FROM %s", arrayToStr(fields), nameTable );
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        }
    }

    /**
     * select * from [table_name] where [field] is [value] and..
     * @param nameTable
     * @return result_set
     * @throws SQLException
     */
    public ResultSet selectAllWith(String nameTable, List<Pair<String, String>> set) throws SQLException {
        synchronized (connection) {
            String sqlQuery = String.format("SELECT * FROM %s WHERE %s", nameTable, setToStr(set, " AND ", " is "));
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int i = 1;
            for (Pair<String, String> pair: set) {
                preparedStatement.setString(i++, pair.getValue());
            }

            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        }
    }

    /**
     * select * from [table_name]
     * @param nameTable
     * @return result_set
     * @throws SQLException
     */
    public ResultSet selectAll(String nameTable) throws SQLException {
        synchronized (connection) {
            String sqlQuery = String.format("SELECT * FROM %s", nameTable);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        }
    }

    /**
     * delete from [table_name] where [field] = [value]
     * @param nameTable
     * @throws SQLException
     */
    public void delete(String nameTable, String field, String value) throws SQLException {
        synchronized (connection) {
            String sqlQuery = String.format("DELETE FROM %s WHERE %s = ?", nameTable, field);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int i = 1;
            preparedStatement.setString(i, value);

            preparedStatement.executeUpdate();
        }

    }

    /**
     * insert into [name_table] ([column], ..) values ([value], ..)
     * @param nameTable
     * @param columns [column], ..
     * @param values [value], ..
     * @throws SQLException
     */
    public void insert(String nameTable, List<String> columns, List<String> values) throws SQLException {
        synchronized (connection) {
            String sqlQuery = String.format("INSERT INTO %s (%s) VALUES (%s)", nameTable, arrayToStr(columns),
                    generatePrepare(values.size()));
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int i = 1;
            for (String val: values) {
                preparedStatement.setString(i++, val);
            }

            preparedStatement.executeUpdate();
        }
    }

    /**
     * update [name_table] set [[field = value], ..] where id = [id]
     * @param nameTable
     * @param set [field = value], ..
     * @param id = [id]
     * @throws SQLException
     */
    public void update(String nameTable, List<Pair<String, String>> set, String id) throws SQLException {
        synchronized (connection) {
            String sqlQuery = String.format("UPDATE %s SET %s WHERE id = ?", nameTable, setToStr(set, ", ", " = "));
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            int i = 1;
            for (Pair<String, String> pair: set) {
                preparedStatement.setString(i++, pair.getValue());
            }
            preparedStatement.setString(i, id);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * numbers of "?, ?, .."
     * @param size numbers of ?
     * @return
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
     * @param arr
     * @return str
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
     * @param set
     * @return str
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
        for (String str: strings) {
            ret.add(str);
        }
        return ret;
    }
}
