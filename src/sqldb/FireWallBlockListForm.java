package sqldb;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by v1ar on 30.10.14.
 */
public class FireWallBlockListForm extends JFrame{
    private JTabbedPane tabbedPane;
    private JPanel FamePanel;
    private JPanel names;
    private JPanel ips;
    private JPanel ports;
    private JTable table_names;
    private JTable table_ips;
    private JTable table_ports;
    private JScrollPane scrollpane_names;
    private JScrollPane scrollpane_ips;
    private JScrollPane scrollpane_ports;
    private JPanel DownPane;
    private JButton button_add_null;
    private JButton button_update;
    private JButton button_delete;
    private JButton button_refresh;
    private JButton button_addtodb;
    private JPanel SQL;
    private JButton button_sqlquery;
    private JTextArea textArea_sqlqury;
    private JTable table_sqlquery;
    private JScrollPane scrollpane_sqlpane;
    private JPanel query;
    private JTextField netAddress;
    private JButton AddBlock;
    private JButton DelBlock;

    // DB
    public final String[] ListTablesDB = {"names", "ips", "ports"};
    private ArrayList<DBTableModel> PostgresDB;
    private ArrayList<JTable> FormTable;
    private DBTableModel QuerySQL_TableModel;

    DBAdapter db;

    /**
     * Initialization FireWallBlockListForm
     * @param db DBAdapter
     */
    public FireWallBlockListForm(final DBAdapter db) {
        super();
        this.db = db;

        // INIT
        initTables();

        setContentPane(FamePanel);
        setType(Type.UTILITY);
        pack();
        setLocationRelativeTo(null);
        setTitle("Настройка черного списка фаерволла");
        setResizable(false);

        button_sqlquery.setVisible(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                setVisible(false);
            }
        } );


        button_add_null.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // обработка нажатия на кнопку добавления пустой записи
                PostgresDB.get( tabbedPane.getSelectedIndex() ).addData();
            }
        });

        button_addtodb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new insertSelctRecordFromBD().run();
            }
        });

        button_update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new updateSelectRecordFromBD().run();
            }
        });

        button_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new deleteSelectRecordFromBD().run();
            }
        });

        button_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDB();
            }
        });

        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (tabbedPane.getSelectedIndex() >= 3) {
                    button_add_null.setVisible(false);
                    button_addtodb.setVisible(false);
                    button_delete.setVisible(false);
                    button_refresh.setVisible(false);
                    button_update.setVisible(false);
                    if (tabbedPane.getSelectedIndex() == 3) {
                        button_sqlquery.setVisible(true);
                    } else {
                        button_sqlquery.setVisible(false);
                    }
                } else {
                    button_add_null.setVisible(true);
                    button_addtodb.setVisible(true);
                    button_delete.setVisible(true);
                    button_refresh.setVisible(true);
                    button_update.setVisible(true);
                    button_sqlquery.setVisible(false);
                }
            }
        });
        button_sqlquery.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textArea_sqlqury.getText().isEmpty()) {
                    getSQLquery(textArea_sqlqury.getText());
                }
            }
        });
        // add all ip address for site domain
        AddBlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (netAddress.getText().trim().compareTo("") != 0) {
                        InetAddress.getByName(netAddress.getText().trim());

                        int res = db.insert("insert into names (name) values ('" + netAddress.getText().trim() + "')");
                        if (res == 0) {
                            int indexName;
                            ResultSet resSet = db.select("select * from names where name = '" + netAddress.getText().trim() + "'");
                            indexName = 0;
                            if (resSet != null) {
                                while (resSet.next()) {
                                    indexName = resSet.getInt("id");
                                }
                            }
                            if (indexName != 0) {
                                Thread thread = new NetInformer(netAddress.getText().trim(), indexName, db);
                                thread.start();
                                thread.join();

                                updateDB();
                                netAddress.setText("");
                            }
                        }
                    }
                } catch (SQLException | InterruptedException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        DelBlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (netAddress.getText().trim().compareTo("") != 0) {

                        ResultSet resSet = db.select("select * from names where name = '" + netAddress.getText().trim() + "'");
                        int indexName = 0;
                        if (resSet != null) {
                            while (resSet.next()) {
                                indexName = resSet.getInt("id");
                            }
                        }
                        if (indexName  > 0) {
                            db.delete("delete from names where id = " + Integer.toString(indexName));
                            db.delete("delete from ips where namesid = " + Integer.toString(indexName));
                            updateDB();
                            netAddress.setText("");
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void initTables () {
        FormTable = new ArrayList<JTable>();
        FormTable.add(table_names);
        FormTable.add(table_ips);
        FormTable.add(table_ports);
        // создание моделей БД
        PostgresDB = new ArrayList<DBTableModel>(); // модель для работы с БД
        for (String aListTablesDB : ListTablesDB) {
            PostgresDB.add(new DBTableModel());
        }
        QuerySQL_TableModel = new DBTableModel();
        table_sqlquery.setModel(QuerySQL_TableModel);
    }

    public void display () {
        //  отображение таблиц на форме
        for ( int i = 0; i < ListTablesDB.length; i++) {
            FormTable.get(i).setModel(PostgresDB.get(i));
        }
        updateDB(); // заполнение таблиц

    }

    private int getSQLquery (String query) {
        try {
            ResultSet resSet = db.select(query);
            QuerySQL_TableModel.setMetaDataForQuery(resSet);
            QuerySQL_TableModel.setDataSourceForQuery(resSet); // заполняем
            return 0;
        } catch (Exception e) {
            textArea_sqlqury.append("\n\n" + e);
            return 1;
        }
    }

    // обновляем записей формы
    public int updateDB ()  {
        try {
            for (int i = 0; i < ListTablesDB.length; i++) {
                ResultSet resSet = db.select("SELECT * FROM " + ListTablesDB[ i ] );

                // применяем к нужной таблице на форме
                PostgresDB.get( i ).setMetaData(resSet); // определяем структуру таблиц
                PostgresDB.get( i ).setHashes(db); // ставим хеши для полей id, где они [name, id]

                resSet = db.select("SELECT * FROM " + ListTablesDB[ i ] );
                PostgresDB.get( i ).setDataSource(resSet); // заполняем таблицы
                // добавление combobox на таблицу
                for (int j = 0; j < PostgresDB.get(i).nameWithID.size(); j++) {
                    Set<String> keys = PostgresDB.get(i).hashMaps.get(j).keySet();
                    JComboBox<Object> combo = new JComboBox<Object>(keys.toArray());
                    TableColumn tbcol = FormTable.get(i).getColumnModel()
                            .getColumn(PostgresDB.get(i).columnNames
                                    .indexOf(PostgresDB.get(i).nameWithID.get(j) + "id"));
                    tbcol.setCellEditor(new DefaultCellEditor(combo));
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return updateDB();
        }
    }

    private abstract class wrapper {
        protected abstract int method_sql(int selectRow, DBTableModel DB);
        private int wrapper_method (int selectRow) {
            DBTableModel DB = PostgresDB.get( tabbedPane.getSelectedIndex() );
            try {
                // снимаем фокус с выделенной ячейки
                FormTable.get(tabbedPane.getSelectedIndex()).getCellEditor().stopCellEditing();
            } catch (Exception e) {
                e.printStackTrace();
                return 2;
            }

            return (method_sql(selectRow, DB) == 0)? 0: 1;
        }

        public void run() {
            if (FormTable.get( tabbedPane.getSelectedIndex() ).getSelectedRowCount() != 0) {
                int res = 0;
                for (int selectRow: FormTable.get( tabbedPane.getSelectedIndex() ).getSelectedRows()) {
                    res += wrapper_method(selectRow); // обернутый метод
                }

                if (res == 0) {
                    updateDB();
                    JOptionPane.showMessageDialog(FamePanel,"Успешно завершено.");
                }
            } else {
                JOptionPane.showMessageDialog(FamePanel,"Не выделена запись.");
            }
        }
    }

    public class deleteSelectRecordFromBD extends wrapper {
        @Override
        protected int method_sql(int selectRow, DBTableModel DB)  {
            return  db.delete("DELETE FROM " + ListTablesDB[tabbedPane.getSelectedIndex()]
                    + " WHERE " + "id = " + DB.ids.get(selectRow));
        }
    }

    public class insertSelctRecordFromBD extends wrapper {
        @Override
        protected int method_sql (int selectRow, DBTableModel DB) {
            String tmp = "";
            String tmp2 = "";
            for (int i = 0; i < DB.getColumnCount(); i++) {
                if (DB.columnNames.get(i).contains("id")) {
                    HashMap<String, String> hm = DB.hashMaps.get(DB.nameWithID.indexOf( DB.columnNames.get(i).replaceAll("id", "") ));
                    tmp += "'" + hm.get(((ArrayList<String>)DB.data.get(selectRow)).get(i)) + "'";
                } else {
                    tmp += "'" + ((ArrayList<String>)DB.data.get(selectRow)).get(i) + "'";
                }
                tmp2 += DB.columnNames.get(i);
                if (i < DB.getColumnCount()-1) {
                    tmp += ", ";
                    tmp2 += ", ";
                }
            }
            // делает INSERT INTO Messages () VALUES( tmp )"
            return  db.insert("INSERT INTO " + ListTablesDB[tabbedPane.getSelectedIndex()]
                    + "(" + tmp2 + ")" + " VALUES(" + tmp + ")");

        }
    }

    public class updateSelectRecordFromBD extends wrapper {
        @Override
        protected int method_sql (int selectRow, DBTableModel DB) {
            String tmp = "";
            for (int i = 0; i < DB.getColumnCount(); i++) {
                tmp += DB.columnNames.get(i) + " = ";
                if (DB.columnNames.get(i).contains("id")) {
                    HashMap<String, String> hm = DB.hashMaps.get(DB.nameWithID.indexOf( DB.columnNames.get(i).replaceAll("id", "") ));
                    String val = hm.get(((ArrayList<String>)DB.data.get(selectRow)).get(i));
                    if (val.compareTo("null") != 0) {
                        tmp += "'" + val + "'";
                    } else {
                        tmp += val;
                    }
                } else {
                    tmp += "'" + ((ArrayList<String>)DB.data.get(selectRow)).get(i) + "'";
                }
                if (i != DB.columnNames.size()-1) {
                    tmp += ", ";
                }
            }

            return db.update("UPDATE " + ListTablesDB[tabbedPane.getSelectedIndex()]
                    + " SET " + tmp + " WHERE " + "id = " + DB.ids.get(selectRow));

        }
    }
}
