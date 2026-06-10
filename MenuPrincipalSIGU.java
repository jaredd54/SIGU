import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuPrincipalSIGU extends JFrame {

    private JPanel centerCardPanel;
    private CardLayout cardLayout;
    private JButton[] sidebarButtons;
    private String[] opcionesSidebar = {"Agregar nuevo bien", "Editar", "Consultar inventario", "Eliminar registro"};

    public MenuPrincipalSIGU() {
        setTitle("SIGU - Sistema de Inventario Global Universitario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa automática
        setMinimumSize(new Dimension(1100, 750));
        setLayout(new BorderLayout());

        // 1. HEADER (NORTE)
        add(crearHeader(), BorderLayout.NORTH);

        // 2. SIDEBAR (OESTE)
        add(crearSidebar(), BorderLayout.WEST);

        // 3. CONTENEDOR DINÁMICO (CENTRO)
        cardLayout = new CardLayout();
        centerCardPanel = new JPanel(cardLayout);
        centerCardPanel.setBackground(Color.WHITE);

        // INSTANCIAMOS E INYECTAMOS LAS NUEVAS CLASES (JPanels)
        centerCardPanel.add(new PanelAgregarBien(), "Agregar nuevo bien");
        centerCardPanel.add(new PanelEditarBien(), "Editar");
        centerCardPanel.add(new PanelConsultarInventario(), "Consultar inventario");
        centerCardPanel.add(new PanelEliminarRegistro(), "Eliminar registro");

        add(centerCardPanel, BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel lblLogo = new JLabel("  SIGU");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(lblLogo, BorderLayout.WEST);

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        profilePanel.setBackground(Color.WHITE);
        
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(230, 230, 230));
                g2d.fillOval(0, 0, 35, 35);
                g2d.setColor(Color.GRAY);
                g2d.fillOval(12, 8, 11, 11);
                g2d.fillArc(8, 20, 19, 19, 0, 180);
            }
        };
        avatar.setPreferredSize(new Dimension(35, 35));
        avatar.setBackground(Color.WHITE);

        JComboBox<String> userCombo = new JComboBox<>(new String[]{"Administrador/ Usuario"});
        userCombo.setPreferredSize(new Dimension(180, 28));

        profilePanel.add(avatar);
        profilePanel.add(userCombo);
        header.add(profilePanel, BorderLayout.EAST);

        return header;
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(252, 252, 252));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarButtons = new JButton[opcionesSidebar.length];

        for (int i = 0; i < opcionesSidebar.length; i++) {
            String opcion = opcionesSidebar[i];
            JButton btn = new JButton(opcion);
            btn.setMaximumSize(new Dimension(240, 45));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorder(new EmptyBorder(10, 25, 10, 10));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setFont(new Font("Arial", Font.PLAIN, 15));
            
            if (i == 0) {
                btn.setForeground(new Color(41, 92, 180));
                btn.setFont(new Font("Arial", Font.BOLD, 15));
            } else {
                btn.setForeground(Color.GRAY);
            }

            btn.addActionListener(e -> {
                cardLayout.show(centerCardPanel, opcion);
                actualizarEstiloSidebar(opcion);
            });

            sidebarButtons[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        return sidebar;
    }

    private void actualizarEstiloSidebar(String opcionSeleccionada) {
        for (JButton btn : sidebarButtons) {
            if (btn.getText().equals(opcionSeleccionada)) {
                btn.setForeground(new Color(41, 92, 180));
                btn.setFont(new Font("Arial", Font.BOLD, 15));
            } else {
                btn.setForeground(Color.GRAY);
                btn.setFont(new Font("Arial", Font.PLAIN, 15));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipalSIGU().setVisible(true);
        });
    }
}