import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipalSIGU extends JFrame {

    private JPanel contentCards; // Para cambiar entre pantallas si fuera necesario
    private CardLayout cardLayout;

    public MenuPrincipalSIGU() {
        setTitle("SIGU - Sistema de Inventario Global Universitario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setMinimumSize(new Dimension(1000, 700));
        
        // --- LAYOUT PRINCIPAL ---
        setLayout(new BorderLayout());

        // 1. HEADER (NORTE)
        add(crearHeader(), BorderLayout.NORTH);

        // 2. SIDEBAR (OESTE)
        add(crearSidebar(), BorderLayout.WEST);

        // 3. CONTENIDO PRINCIPAL (CENTRO) CON SCROLL
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblTituloForm = new JLabel("Agregar nuevo Bien");
        lblTituloForm.setFont(new Font("Arial", Font.BOLD, 28));
        lblTituloForm.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainContent.add(lblTituloForm, BorderLayout.NORTH);

        // Formulario dentro de un ScrollPane
        JPanel formPanel = crearFormulario();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null); // Quitar borde feo del scroll
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll suave
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Botones de acción inferiores
        mainContent.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Logo
        JLabel lblLogo = new JLabel("  SIGU");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(Color.BLACK);
        header.add(lblLogo, BorderLayout.WEST);

        // Usuario / Perfil
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        profilePanel.setBackground(Color.WHITE);
        
        // Avatar (Círculo pequeño)
        JPanel avatar = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(230, 230, 230));
                g2d.fillOval(0, 0, 40, 40);
                g2d.setColor(Color.GRAY);
                g2d.fillOval(14, 10, 12, 12);
                g2d.fillArc(10, 22, 20, 20, 0, 180);
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setBackground(Color.WHITE);

        JComboBox<String> userCombo = new JComboBox<>(new String[]{"Administrador/ Usuario"});
        userCombo.setPreferredSize(new Dimension(200, 30));

        profilePanel.add(avatar);
        profilePanel.add(userCombo);
        header.add(profilePanel, BorderLayout.EAST);

        return header;
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(250, 250, 250));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        String[] opciones = {"Agregar nuevo bien", "Editar", "Consultar inventario", "Eliminar registro"};
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        for (String opcion : opciones) {
            JButton btn = new JButton(opcion);
            btn.setMaximumSize(new Dimension(250, 50));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorder(new EmptyBorder(10, 30, 10, 10));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
            
            // Estilo para la opción seleccionada
            if (opcion.equals("Agregar nuevo bien")) {
                btn.setForeground(new Color(41, 92, 180));
                btn.setFont(new Font("Arial", Font.BOLD, 16));
            } else {
                btn.setForeground(Color.GRAY);
            }

            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return sidebar;
    }

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
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
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            JLabel label = new JLabel(etiquetas[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            form.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            if (etiquetas[i].equals("Estado físico:")) {
                JComboBox<String> combo = new JComboBox<>(new String[]{"Bueno", "Regular", "Malo"});
                form.add(combo, gbc);
            } else {
                JTextField field = new JTextField();
                field.setPreferredSize(new Dimension(400, 30));
                form.add(field, gbc);
            }
        }

        return form;
    }

    private JPanel crearPanelBotones() {
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        pnlBotones.setBackground(Color.WHITE);

        JButton btnCarga = new JButton("Carga masiva");
        estiloBoton(btnCarga, new Color(41, 92, 180));

        JButton btnGuardar = new JButton("Guardar registro");
        estiloBoton(btnGuardar, new Color(41, 92, 180));

        pnlBotones.add(btnCarga);
        pnlBotones.add(btnGuardar);

        return pnlBotones;
    }

    private void estiloBoton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(180, 45));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipalSIGU().setVisible(true);
        });
    }
}
