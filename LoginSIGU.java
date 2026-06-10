import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class LoginSIGU extends JFrame {

    public LoginSIGU() {
        // Configuración básica de la ventana (JFrame)
        setTitle("SIGU - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null); 
        getContentPane().setBackground(Color.WHITE); 
        setLayout(new GridBagLayout()); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0); 

        // --- 1. SECCIÓN DEL ENCABEZADO (SIGU) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("SIGU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 48));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Sistema de Inventario Global", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSubTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubTitle2 = new JLabel("Universitario", SwingConstants.CENTER);
        lblSubTitle2.setFont(new Font("Arial", Font.PLAIN, 18));
        lblSubTitle2.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lblSubTitle);
        headerPanel.add(lblSubTitle2);

        gbc.gridy = 0;
        add(headerPanel, gbc);


        // --- 2. SECCIÓN DEL CONTENEDOR AZUL (LOGIN CARD) ---
        JPanel loginCard = new JPanel();
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        loginCard.setLayout(new GridBagLayout());
        loginCard.setPreferredSize(new Dimension(360, 440)); 

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.insets = new Insets(8, 30, 8, 30); 

        // Avatar dibujado por código para evitar el cuadro vacío
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Círculo gris de fondo
                g2d.setColor(new Color(230, 230, 230));
                g2d.fillOval(5, 5, 80, 80);
                
                // Silueta del avatar (Cabeza y hombros)
                g2d.setColor(Color.GRAY);
                g2d.fillOval(34, 18, 22, 22); // Cabeza
                g2d.fillArc(20, 48, 50, 50, 0, 180); // Hombros
            }
        };
        avatarPanel.setPreferredSize(new Dimension(90, 90));
        avatarPanel.setBackground(Color.WHITE);
        
        cardGbc.gridy = 0;
        cardGbc.insets = new Insets(20, 30, 15, 30);
        // Usamos un panel intermedio para centrar el avatar bien
        JPanel centerAvatar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerAvatar.setBackground(Color.WHITE);
        centerAvatar.add(avatarPanel);
        loginCard.add(centerAvatar, cardGbc);
        cardGbc.insets = new Insets(8, 30, 8, 30); 

        // Etiqueta Usuario
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        cardGbc.gridy = 1;
        loginCard.add(lblUsuario, cardGbc);

        // Campo de Texto Usuario
        JTextFieldHint txtUsuario = new JTextFieldHint("nombre de usuario");
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(200, 35));
        txtUsuario.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        cardGbc.gridy = 2;
        loginCard.add(txtUsuario, cardGbc);

        // Etiqueta Contraseña
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 14));
        cardGbc.gridy = 3;
        loginCard.add(lblPassword, cardGbc);

        // --- CONTENEDOR PARA CONTRASEÑA + BOTÓN OJO DIBUJADO ---
        JPanel passwordContainer = new JPanel(new BorderLayout());
        passwordContainer.setBackground(Color.WHITE);
        passwordContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        passwordContainer.setPreferredSize(new Dimension(200, 35));

        JPasswordFieldHint txtPassword = new JPasswordFieldHint("escribe tu contraseña");
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        // El botón ahora dibuja los ojos con vectores gráficos matemáticos
        EyeButton btnTogglePassword = new EyeButton();
        btnTogglePassword.setPreferredSize(new Dimension(40, 35));
        btnTogglePassword.setBorder(BorderFactory.createEmptyBorder());
        btnTogglePassword.setContentAreaFilled(false);
        btnTogglePassword.setFocusPainted(false);
        btnTogglePassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        passwordContainer.add(txtPassword, BorderLayout.CENTER);
        passwordContainer.add(btnTogglePassword, BorderLayout.EAST);

        cardGbc.gridy = 4;
        loginCard.add(passwordContainer, cardGbc);

        // Botón Iniciar Sesión
        JButton btnLogin = new JButton("iniciar sesión");
        btnLogin.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLogin.setBackground(new Color(41, 92, 180)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(150, 40));
        cardGbc.gridy = 5;
        cardGbc.insets = new Insets(20, 60, 20, 60); 
        loginCard.add(btnLogin, cardGbc);
        cardGbc.insets = new Insets(8, 30, 8, 30); 

        // Enlace de Olvidaste tu contraseña
        JLabel lblForgot = new JLabel("<html><u>¿Olvidaste tu contraseña? Haz clic aquí</u></html>", SwingConstants.CENTER);
        lblForgot.setFont(new Font("Arial", Font.PLAIN, 12));
        lblForgot.setForeground(new Color(41, 92, 180));
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cardGbc.gridy = 6;
        cardGbc.insets = new Insets(0, 20, 20, 20);
        loginCard.add(lblForgot, cardGbc);

        gbc.gridy = 1;
        add(loginCard, gbc);

        // --- LÓGICA DEL INTERRUPTOR ---
        btnTogglePassword.addActionListener(new ActionListener() {
            private boolean isPasswordVisible = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPasswordVisible) {
                    txtPassword.setEchoChar('•');
                    btnTogglePassword.setPasswordVisible(false);
                } else {
                    txtPassword.setEchoChar((char) 0);
                    btnTogglePassword.setPasswordVisible(true);
                }
                isPasswordVisible = !isPasswordVisible;
                txtPassword.requestFocus();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText();
                JOptionPane.showMessageDialog(null, "Intentando conectar con el usuario: " + usuario);
            }
        });
    }

    // --- CLASE PARA DIBUJAR EL OJO CLÁSICO SIN DEPENDER DE FUENTES ---
    class EyeButton extends JButton {
        private boolean isPasswordVisible = false;

        public void setPasswordVisible(boolean visible) {
            this.isPasswordVisible = visible;
            repaint(); // Redibuja el botón con el estado correcto
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.5f));

            // Coordenadas para la forma curva del ojo (Arriba y abajo)
            Path2D eyePath = new Path2D.Double();
            eyePath.moveTo(w * 0.25, h * 0.5);
            eyePath.quadTo(w * 0.5, h * 0.25, w * 0.75, h * 0.5);
            eyePath.quadTo(w * 0.5, h * 0.75, w * 0.25, h * 0.5);
            eyePath.closePath();
            g2d.draw(eyePath);

            // El iris (Círculo del centro)
            g2d.fill(new Ellipse2D.Double(w * 0.42, h * 0.38, w * 0.16, w * 0.16));

            // Si la contraseña está oculta, le cruza una línea diagonal (Ojo tachado)
            if (!isPasswordVisible) {
                g2d.drawLine((int)(w * 0.28), (int)(h * 0.32), (int)(w * 0.72), (int)(h * 0.68));
            }
        }
    }

    // --- CLASES PERSONALIZADAS PARA LOS PLACEHOLDERS ---
    class JTextFieldHint extends JTextField {
        private String hint;
        public JTextFieldHint(String hint) { this.hint = hint; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.GRAY);
                int paddingX = getInsets().left + 5;
                int paddingY = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                g2d.drawString(hint, paddingX, paddingY);
            }
        }
    }

    class JPasswordFieldHint extends JPasswordField {
        private String hint;
        public JPasswordFieldHint(String hint) { this.hint = hint; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.GRAY);
                int paddingX = getInsets().left + 5;
                int paddingY = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                g2d.drawString(hint, paddingX, paddingY);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginSIGU().setVisible(true);
        });
    }
}