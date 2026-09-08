import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.MessageFormat;

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
                return false; // Solo lectura
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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

    private String obtenerTablaTarget() {
        if ("Bienes Inmuebles".equals(MenuPrincipalSIGU.moduloSeleccionado)) {
            return "bienes_inmuebles";
        }
        return "bienes_muebles";
    }

    public void cargarDatos() {
        model.setRowCount(0);
        String tabla = obtenerTablaTarget();

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

        String sql = "SELECT b.num_serie, b.num_dependencia, b.unidad_responsable, b.num_trabajador, "
                   + "t.nombre_trabajador, b.descripcion_bien, b.marca, b.color, "
                   + "b.material, b.serie, b.estado_fisico, b.num_talon, b.num_cheque, "
                   + "b.folio_operacion, b.num_factura_titulo, b.fecha_factura, b.num_proveedor, "
                   + "b.costo_adquisicion, b.partida_generica, b.partida_especifica, b.tipo_recurso, b.observaciones "
                   + "FROM " + tabla + " b "
                   + "LEFT JOIN trabajadores t ON b.num_trabajador = t.num_trabajador "
                   + "WHERE " + columnaBd + " LIKE ? "
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

    /**
     * Exportación a Excel (.xls) usando formato HTML.
     * Funciona 100% nativo con Java sin librerías ni dependencias externas.
     */
    private void exportarExcel() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla para exportar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como Excel (.xls)");
        fileChooser.setSelectedFile(new File("Inventario.xls"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xls")) {
                path += ".xls";
            }

            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(path), StandardCharsets.UTF_8))) {

                pw.println("<!DOCTYPE html>");
                pw.println("<html><head><meta charset='UTF-8'>");
                pw.println("<style>");
                pw.println("table { border-collapse: collapse; font-family: Arial, sans-serif; font-size: 11pt; }");
                pw.println("th { background-color: #295CB4; color: #FFFFFF; font-weight: bold; border: 1px solid #000000; padding: 6px 10px; text-align: center; }");
                pw.println("td { border: 1px solid #CCCCCC; padding: 5px 8px; text-align: left; mso-number-format:'\\@'; }");
                pw.println("tr:nth-child(even) { background-color: #F8F9FA; }");
                pw.println("</style></head><body>");

                pw.println("<table><thead><tr>");
                for (int col = 0; col < model.getColumnCount(); col++) {
                    pw.println("<th>" + model.getColumnName(col) + "</th>");
                }
                pw.println("</tr></thead><tbody>");

                for (int row = 0; row < model.getRowCount(); row++) {
                    pw.println("<tr>");
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object val = model.getValueAt(row, col);
                        String celda = (val != null) ? val.toString().replace("<", "&lt;").replace(">", "&gt;") : "";
                        pw.println("<td>" + celda + "</td>");
                    }
                    pw.println("</tr>");
                }

                pw.println("</tbody></table></body></html>");

                JOptionPane.showMessageDialog(this, "¡Reporte exportado exitosamente a Excel!\nUbicación: " + path, "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error File", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Exportación a PDF utilizando el motor de impresión nativo de Java.
     * Abre el diálogo de impresión donde el usuario selecciona "Guardar como PDF" / "Microsoft Print to PDF".
     */
    private void exportarPDF() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla para exportar a PDF.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Encabezado y Pie de página del documento PDF
            MessageFormat header = new MessageFormat("Consulta General de Inventario - SIGU");
            MessageFormat footer = new MessageFormat("Página {0,number,integer}");

            // FIT_WIDTH ajusta automáticamente las 22 columnas para que quepan en la hoja
            boolean finalizado = table.print(
                JTable.PrintMode.FIT_WIDTH, 
                header, 
                footer, 
                true,   // Muestra el diálogo para seleccionar la impresora / Guardar como PDF
                null, 
                true    // Habilita la impresión interactiva
            );

            if (finalizado) {
                JOptionPane.showMessageDialog(this, "¡Proceso de generación de PDF completado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Se canceló el proceso de exportación a PDF.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(this, "Error al generar la impresión en PDF: " + e.getMessage(), "Error de Impresión", JOptionPane.ERROR_MESSAGE);
        }
    }
}