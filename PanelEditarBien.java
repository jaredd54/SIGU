import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelEditarBien extends JPanel {

    public PanelEditarBien() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel lblTitulo = new JLabel("Editar", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 15));
        bodyPanel.setBackground(Color.WHITE);

        // Barra de búsqueda y filtrado
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"Seleccione una opción", "N° de serie", "N° de inventario", "N° de trabajador", "Nombre del trabajador"});
        comboFiltro.setPreferredSize(new Dimension(200, 32));
        
        PlaceholderField txtBuscar = new PlaceholderField(" 🔍 Buscar");
        txtBuscar.setPreferredSize(new Dimension(350, 32));

        filterPanel.add(comboFiltro);
        filterPanel.add(txtBuscar);
        bodyPanel.add(filterPanel, BorderLayout.NORTH);

        // Estructura de Tabla
        JTable tabla = crearTablaModelo();
        bodyPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(bodyPanel, BorderLayout.CENTER);
    }

    private JTable crearTablaModelo() {
        String[] columnas = {"Columna 1", "Columna 2", "Columna 3", "Columna 4", "Columna 5", "Columna 6"};
        Object[][] datos = {
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""}
        };
        DefaultTableModel model = new DefaultTableModel(datos, columnas);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);
        return table;
    }
}