import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PanelEditarBien extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> comboFiltro;
    private JTextField txtBuscar;

    public PanelEditarBien() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel lblTitulo = new JLabel("Edición y Modificación de Bienes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 15));
        bodyPanel.setBackground(Color.WHITE);

        // Barra de búsqueda y filtrado
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.WHITE);

        comboFiltro = new JComboBox<>(new String[]{"N° de Serie", "Serie", "Descripción", "Nombre del Trabajador"});
        comboFiltro.setPreferredSize(new Dimension(200, 32));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(300, 32));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(41, 92, 180));
        btnBuscar.setForeground(Color.WHITE);

        filterPanel.add(new JLabel("Filtrar por:"));
        filterPanel.add(comboFiltro);
        filterPanel.add(txtBuscar);
        filterPanel.add(btnBuscar);
        bodyPanel.add(filterPanel, BorderLayout.NORTH);

        // Tabla con datos reales
        table = crearTablaModelo();
        bodyPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Botón de Edición en el Sur
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(Color.WHITE);

        JButton btnEditar = new JButton("Editar Bien Seleccionado");
        btnEditar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEditar.setBackground(new Color(230, 138, 0));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setPreferredSize(new Dimension(200, 38));
        btnEditar.setFocusPainted(false);

        southPanel.add(btnEditar);
        bodyPanel.add(southPanel, BorderLayout.SOUTH);

        add(bodyPanel, BorderLayout.CENTER);

        // Eventos
        btnBuscar.addActionListener(e -> buscarBien());
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscarBien();
            }
        });
        btnEditar.addActionListener(e -> abrirVentanaEdicion());

        // Carga inicial
        cargarTodosLosDatos();
    }

    private JTable crearTablaModelo() {
        // Ajustado a las columnas reales: N° Serie, Serie, Descripción, Trabajador, Estado Físico
        String[] columnas = {"N° Serie", "Serie", "Descripción", "Trabajador", "Estado Físico"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Deshabilita la edición directa en la tabla
            }
        };
        JTable tbl = new JTable(model);
        tbl.setRowHeight(30);
        tbl.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        return tbl;
    }

    private String obtenerTabla() {
        return "Bienes Inmuebles".equals(MenuPrincipalSIGU.moduloSeleccionado) ? "bienes_inmuebles" : "bienes_muebles";
    }

    public void cargarTodosLosDatos() {
        model.setRowCount(0);
        String tabla = obtenerTabla();
        String sql = "SELECT b.num_serie, b.serie, b.descripcion_bien, t.nombre_trabajador, b.estado_fisico " +
                     "FROM " + tabla + " b " +
                     "LEFT JOIN trabajadores t ON b.num_trabajador = t.num_trabajador";

        try (Connection con = Conectar.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("num_serie"),
                        rs.getString("serie"),
                        rs.getString("descripcion_bien"),
                        rs.getString("nombre_trabajador") != null ? rs.getString("nombre_trabajador") : "Sin asignar",
                        rs.getString("estado_fisico")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar en edición: " + e.getMessage());
        }
    }

    private void buscarBien() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarTodosLosDatos();
            return;
        }

        String colBd = "b.num_serie";
        int idx = comboFiltro.getSelectedIndex();
        if (idx == 1) colBd = "b.serie";
        else if (idx == 2) colBd = "b.descripcion_bien";
        else if (idx == 3) colBd = "t.nombre_trabajador";

        model.setRowCount(0);
        String tabla = obtenerTabla();
        String sql = "SELECT b.num_serie, b.serie, b.descripcion_bien, t.nombre_trabajador, b.estado_fisico " +
                     "FROM " + tabla + " b " +
                     "LEFT JOIN trabajadores t ON b.num_trabajador = t.num_trabajador " +
                     "WHERE " + colBd + " LIKE ?";

        try (Connection con = Conectar.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("num_serie"),
                            rs.getString("serie"),
                            rs.getString("descripcion_bien"),
                            rs.getString("nombre_trabajador") != null ? rs.getString("nombre_trabajador") : "Sin asignar",
                            rs.getString("estado_fisico")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en la búsqueda: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirVentanaEdicion() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numSerieOriginal = model.getValueAt(fila, 0).toString();
        String descActual = model.getValueAt(fila, 2) != null ? model.getValueAt(fila, 2).toString() : "";
        String estadoActual = model.getValueAt(fila, 4) != null ? model.getValueAt(fila, 4).toString() : "Bueno";

        // Diálogo rápido de edición
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Bien: " + numSerieOriginal, true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(420, 250);
        dialog.setLocationRelativeTo(this);

        JTextField txtDesc = new JTextField(descActual);
        JTextField txtNumTrabajador = new JTextField(); // ID numérico del trabajador (num_trabajador)
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{"Bueno", "Regular", "Malo"});
        comboEstado.setSelectedItem(estadoActual);

        dialog.add(new JLabel(" Descripción:"));
        dialog.add(txtDesc);
        dialog.add(new JLabel(" ID Trabajador (N°):"));
        dialog.add(txtNumTrabajador);
        dialog.add(new JLabel(" Estado Físico:"));
        dialog.add(comboEstado);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(34, 139, 34));
        btnGuardar.setForeground(Color.WHITE);

        btnGuardar.addActionListener(e -> {
            String tabla = obtenerTabla();
            String sqlUpdate = "UPDATE " + tabla + " SET descripcion_bien = ?, num_trabajador = ?, estado_fisico = ? WHERE num_serie = ?";

            try (Connection con = Conectar.getConexion();
                 PreparedStatement ps = con.prepareStatement(sqlUpdate)) {

                ps.setString(1, txtDesc.getText().trim());

                // Validación para asignar num_trabajador (entero) o NULL si no se ingresa
                String trabInput = txtNumTrabajador.getText().trim();
                if (trabInput.isEmpty()) {
                    ps.setNull(2, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(2, Integer.parseInt(trabInput));
                }

                ps.setString(3, (String) comboEstado.getSelectedItem());
                ps.setString(4, numSerieOriginal);

                int lineas = ps.executeUpdate();
                if (lineas > 0) {
                    JOptionPane.showMessageDialog(dialog, "¡Registro actualizado con éxito!");
                    dialog.dispose();
                    cargarTodosLosDatos(); // Refresca la tabla visual
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El ID de trabajador debe ser un número entero válido.", "Error de entrada", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar en la BD: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(new JLabel());
        dialog.add(btnGuardar);
        dialog.setVisible(true);
    }
}