import javafx.util.Pair;
import sqldb.DBAdapter;
import sqldb.DBAdapterSQLite;
import sqldb.DBTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
            this.db = DBAdapterSQLite.getInstance();
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
                try {
                    updateDB();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
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

                        ArrayList<String> columns = DBAdapter.initArrayList("name");
                        ArrayList<String> values = DBAdapter.initArrayList(netAddressTextField.getText().trim());

                        db.insert("names", columns, values);

                        int indexName;
                        ArrayList<Pair<String, String>> val = new ArrayList<Pair<String, String>>(){{
                            add(new Pair<>("name", netAddressTextField.getText().trim()));
                        }};
                        ResultSet resSet = db.selectAllWith("names", val);
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
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        deleteBlockBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (netAddressTextField.getText().trim().compareTo("") != 0) {

                        ArrayList<Pair<String, String>> values= new ArrayList<Pair<String, String>>(){{
                            add(new Pair<>("name", netAddressTextField.getText().trim()));
                        }};
                        ResultSet resSet = db.selectAllWith("names", values);
                        int indexName = 0;
                        if (resSet != null) {
                            while (resSet.next()) {
                                indexName = resSet.getInt("id");
                            }
                        }
                        if (indexName > 0) {
                            db.delete("names", "id", Integer.toString(indexName) );
                            db.delete("list", "namesid", Integer.toString(indexName) );

                            updateDB();

                            netAddressTextField.setText("");
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
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
        try {
            updateDB(); // заполнение таблиц
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getSQLquery (String query) {
        try {
            ResultSet resSet = db.selectQuery(query);
            sqlQueryTableModel.setMetaDataForQuery(resSet);
            sqlQueryTableModel.setDataSourceForQuery(resSet); // заполняем
            return 0;
        } catch (Exception e) {
            sqlQueryTextArea.append("\n\n" + e);
            return 1;
        }
    }

    // обновляем записей формы
    public void updateDB () throws Exception {
            for (int i = 0; i < nameListOfTables.length; i++) {
                ResultSet resSet = db.selectAll(nameListOfTables[i]);

                // применяем к нужной таблице на форме
                dbTableModelArrayList.get(i).setMetaData(resSet); // определяем структуру таблиц
                dbTableModelArrayList.get(i).setHashes(db); // ставим хеши для полей id, где они [name, id]

                resSet = db.selectAll(nameListOfTables[i]);
                dbTableModelArrayList.get(i).setDataSource(resSet); // заполняем таблицы
                // добавление combobox на таблицу
                for (int j = 0; j < dbTableModelArrayList.get(i).nameWithID.size(); j++) {
                    Set<String> keys = dbTableModelArrayList.get(i).hashMapArrayList.get(j).keySet();
                    Object[] obj = keys.toArray();
                    Arrays.sort(obj);
                    JComboBox<Object> combo = new JComboBox<Object>(obj);
                    TableColumn tbcol = dbjTableArrayList.get(i).getColumnModel()
                            .getColumn(dbTableModelArrayList.get(i).columnNames
                                    .indexOf(dbTableModelArrayList.get(i).nameWithID.get(j) + "id"));
                    tbcol.setCellEditor(new DefaultCellEditor(combo));
                }
            }
    }

    private abstract class wrapper {
        protected abstract void sqlMethod(int selectRow, DBTableModel DB) throws SQLException;
        private void wrapperMethod(int selectRow) throws SQLException {
            DBTableModel DB = dbTableModelArrayList.get( tabbedPane.getSelectedIndex() );
            try {
                dbjTableArrayList.get(tabbedPane.getSelectedIndex()).getCellEditor().stopCellEditing();
            } catch (NullPointerException e) {
                // it doesn't matter
            }

            sqlMethod(selectRow, DB);
        }

        public void run() {
            if (dbjTableArrayList.get( tabbedPane.getSelectedIndex() ).getSelectedRowCount() != 0) {
                for (int selectRow: dbjTableArrayList.get( tabbedPane.getSelectedIndex() ).getSelectedRows()) {
                    try {
                        wrapperMethod(selectRow); // обернутый метод
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(famePanel,"Ошибка " + e);
                        return;
                    }
                }

                try {
                    updateDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JOptionPane.showMessageDialog(famePanel,"Успешно завершено.");
            } else {
                JOptionPane.showMessageDialog(famePanel,"Не выделена запись.");
            }
        }
    }

    public class deleteSelectRecordFromBD extends wrapper {
        @Override
        protected void sqlMethod(int selectRow, DBTableModel DB) throws SQLException {
            db.delete(nameListOfTables[tabbedPane.getSelectedIndex()], "id", DB.ids.get(selectRow));
        }
    }

    public class insertSelctRecordFromBD extends wrapper {
        @Override
        protected void sqlMethod(int selectRow, DBTableModel DB)  throws SQLException {
            ArrayList<String> columns = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            for (int i = 0; i < DB.getColumnCount(); i++) {
                columns.add(DB.columnNames.get(i));
                values.add(getValueFromCell(selectRow, i, DB));
            }

            db.insert(nameListOfTables[tabbedPane.getSelectedIndex()], columns, values);

        }
    }

    public class updateSelectRecordFromBD extends wrapper {
        @Override
        protected void sqlMethod(int selectRow, DBTableModel DB) throws SQLException {
            ArrayList<Pair<String, String>> set = new ArrayList<>();
            Pair<String, String> pair;
            for (int i = 0; i < DB.getColumnCount(); i++) {
                pair = new Pair<>(DB.columnNames.get(i), getValueFromCell(selectRow, i, DB));
                set.add(pair);
            }

            db.update(nameListOfTables[tabbedPane.getSelectedIndex()], set, DB.ids.get(selectRow));

        }
    }

    private String getValueFromCell(int selectRow, int col, DBTableModel DB) {
        String colValueInSelectRow = ((ArrayList<String>) DB.data.get(selectRow)).get(col);
        String value;
        if (DB.columnNames.get(col).contains("id")) {
            String colName = DB.columnNames.get(col).replaceAll("id", "");
            HashMap<String, String> hm = DB.hashMapArrayList.get(DB.nameWithID.indexOf(colName));
            value = hm.get(colValueInSelectRow);
        } else {
            value = colValueInSelectRow;
        }

        return value;
    }
}
