package brm;

import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

import java.util.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class BookFrame {

    Connection con;
    PreparedStatement ps;
    JFrame frame = new JFrame("Library Management");
    JTabbedPane tabbedPane = new JTabbedPane();
    JPanel insertPanel, viewPanel;
    JLabel l1, l2, l3, l4, l5;
    JTextField t1, t2, t3, t4, t5;
    JButton saveButton, deleteButton, updateButton;
    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel tm;
    String[] colNames = { "Book ID", "Book Name", "Price", "Author", "Publisher" };

    public BookFrame() {
        getConnectionFromMySQL();
    }

    void getConnectionFromMySQL() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
            System.out.println("Connected to MySQL");
        } catch (SQLException e) {
            System.out.println("connection error:" + e.getMessage());
        }
    }

    void initComponents() {
        l1 = new JLabel("Book ID");
        l2 = new JLabel("Book Name");
        l3 = new JLabel("Price");
        l4 = new JLabel("Author");
        l5 = new JLabel("Publisher");

        t1 = new JTextField();
        t2 = new JTextField();
        t3 = new JTextField();
        t4 = new JTextField();
        t5 = new JTextField();

        saveButton = new JButton("Save");

        l1.setBounds(100, 100, 100, 30);
        l2.setBounds(100, 150, 100, 30);
        l3.setBounds(100, 200, 100, 30);
        l4.setBounds(100, 250, 100, 30);
        l5.setBounds(100, 300, 100, 30);
        t1.setBounds(200, 100, 100, 30);
        t2.setBounds(200, 150, 100, 30);
        t3.setBounds(200, 200, 100, 30);
        t4.setBounds(200, 250, 100, 30);
        t5.setBounds(200, 300, 100, 30);
        saveButton.setBounds(100, 350, 100, 30);

        saveButton.addActionListener(new InsertBookRecord());

        insertPanel = new JPanel();
        insertPanel.setLayout(null);
        insertPanel.add(l1);
        insertPanel.add(l2);
        insertPanel.add(l3);
        insertPanel.add(l4);
        insertPanel.add(l5);
        insertPanel.add(t1);
        insertPanel.add(t2);
        insertPanel.add(t3);
        insertPanel.add(t4);
        insertPanel.add(t5);
        insertPanel.add(saveButton);

        class deleteBookRecord implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowNo = table.getSelectedRow();
                if (rowNo != -1) {
                    int id = (int) table.getValueAt(rowNo, 0);
                    String q = "DELETE FROM book WHERE bookid=?";
                    try {
                        ps = con.prepareStatement(q);
                        ps.setInt(1, id);
                        ps.execute();
                        System.out.println("Record deleted");
                    } catch (SQLException e2) {
                        System.out.println("delete exception:" + e2.getMessage());
                    } finally {

                        ArrayList<Book> bookList = fetchBookRecords();
                        updateTable(bookList);
                    }
                }
            }
        }

        ArrayList<Book> bookList = fetchBookRecords();
        setDataOnTable(bookList);

        updateButton = new JButton("Update Book");
        updateButton.addActionListener(new UpdateBookRecord());
        deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(new deleteBookRecord());

        updateButton.setBounds(100, 350, 100, 30);
        deleteButton.setBounds(250, 350, 100, 30);

        viewPanel = new JPanel();

        viewPanel.add(updateButton);
        viewPanel.add(deleteButton);

        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(100, 100, 300, 200);
        viewPanel.add(scrollPane);

        tabbedPane.add(insertPanel);
        tabbedPane.add(viewPanel);
        tabbedPane.addChangeListener(new TabChangeHandler());

        frame.add(tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    void updateTable(ArrayList<Book> bookList) {
        Object[][] obj = new Object[bookList.size()][5];

        for (int i = 0; i < bookList.size(); i++) {
            obj[i][0] = bookList.get(i).getBookId();
            obj[i][1] = bookList.get(i).getTitle();
            obj[i][2] = bookList.get(i).getPrice();
            obj[i][3] = bookList.get(i).getAuthor();
            obj[i][4] = bookList.get(i).getPublisher();
        }

        tm.setRowCount(bookList.size());
        tm.setColumnIdentifiers(colNames);

        for (int i = 0; i < bookList.size(); i++) {
            tm.setValueAt(obj[i][0], i, 0);
            tm.setValueAt(obj[i][1], i, 1);
            tm.setValueAt(obj[i][2], i, 2);
            tm.setValueAt(obj[i][3], i, 3);
            tm.setValueAt(obj[i][4], i, 4);

        }

        table.setModel(tm);
    }

    void setDataOnTable(ArrayList<Book> bookList) {

        Object[][] obj = new Object[bookList.size()][5];

        for (int i = 0; i < bookList.size(); i++) {
            obj[i][0] = bookList.get(i).getBookId();
            obj[i][1] = bookList.get(i).getTitle();
            obj[i][2] = bookList.get(i).getPrice();
            obj[i][3] = bookList.get(i).getAuthor();
            obj[i][4] = bookList.get(i).getPublisher();
        }
        table = new JTable();
        tm = new DefaultTableModel();
        tm.setRowCount(bookList.size());
        tm.setColumnIdentifiers(colNames);

        for (int i = 0; i < bookList.size(); i++) {
            tm.setValueAt(obj[i][0], i, 0);
            tm.setValueAt(obj[i][1], i, 1);
            tm.setValueAt(obj[i][2], i, 2);
            tm.setValueAt(obj[i][3], i, 3);
            tm.setValueAt(obj[i][4], i, 4);

        }

        table.setModel(tm);

    }

    ArrayList<Book> fetchBookRecords() {
        ArrayList<Book> booklist = new ArrayList<Book>();
        String q = "select * from book";

        try {
            ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book b1 = new Book();
                b1.setBookId(rs.getInt("bookid"));
                b1.setTitle(rs.getString("title"));
                b1.setPrice(rs.getDouble("price"));
                b1.setAuthor(rs.getString("author"));
                b1.setPublisher(rs.getString("publisher"));
                booklist.add(b1);
            }
        } catch (SQLException e) {
            System.out.println("fetch exception" + e.getMessage());
        }
        return booklist;
    }

    class InsertBookRecord implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Book b1 = readFromData();
            String q = "insert into book(bookid,title,price,author,publisher) values(?,?,?,?,?)";
            try {
                ps = con.prepareStatement(q);
                ps.setInt(1, b1.getBookId());
                ps.setString(2, b1.getTitle());
                ps.setDouble(3, b1.getPrice());
                ps.setString(4, b1.getAuthor());
                ps.setString(5, b1.getPublisher());
                ps.execute();
                System.out.println("Record inserted");
                t1.setText("");
                t2.setText("");
                t3.setText("");
                t4.setText("");
                t5.setText("");

            } catch (SQLException e1) {
                System.out.println("insert exception:" + e1.getMessage());
            }
        }

        Book readFromData() {
            Book b1 = new Book();
            b1.setBookId(Integer.parseInt(t1.getText()));
            b1.setTitle(t2.getText());
            b1.setPrice(Double.parseDouble(t3.getText()));
            b1.setAuthor(t4.getText());
            b1.setPublisher(t5.getText());
            return b1;
        }

    }

    class TabChangeHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            int index = tabbedPane.getSelectedIndex();
            if (index == 1) {
                ArrayList<Book> booklist = fetchBookRecords();
                updateTable(booklist);
            }
            if (index == 0) {
                System.out.println("Insert tab selected");
            }
        } // Assuming ps is declared somewhere in your class

    }

    class UpdateBookRecord implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ArrayList<Book> updatedBookList = readTableData();
            String q = "update book set title=?, price=?, author=?, publisher=? where bookid=?";

            try {
                ps = con.prepareStatement(q);
                for (Book b1 : updatedBookList) {
                    ps.setString(1, b1.getTitle());
                    ps.setDouble(2, b1.getPrice());
                    ps.setString(3, b1.getAuthor());
                    ps.setString(4, b1.getPublisher());
                    ps.setInt(5, b1.getBookId());
                    ps.executeUpdate();
                }
                System.out.println("Record updated");
            } catch (SQLException e1) {
                System.out.println("update exception:" + e1.getMessage());
            }
        }

        ArrayList<Book> readTableData() {
            ArrayList<Book> updatedbookList = new ArrayList<Book>();
            for (int i = 0; i < tm.getRowCount(); i++) {
                Book b1 = new Book();
                b1.setBookId((int) tm.getValueAt(i, 0));
                b1.setTitle((String) tm.getValueAt(i, 1));
                b1.setPrice((double) tm.getValueAt(i, 2));
                b1.setAuthor((String) tm.getValueAt(i, 3));
                b1.setPublisher((String) tm.getValueAt(i, 4));
                updatedbookList.add(b1);
            }
            return updatedbookList;
        }

        // class deleteBookRecord implements ActionListener {
        // public void actionPerformed(ActionEvent e) {

        // int rowNo = table.getSelectedRow();
        // if (rowNo != -1) {
        // int id = (int) table.getValueAt(rowNo, 0);
        // String q = "DELETE FROM book WHERE bookid=?";
        // try {
        // ps = con.prepareStatement(q);
        // ps.setInt(1, id);
        // ps.execute();
        // System.out.println("Record deleted");
        // } catch (SQLException e2) {
        // System.out.println("delete exception:" + e2.getMessage());
        // } finally {

        // ArrayList<Book> bookList = fetchBookRecords();
        // updateTable(bookList);
        // }
        // }
        // }
        // }

    }
}