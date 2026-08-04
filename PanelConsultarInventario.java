import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class PanelConsultarInventario extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JTextField txtBuscar;
    private JComboBox<String> comboFiltro;

    public PanelConsultarInventario() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // --- ENCABEZADO Y FILTROS (NORTE) ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Consulta General de Inventario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        // Subpanel de Búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);

        JLabel lblBuscar = new JLabel("Buscar por:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 13));

        comboFiltro = new JComboBox<>(new String[]{"Num Serie", "Serie", "Descripción", "Nombre Trabajador"});
        comboFiltro.setFont(new Font("Arial", Font.PLAIN, 13));

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(41, 92, 180));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);

        JButton btnRefrescar = new JButton("Mostrar Todos");
        btnRefrescar.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefrescar.setFocusPainted(false);

        searchPanel.add(lblBuscar);
        searchPanel.add(comboFiltro);
        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnRefrescar);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- TABLA DE DATOS (CENTRO) ---
        String[] columnas = {"N° Serie", "Serie", "Descripción", "Nombre Trabajador", "Estado"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace la tabla de solo lectura
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- BOTONES INFERIORES (SUR) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        bottomPanel.setBackground(Color.WHITE);

        JButton btnExportarExcel = new JButton("Exportar a Excel");
        btnExportarExcel.setBackground(new Color(34, 139, 34));
        btnExportarExcel.setForeground(Color.WHITE);
        btnExportarExcel.setFont(new Font("Arial", Font.BOLD, 13));
        btnExportarExcel.setFocusPainted(false);

        JButton btnExportarPDF = new JButton("Exportar a PDF");
        btnExportarPDF.setBackground(new Color(178, 34, 34));
        btnExportarPDF.setForeground(Color.WHITE);
        btnExportarPDF.setFont(new Font("Arial", Font.BOLD, 13));
        btnExportarPDF.setFocusPainted(false);

        bottomPanel.add(btnExportarExcel);
        bottomPanel.add(btnExportarPDF);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnBuscar.addActionListener(e -> buscarDatos());
        btnRefrescar.addActionListener(e -> cargarDatos());
        btnExportarExcel.addActionListener(e -> exportarExcel());
        btnExportarPDF.addActionListener(e -> exportarPDF());

        // Carga inicial
        cargarDatos();
    }

    // Determina la tabla de MySQL según el módulo elegido en el Header
    private String obtenerTablaTarget() {
        if ("Bienes Inmuebles".equals(MenuPrincipalSIGU.moduloSeleccionado)) {
            return "bienes_inmuebles";
        }
        return "bienes_muebles";
    }

    public void cargarDatos() {
        model.setRowCount(0);
        String tabla = obtenerTablaTarget();
    
        // Consulta con INNER JOIN para traer el nombre real del trabajador
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
        JOptionPane.showMessageDialog(this, "Error al cargar datos desde " + tabla + ": " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarDatos() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarDatos();
            return;
        }

        String columnaBd = "b.num_serie";
        int filtroIdx = comboFiltro.getSelectedIndex();
        if (filtroIdx == 1) columnaBd = "b.serie";
        else if (filtroIdx == 2) columnaBd = "b.descripcion_bien";
        else if (filtroIdx == 3) columnaBd = "t.nombre_trabajador";

        model.setRowCount(0);
        String tabla = obtenerTablaTarget();
        String sql = "SELECT b.num_serie, b.serie, b.descripcion_bien, t.nombre_trabajador, b.estado_fisico " +
                    "FROM " + tabla + " b " +
                    "LEFT JOIN trabajadores t ON b.num_trabajador = t.num_trabajador " +
                    "WHERE " + columnaBd + " LIKE ?";

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

    private void exportarExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como reporte CSV / Excel");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";

            try (FileWriter fw = new FileWriter(path)) {
                for (int i = 0; i < model.getColumnCount(); i++) {
                    fw.write(model.getColumnName(i) + (i == model.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");

                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        fw.write(model.getValueAt(i, j).toString() + (j == model.getColumnCount() - 1 ? "" : ","));
                    }
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente a:\n" + path);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar archivo: " + ex.getMessage(), "Error File", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarPDF() {
        JOptionPane.showMessageDialog(this, "Iniciando generación de documento PDF del inventario...");
    }
}