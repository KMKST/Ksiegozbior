import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class KsiegozbiorApp extends JFrame {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteka";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTytul, txtAutor, txtRok;

    public KsiegozbiorApp() {
        setTitle("Księgozbiór");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"ID", "Tytuł", "Autor", "Rok wydania"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelDolny = new JPanel(new GridLayout(4, 2, 5, 5));

        panelDolny.add(new JLabel("Tytuł:"));
        txtTytul = new JTextField();
        panelDolny.add(txtTytul);

        panelDolny.add(new JLabel("Autor:"));
        txtAutor = new JTextField();
        panelDolny.add(txtAutor);

        panelDolny.add(new JLabel("Rok wydania:"));
        txtRok = new JTextField();
        panelDolny.add(txtRok);

        JButton btnDodaj = new JButton("Dodaj");
        JButton btnUsun = new JButton("Usuń");
        panelDolny.add(btnDodaj);
        panelDolny.add(btnUsun);

        add(panelDolny, BorderLayout.SOUTH);

        btnDodaj.addActionListener(e -> dodajKsiazke());
        btnUsun.addActionListener(e -> usunKsiazke());

        odswiezTabele();
    }
    private void dodajKsiazke() {
        String tytul = txtTytul.getText();
        String autor = txtAutor.getText();
        String rokStr = txtRok.getText();
        if (tytul.isEmpty() || autor.isEmpty() || rokStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wypełnij wszystkie pola!");
            return;
        }
        String sql = "INSERT INTO ksiazki (tytul, autor, rok_wydania) VALUES ('"
                + tytul + "', '" + autor + "', " + rokStr + ")";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

            JOptionPane.showMessageDialog(this, "Książka dodana!");
            wyczyscPola();
            odswiezTabele();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd bazy danych: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Rok musi być liczbą!");
        }
    }
    private void usunKsiazke() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Wybierz książkę do usunięcia!");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String sql = "DELETE FROM ksiazki WHERE id = " + id;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

            JOptionPane.showMessageDialog(this, "Książka usunięta!");
            odswiezTabele();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd bazy danych: " + e.getMessage());
        }
    }
    private void odswiezTabele() {
        tableModel.setRowCount(0);
        String sql = "SELECT id, tytul, autor, rok_wydania FROM ksiazki";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String tytul = rs.getString("tytul");
                String autor = rs.getString("autor");
                int rok = rs.getInt("rok_wydania");

                tableModel.addRow(new Object[]{id, tytul, autor, rok});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd odczytu danych: " + e.getMessage());
        }
    }
    private void wyczyscPola() {
        txtTytul.setText("");
        txtAutor.setText("");
        txtRok.setText("");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KsiegozbiorApp app = new KsiegozbiorApp();
            app.setVisible(true);
        });
    }
}