import sqldb.DBAdapter;
import sqldb.DBTableModel;

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


public class FireWallBlockListForm extends JFrame{
    private JTabbedPane tabbedPane;

    private JPanel famePanel;
    private JPanel namesPanel;
    private JPanel listPanel;
    private JPanel downPanel;
    private JPanel sqlPanel;
    private JPanel queryPanel;

    private JTable namesTable;
    private JTable listTable;
    private JTable sqlQueryTable;

    private JScrollPane namesScrollPane;
    private JScrollPane listScrollPane;
    private JScrollPane sqlQueryScrollPane;

    private JButton addNullBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton refreshBtn;
    private JButton add2dbBtn;
    private JButton sqlQueryBtn;
    private JButton addBlockBtn;
    private JButton deleteBlockBtn;

    private JTextArea sqlQueryTextArea;
    private JTextField netAddressTextField;

    public final String[] nameListOfTables = {"names", "list"};
    private ArrayList<DBTableModel> dbTableModelArrayList;
    private ArrayList<JTable> dbjTableArrayList;
    private DBTableModel sqlQueryTableModel;

    DBAdapter db;


    public FireWallBlockListForm() {
        super();
        try {
            this.db = DBAdapter.getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        initTables();

        setContentPane(famePanel);
        setType(Type.UTILITY);
        pack();
        setLocationRelativeTo(null);
        setTitle("Настройка черного списка фаерволла");
        setResizable(false);

        sqlQueryBtn.setVisible(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                setVisible(false);
            }
        } );


        addNullBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // обработка нажатия на кнопку добавления пустой записи
                dbTableModelArrayList.get(tabbedPane.getSelectedIndex()).addData();
            }
        });

        add2dbBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new insertSelctRecordFromBD().run();
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new updateSelectRecordFromBD().run();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new deleteSelectRecordFromBD().run();
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
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
                    addNullBtn.setVisible(false);
                    add2dbBtn.setVisible(false);
                    deleteBtn.setVisible(false);
                    refreshBtn.setVisible(false);
                    updateBtn.setVisible(false);
                    if (tabbedPane.getSelectedIndex() == 3) {
                        sqlQueryBtn.setVisible(true);
                    } else {
                        sqlQueryBtn.setVisible(false);
                    }
                } else {
                    addNullBtn.setVisible(true);
                    add2dbBtn.setVisible(true);
                    deleteBtn.setVisible(true);
                    refreshBtn.setVisible(true);
                    updateBtn.setVisible(true);
                    sqlQueryBtn.setVisible(false);
                }
            }
        });
        sqlQueryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!sqlQueryTextArea.getText().isEmpty()) {
                    getSQLquery(sqlQueryTextArea.getText());
                }
            }
        });
        // add all ip address for site domain
        addBlockBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (netAddressTextField.getText().trim().compareTo("") != 0) {
                        InetAddress.getByName(netAddressTextField.getText().trim());

                        db.insert("insert into names (name) values ('" + netAddressTextField.getText().trim() + "')");

                        int indexName;
                        ResultSet resSet = db.select("select * from names where name = '" + netAddressTextField.getText().trim() + "'");
                        indexName = 0;
                        if (resSet != null) {
                            while (resSet.next()) {
                                indexName = resSet.getInt("id");
                            }
                        }
                        if (indexName != 0) {
                            Thread thread = new NetInformer(netAddressTextField.getText().trim(), indexName, db);
                            thread.start();
                            thread.join();

                            updateDB();
                            netAddressTextField.setText("");
                        }
                    }
                } catch (SQLException | InterruptedException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        deleteBlockBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (netAddressTextField.getText().trim().compareTo("") != 0) {

                        ResultSet resSet = db.select("select * from names where name = '" + netAddressTextField.getText().trim() + "'");
                        int indexName = 0;
                        if (resSet != null) {
                            while (resSet.next()) {
                                indexName = resSet.getInt("id");
                            }
                        }
                        if (indexName > 0) {
                            db.delete("delete from names where id = " + Integer.toString(indexName));
                            db.delete("delete from list where namesid = " + Integer.toString(indexName));
                            updateDB();
                            netAddressTextField.setText("");
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void initTables () {
        dbjTableArrayList = new ArrayList<JTable>();
        dbjTableArrayList.add(namesTable);
        dbjTableArrayList.add(listTable);
        // создание моделей БД
        dbTableModelArrayList = new ArrayList<DBTableModel>(); // модель для работы с БД
        for (String aListTablesDB : nameListOfTables) {
            dbTableModelArrayList.add(new DBTableModel());
        }
        sqlQueryTableModel = new DBTableModel();
        sqlQueryTable.setModel(sqlQueryTableModel);
    }

    public void display () {
        //  отображение таблиц на форме
        for ( int i = 0; i < nameListOfTables.length; i++) {
            dbjTableArrayList.get(i).setModel(dbTableModelArrayList.get(i));
        }
        updateDB(); // заполнение таблиц

    }

    private int getSQLquery (String query) {
        try {
            ResultSet resSet = db.select(query);
            sqlQueryTableModel.setMetaDataForQuery(resSet);
            sqlQueryTableModel.setDataSourceForQuery(resSet); // заполняем
            return 0;
        } catch (Exception e) {
            sqlQueryTextArea.append("\n\n" + e);
            return 1;
        }
    }

    // обновляем записей формы
    public int updateDB ()  {
        try {
            for (int i = 0; i < nameListOfTables.length; i++) {
                ResultSet resSet = db.select("SELECT * FROM " + nameListOfTables[ i ] );

                // применяем к нужной таблице на форме
                dbTableModelArrayList.get(i).setMetaData(resSet); // определяем структуру таблиц
                dbTableModelArrayList.get(i).setHashes(db); // ставим хеши для полей id, где они [name, id]

                resSet = db.select("SELECT * FROM " + nameListOfTables[ i ] );
                dbTableModelArrayList.get(i).setDataSource(resSet); // заполняем таблицы
                // добавление combobox на таблицу
                for (int j = 0; j < dbTableModelArrayList.get(i).nameWithID.size(); j++) {
                    Set<String> keys = dbTableModelArrayList.get(i).hashMaps.get(j).keySet();
                    JComboBox<Object> combo = new JComboBox<Object>(keys.toArray());
                    TableColumn tbcol = dbjTableArrayList.get(i).getColumnModel()
                            .getColumn(dbTableModelArrayList.get(i).columnNames
                                    .indexOf(dbTableModelArrayList.get(i).nameWithID.get(j) + "id"));
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
        protected abstract void method_sql(int selectRow, DBTableModel DB) throws SQLException;
        private void wrapper_method (int selectRow) throws SQLException {
            DBTableModel DB = dbTableModelArrayList.get( tabbedPane.getSelectedIndex() );
            dbjTableArrayList.get(tabbedPane.getSelectedIndex()).getCellEditor().stopCellEditing();
            method_sql(selectRow, DB);
        }

        public void run() {
            if (dbjTableArrayList.get( tabbedPane.getSelectedIndex() ).getSelectedRowCount() != 0) {
                for (int selectRow: dbjTableArrayList.get( tabbedPane.getSelectedIndex() ).getSelectedRows()) {
                    try {
                        wrapper_method(selectRow); // обернутый метод
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(famePanel,"Ошибка " + e);
                        return;
                    }
                }

                updateDB();
                JOptionPane.showMessageDialog(famePanel,"Успешно завершено.");
            } else {
                JOptionPane.showMessageDialog(famePanel,"Не выделена запись.");
            }
        }
    }

    public class deleteSelectRecordFromBD extends wrapper {
        @Override
        protected void method_sql(int selectRow, DBTableModel DB) throws SQLException {
            db.delete("DELETE FROM " + nameListOfTables[tabbedPane.getSelectedIndex()]
                    + " WHERE " + "id = " + DB.ids.get(selectRow));
        }
    }

    public class insertSelctRecordFromBD extends wrapper {
        @Override
        protected void method_sql (int selectRow, DBTableModel DB)  throws SQLException {
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
            db.insert(String.format("INSERT INTO %s(%s) VALUES(%s)", nameListOfTables[tabbedPane.getSelectedIndex()], tmp2, tmp));

        }
    }

    public class updateSelectRecordFromBD extends wrapper {
        @Override
        protected void method_sql (int selectRow, DBTableModel DB) throws SQLException {
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

            db.update("UPDATE " + nameListOfTables[tabbedPane.getSelectedIndex()]
                    + " SET " + tmp + " WHERE " + "id = " + DB.ids.get(selectRow));

        }
    }
}
