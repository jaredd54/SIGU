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

public class PanelEliminarRegistro extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> comboFiltro;
    private JTextField txtBuscar;

    public PanelEliminarRegistro() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel lblTitulo = new JLabel("Eliminación de Registros", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(180, 40, 40));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 15));
        bodyPanel.setBackground(Color.WHITE);

        // Barra de búsqueda y filtrado ajustada a las columnas reales
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

        // Tabla con datos
        table = crearTablaModelo();
        bodyPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Botón de Eliminación en el Sur
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(Color.WHITE);

        JButton btnEliminar = new JButton("Eliminar Bien Seleccionado");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEliminar.setBackground(new Color(180, 40, 40)); // Rojo indicativo de acción destructiva
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setPreferredSize(new Dimension(220, 38));
        btnEliminar.setFocusPainted(false);

        southPanel.add(btnEliminar);
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
        btnEliminar.addActionListener(e -> eliminarRegistroSeleccionado());

        // Carga inicial
        cargarTodosLosDatos();
    }

    private JTable crearTablaModelo() {
        String[] columnas = {"N° Serie", "Serie", "Descripción", "Trabajador", "Estado Físico"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Desactiva edición en celdas
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
            System.err.println("Error al cargar registros: " + e.getMessage());
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

    private void eliminarRegistroSeleccionado() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numSerie = model.getValueAt(fila, 0).toString();
        String descripcion = model.getValueAt(fila, 2) != null ? model.getValueAt(fila, 2).toString() : "";

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea eliminar permanentemente el registro?\n" +
                "N° Serie: " + numSerie + "\n" +
                "Descripción: " + descripcion,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            String tabla = obtenerTabla();
            String sqlDelete = "DELETE FROM " + tabla + " WHERE num_serie = ?";

            try (Connection con = Conectar.getConexion();
                 PreparedStatement ps = con.prepareStatement(sqlDelete)) {

                ps.setString(1, numSerie);
                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "El registro ha sido eliminado exitosamente.");
                    cargarTodosLosDatos(); // Refresca la tabla tras eliminar
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}