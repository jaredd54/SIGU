import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelConsultarInventario extends JPanel {

    public PanelConsultarInventario() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel lblTitulo = new JLabel("Consultar inventario", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 15));
        bodyPanel.setBackground(Color.WHITE);

        // Panel de Control Superior
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"Seleccione una opción", "N° de serie", "N° de inventario", "Bien", "Bajas"});
        comboFiltro.setPreferredSize(new Dimension(200, 32));
        PlaceholderField txtBuscar = new PlaceholderField(" 🔍 Buscar");
        txtBuscar.setPreferredSize(new Dimension(350, 32));
        
        JButton btnConsultar = new JButton("consultar");
        btnConsultar.setBackground(new Color(41, 92, 180));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setPreferredSize(new Dimension(120, 32));

        filterPanel.add(comboFiltro);
        filterPanel.add(txtBuscar);
        filterPanel.add(btnConsultar);
        bodyPanel.add(filterPanel, BorderLayout.NORTH);

        // Distribución de Dos Columnas (Altas vs Bajas)
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setBackground(Color.WHITE);

        JPanel pnlAltas = new JPanel(new BorderLayout());
        pnlAltas.setBorder(BorderFactory.createTitledBorder("Altas"));
        pnlAltas.add(new JScrollPane(crearTablaModelo()), BorderLayout.CENTER);

        JPanel pnlBajas = new JPanel(new BorderLayout());
        pnlBajas.setBorder(BorderFactory.createTitledBorder("Bajas"));
        pnlBajas.add(new JScrollPane(crearTablaModelo()), BorderLayout.CENTER);

        tablesPanel.add(pnlAltas);
        tablesPanel.add(pnlBajas);
        bodyPanel.add(tablesPanel, BorderLayout.CENTER);

        // Exportadores de Datos
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        exportPanel.setBackground(Color.WHITE);
        JButton btnPdf = new JButton("Exportar .PDF");
        JButton btnXls = new JButton("Exportar .XLS");
        estiloBoton(btnPdf); estiloBoton(btnXls);
        exportPanel.add(btnPdf); exportPanel.add(btnXls);
        
        bodyPanel.add(exportPanel, BorderLayout.SOUTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    private JTable crearTablaModelo() {
        String[] columnas = {"Columna 1", "Columna 2", "Columna 3"};
        Object[][] datos = {{"dfghj", "dfghj", "dfghj"}, {"dfghj", "dfghj", "dfghj"}};
        DefaultTableModel model = new DefaultTableModel(datos, columnas);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);
        return table;
    }

    private void estiloBoton(JButton btn) {
        btn.setBackground(new Color(41, 92, 180));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(150, 40));
    }
}