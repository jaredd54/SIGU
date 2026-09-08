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

        // Botón de Eliminación en el Sur
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(Color.WHITE);

        JButton btnEliminar = new JButton("Eliminar Bien Seleccionado");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEliminar.setBackground(new Color(180, 40, 40));
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
        // Las 22 columnas completas
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
                return false;
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

    private void eliminarRegistroSeleccionado() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Conversión del índice visual al índice del modelo en caso de ordenamiento
        int modelRow = table.convertRowIndexToModel(viewRow);

        // Extracción usando los índices correctos de las 22 columnas:
        // Columna 0 = N° Serie, Columna 5 = Descripción del bien
        String numSerie = String.valueOf(model.getValueAt(modelRow, 0));
        Object descObj = model.getValueAt(modelRow, 5);
        String descripcion = descObj != null ? descObj.toString() : "";

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea eliminar permanentemente el registro?\n\n" +
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
                    cargarTodosLosDatos(); // Refresca la tabla
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}