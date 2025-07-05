import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class GestorClientes extends JFrame {

    private JTextField nombrField, correoField, telefinoField;
    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public GestorClientes() {
        setTitle("Gestor de Clientes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        // Panel de entrada
        JPanel panelEntrada = new JPanel(new GridLayout(4, 2));
        panelEntrada.add(new JLabel("Nombre"));
        nombrField = new JTextField();
        panelEntrada.add(nombrField);

        panelEntrada.add(new JLabel("Correo Electrónico"));
        correoField = new JTextField();
        panelEntrada.add(correoField);

        panelEntrada.add(new JLabel("Teléfono"));
        telefinoField = new JTextField();
        panelEntrada.add(telefinoField);

        // Validación en tiempo real: solo números en teléfono
        telefinoField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    e.consume(); // Bloquea cualquier carácter que no sea número
                }
            }
        });

        JButton agregarBtn = new JButton("Agregar Cliente");
        JButton eliminarBtn = new JButton("Eliminar Cliente");

        panelEntrada.add(agregarBtn);
        panelEntrada.add(eliminarBtn);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[] { "ID", "Nombre", "Correo", "Teléfono" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };
        tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);

        add(panelEntrada, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Carga inicial de datos desde la BD
        cargarClientesDesdeBD();

        // Acción agregar
        agregarBtn.addActionListener(e -> {
            String nombre = nombrField.getText();
            String correo = correoField.getText();
            String telefono = telefinoField.getText();

            if (!nombre.isEmpty() && !correo.isEmpty() && !telefono.isEmpty()) {

                // Validar que el teléfono solo tenga números
                if (!telefono.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "El teléfono solo debe contener números.");
                    return;
                }

                // Validar correo con expresión regular
                if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    JOptionPane.showMessageDialog(this, "El correo electrónico no es válido.");
                    return;
                }

                if (agregarClienteBD(nombre, correo, telefono)) {
                    cargarClientesDesdeBD();
                    nombrField.setText("");
                    correoField.setText("");
                    telefinoField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar cliente.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.");
            }
        });

        // Acción eliminar
        eliminarBtn.addActionListener(e -> {
            int filaSeleccionada = tabla.getSelectedRow();
            if (filaSeleccionada != -1) {
                int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
                if (eliminarClienteBD(id)) {
                    cargarClientesDesdeBD();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar cliente.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un cliente para eliminar.");
            }
        });
    }

    private void cargarClientesDesdeBD() {
        modeloTabla.setRowCount(0);
        try (Connection conexion = ConexionBD.conectar()) {
            if (conexion == null) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
                return;
            }
            String sql = "SELECT id, nombre, correo, telefono FROM clientes";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String correo = rs.getString("correo");
                String telefono = rs.getString("telefono");
                modeloTabla.addRow(new Object[] { id, nombre, correo, telefono });
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos: " + e.getMessage());
        }
    }

    private boolean agregarClienteBD(String nombre, String correo, String telefono) {
        try (Connection conexion = ConexionBD.conectar()) {
            if (conexion == null)
                return false;

            String sql = "INSERT INTO clientes (nombre, correo, telefono) VALUES (?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, telefono);

            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + e.getMessage());
            return false;
        }
    }

    private boolean eliminarClienteBD(int id) {
        try (Connection conexion = ConexionBD.conectar()) {
            if (conexion == null)
                return false;

            String sql = "DELETE FROM clientes WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestorClientes().setVisible(true));
    }
}