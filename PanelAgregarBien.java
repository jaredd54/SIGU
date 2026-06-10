import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PanelAgregarBien extends JPanel {

    public PanelAgregarBien() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel lblTitulo = new JLabel("Agregar nuevo Bien", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        add(lblTitulo, BorderLayout.NORTH);

        // Formulario con GridBagLayout
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] etiquetas = {
            "N° de serie:", "N° de inventario:", "N° de dependencia:", "Subunidad responsable:",
            "Descripción del bien:", "Marca:", "Color:", "Material:", "Serie:", "Estado físico:",
            "N° de talón:", "N° de cheque:", "Folio de operación:", "N° de factura o título:",
            "Fecha de factura o título:", "Nombre. Proveedor:", "Costo de adquisición:",
            "Clasificador del objeto del gasto (partida genérica):",
            "Clasificador del objeto por costo (partida específica):",
            "Observaciones:", "Nombre:", "Expediente:"
        };

        for (int i = 0; i < etiquetas.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.2;
            JLabel lbl = new JLabel(etiquetas[i]);
            lbl.setFont(new Font("Arial", Font.PLAIN, 15));
            formGrid.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.8;
            if (etiquetas[i].equals("Estado físico:")) {
                JComboBox<String> combo = new JComboBox<>(new String[]{"Bueno", "Regular", "Malo"});
                formGrid.add(combo, gbc);
            } else {
                JTextField txt = new JTextField();
                txt.setPreferredSize(new Dimension(350, 30));
                formGrid.add(txt, gbc);
            }
        }

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollForm, BorderLayout.CENTER);

        // Botones Inferiores
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBotones.setBackground(Color.WHITE);
        JButton btnCarga = new JButton("Carga masiva");
        JButton btnGuardar = new JButton("Guardar registro");
        
        estiloBoton(btnCarga);
        estiloBoton(btnGuardar);
        
        pnlBotones.add(btnCarga); pnlBotones.add(btnGuardar);
        add(pnlBotones, BorderLayout.SOUTH);
    }

    private void estiloBoton(JButton btn) {
        btn.setBackground(new Color(41, 92, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(160, 40));
    }
}