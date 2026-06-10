import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelEliminarRegistro extends JPanel {

    public PanelEliminarRegistro() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel lblTitulo = new JLabel("Eliminar registro", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 15));
        bodyPanel.setBackground(Color.WHITE);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"Seleccione una opción", "N° de serie", "N° de inventario", "Nombre del trabajador"});
        comboFiltro.setPreferredSize(new Dimension(200, 32));
        PlaceholderField txtBuscar = new PlaceholderField(" 🔍 Buscar");
        txtBuscar.setPreferredSize(new Dimension(350, 32));

        filterPanel.add(comboFiltro);
        filterPanel.add(txtBuscar);
        bodyPanel.add(filterPanel, BorderLayout.NORTH);

        JTable tabla = crearTablaModelo();
        bodyPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JLabel lblHint = new JLabel("* Seleccione un registro de la tabla y presione la tecla Supr/Delete para eliminar permanentemente.", SwingConstants.LEFT);
        lblHint.setFont(new Font("Arial", Font.ITALIC, 13));
        lblHint.setForeground(Color.RED);
        bodyPanel.add(lblHint, BorderLayout.SOUTH);

        add(bodyPanel, BorderLayout.CENTER);
    }

    private JTable crearTablaModelo() {
        String[] columnas = {"Columna 1", "Columna 2", "Columna 3", "Columna 4", "Columna 5", "Columna 6"};
        Object[][] datos = {
            {"dfghj", "dfghj", "dfghj", "dfghj", "dfghj", "dfghj"},
            {"dfghj", "dfghj", "dfghj", "dfghj", "dfghj", "dfghj"}
        };
        DefaultTableModel model = new DefaultTableModel(datos, columnas);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);
        return table;
    }
}

// --- CLASE SOPORTE GLOBAL PARA COMPARTIR EL DISEÑO DE LOS PLACEHOLDERS ---
class PlaceholderField extends JTextField {
    private String hint;
    public PlaceholderField(String hint) { this.hint = hint; }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.LIGHT_GRAY);
            int paddingY = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
            g2d.drawString(hint, getInsets().left + 5, paddingY);
        }
    }
}