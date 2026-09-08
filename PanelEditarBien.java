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

        comboFiltro = new JComboBox<>(new String[]{"Num Serie", "Serie", "Descripción", "Nombre Trabajador"});
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

        // Tabla con las 22 columnas
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
        // Estructura completa de las 22 columnas
        String[] columnas = {
            "N° Serie", "num_dependencia", "unidad responsable", "num_trabajador", 
            "Nombre Trabajador", "Descripción del bien", "marca", "color", 
            "material", "Serie", "Estado", "N° de talón", "N° de cheque", 
            "folio de operación", "num_factura_titulo", "fecha_factura", "num_proveedor", 
            "Costo de adquisición", "partida_generica", "partida_especifica", "tipo_recurso", "observaciones"
        };

        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Deshabilita la edición directa en celdas
            }
        };

        JTable tbl = new JTable(model);
        tbl.setRowHeight(28);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Permite scroll horizontal
        tbl.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tbl.setAutoCreateRowSorter(true);
        return tbl;
    }

    private String obtenerTabla() {
        return "Bienes Inmuebles".equals(MenuPrincipalSIGU.moduloSeleccionado) ? "bienes_inmuebles" : "bienes_muebles";
    }

    public void cargarTodosLosDatos() {
        model.setRowCount(0);
        String tabla = obtenerTabla();

        String sql = "SELECT b.num_serie, b.num_dependencia, b.unidad_responsable, b.num_trabajador, "
                   + "t.nombre_trabajador, b.descripcion_bien, b.marca, b.color, "
                   + "b.material, b.serie, b.estado_fisico, b.num_talon, b.num_cheque, "
                   + "b.folio_operacion, b.num_factura_titulo, b.fecha_factura, b.num_proveedor, "
                   + "b.costo_adquisicion, b.partida_generica, b.partida_especifica, b.tipo_recurso, b.observaciones "
                   + "FROM " + tabla + " b "
                   + "LEFT JOIN trabajadores t ON b.num_trabajador = t.num_trabajador "
                   + "ORDER BY b.num_serie ASC";

        try (Connection con = Conectar.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            poblarTabla(rs);

        } catch (SQLException e) {
            System.err.println("Error al cargar datos en edición: " + e.getMessage());
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

        String sql = "SELECT b.num_serie, b.num_dependencia, b.unidad_responsable, b.num_trabajador, "
                   + "t.nombre_trabajador, b.descripcion_bien, b.marca, b.color, "
                   + "b.material, b.serie, b.estado_fisico, b.num_talon, b.num_cheque, "
                   + "b.folio_operacion, b.num_factura_titulo, b.fecha_factura, b.num_proveedor, "
                   + "b.costo_adquisicion, b.partida_generica, b.partida_especifica, b.tipo_recurso, b.observaciones "
                   + "FROM " + tabla + " b "
                   + "LEFT JOIN trabajadores t ON b.num_trabajador = t.num_trabajador "
                   + "WHERE " + colBd + " LIKE ? "
                   + "ORDER BY b.num_serie ASC";

        try (Connection con = Conectar.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                poblarTabla(rs);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en la búsqueda: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void poblarTabla(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int numTrabajador = rs.getInt("num_trabajador");
            String nombreTrabajador = rs.getString("nombre_trabajador");

            model.addRow(new Object[]{
                rs.getString("num_serie"),
                rs.getString("num_dependencia"),
                rs.getString("unidad_responsable"),
                numTrabajador == 0 ? "Sin asignar" : numTrabajador,
                nombreTrabajador != null ? nombreTrabajador : "Sin asignar",
                rs.getString("descripcion_bien"),
                rs.getString("marca"),
                rs.getString("color"),
                rs.getString("material"),
                rs.getString("serie"),
                rs.getString("estado_fisico"),
                rs.getString("num_talon"),
                rs.getString("num_cheque"),
                rs.getString("folio_operacion"),
                rs.getString("num_factura_titulo"),
                rs.getString("fecha_factura"),
                rs.getString("num_proveedor"),
                rs.getString("costo_adquisicion"),
                rs.getString("partida_generica"),
                rs.getString("partida_especifica"),
                rs.getString("tipo_recurso"),
                rs.getString("observaciones")
            });
        }
    }

    private void abrirVentanaEdicion() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);

        // Extracción de datos actuales utilizando los índices de las 22 columnas
        String numSerieOriginal = String.valueOf(model.getValueAt(modelRow, 0));
        Object numTrabObj = model.getValueAt(modelRow, 3);
        String numTrabActual = (numTrabObj != null && !"Sin asignar".equals(numTrabObj.toString())) ? numTrabObj.toString() : "";
        String descActual = model.getValueAt(modelRow, 5) != null ? model.getValueAt(modelRow, 5).toString() : "";
        String marcaActual = model.getValueAt(modelRow, 6) != null ? model.getValueAt(modelRow, 6).toString() : "";
        String colorActual = model.getValueAt(modelRow, 7) != null ? model.getValueAt(modelRow, 7).toString() : "";
        String materialActual = model.getValueAt(modelRow, 8) != null ? model.getValueAt(modelRow, 8).toString() : "";
        String estadoActual = model.getValueAt(modelRow, 10) != null ? model.getValueAt(modelRow, 10).toString() : "Bueno";
        String obsActual = model.getValueAt(modelRow, 21) != null ? model.getValueAt(modelRow, 21).toString() : "";

        // Diálogo de edición
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Bien - N° Serie: " + numSerieOriginal, true);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(480, 380);
        dialog.setLocationRelativeTo(this);

        JTextField txtDesc = new JTextField(descActual);
        JTextField txtMarca = new JTextField(marcaActual);
        JTextField txtColor = new JTextField(colorActual);
        JTextField txtMaterial = new JTextField(materialActual);
        JTextField txtNumTrabajador = new JTextField(numTrabActual);
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{"Bueno", "Regular", "Malo"});
        comboEstado.setSelectedItem(estadoActual);
        JTextField txtObs = new JTextField(obsActual);

        dialog.add(new JLabel("  Descripción:"));
        dialog.add(txtDesc);
        dialog.add(new JLabel("  Marca:"));
        dialog.add(txtMarca);
        dialog.add(new JLabel("  Color:"));
        dialog.add(txtColor);
        dialog.add(new JLabel("  Material:"));
        dialog.add(txtMaterial);
        dialog.add(new JLabel("  ID Trabajador (N°):"));
        dialog.add(txtNumTrabajador);
        dialog.add(new JLabel("  Estado Físico:"));
        dialog.add(comboEstado);
        dialog.add(new JLabel("  Observaciones:"));
        dialog.add(txtObs);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(34, 139, 34));
        btnGuardar.setForeground(Color.WHITE);

        btnGuardar.addActionListener(e -> {
            String tabla = obtenerTabla();
            String sqlUpdate = "UPDATE " + tabla + " SET descripcion_bien = ?, marca = ?, color = ?, material = ?, "
                             + "num_trabajador = ?, estado_fisico = ?, observaciones = ? WHERE num_serie = ?";

            try (Connection con = Conectar.getConexion();
                 PreparedStatement ps = con.prepareStatement(sqlUpdate)) {

                ps.setString(1, txtDesc.getText().trim());
                ps.setString(2, txtMarca.getText().trim());
                ps.setString(3, txtColor.getText().trim());
                ps.setString(4, txtMaterial.getText().trim());

                // Validación para num_trabajador
                String trabInput = txtNumTrabajador.getText().trim();
                if (trabInput.isEmpty() || "Sin asignar".equalsIgnoreCase(trabInput)) {
                    ps.setNull(5, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(5, Integer.parseInt(trabInput));
                }

                ps.setString(6, (String) comboEstado.getSelectedItem());
                ps.setString(7, txtObs.getText().trim());
                ps.setString(8, numSerieOriginal);

                int lineas = ps.executeUpdate();
                if (lineas > 0) {
                    JOptionPane.showMessageDialog(dialog, "¡Registro actualizado con éxito!");
                    dialog.dispose();
                    cargarTodosLosDatos(); // Refresca la tabla completa
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El ID del trabajador debe ser un número entero válido.", "Error de entrada", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar en la BD: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(new JLabel());
        dialog.add(btnGuardar);
        dialog.setVisible(true);
    }
}