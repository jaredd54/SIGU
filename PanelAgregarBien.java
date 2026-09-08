import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelAgregarBien extends JPanel {

    private Map<String, Component> camposFormulario;

    // Mapas en memoria para la consulta rápida
    private Map<String, String> trabajadorASubunidad = new HashMap<>();
    private Map<String, String> subunidadATrabajador = new HashMap<>();

    // Componentes para el menú desplegable de sugerencias tipo Google
    private JPopupMenu menuSugerencias;
    private boolean actualizandoCampos = false;

    private String[] etiquetas = {
        "N° de serie:", 
        "N° de dependencia:", 
        "unidad responsable:", 
        "N° de trabajador",
        "Subunidad responsable:",
        "Descripción del bien:", 
        "Marca:", 
        "Color:", 
        "Material:", 
        "Serie:", 
        "Estado físico:",
        "N° de talón:", 
        "N° de cheque:", 
        "Folio de operación:", 
        "N° de factura o título:",
        "Fecha de factura o título:", 
        "Nombre. Proveedor:", 
        "Costo de adquisición:",
        "Clasificador del objeto del gasto (partida genérica):",
        "Clasificador del objeto por costo (partida específica):",
        "Tipo de recurso:",  
        "Observaciones:" 
    };

    public PanelAgregarBien() {
        camposFormulario = new HashMap<>();
        menuSugerencias = new JPopupMenu();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel lblTitulo = new JLabel("Agregar Nuevo Bien", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        add(lblTitulo, BorderLayout.NORTH);

        // Formulario con GridBagLayout
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < etiquetas.length; i++) {
            String etiqueta = etiquetas[i];
            
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.2;
            JLabel lbl = new JLabel(etiqueta);
            lbl.setFont(new Font("Arial", Font.PLAIN, 15));
            formGrid.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.8;
            if (etiqueta.equals("Estado físico:")) {
                JComboBox<String> combo = new JComboBox<>(new String[]{"Bueno", "Regular", "Malo"});
                combo.setPreferredSize(new Dimension(350, 30));
                camposFormulario.put(etiqueta, combo);
                formGrid.add(combo, gbc);
            } else {
                JTextField txt = new JTextField();
                txt.setPreferredSize(new Dimension(350, 30));
                camposFormulario.put(etiqueta, txt);
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

        btnGuardar.addActionListener(e -> guardarRegistro());
        btnCarga.addActionListener(e -> procesarCargaMasiva());

        pnlBotones.add(btnCarga); 
        pnlBotones.add(btnGuardar);
        add(pnlBotones, BorderLayout.SOUTH);

        // Cargar datos y activar la autocompletación interactiva
        cargarDatosTrabajadores();
        configurarBuscadorSugerencias();
    }

    private void estiloBoton(JButton btn) {
        btn.setBackground(new Color(41, 92, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(160, 40));
    }

    private void cargarDatosTrabajadores() {
        Connection con = Conectar.getConexion();
        if (con == null) return;

        String sql = "SELECT num_trabajador, nombre_trabajador FROM trabajadores";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String numTrab = rs.getString("num_trabajador");
                String nomTrab = rs.getString("nombre_trabajador");

                if (numTrab != null && nomTrab != null) {
                    trabajadorASubunidad.put(numTrab.trim(), nomTrab.trim());
                    subunidadATrabajador.put(nomTrab.trim(), numTrab.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la lista de trabajadores: " + e.getMessage());
        } finally {
            Conectar.cerrarConexion(con);
        }
    }

    private void configurarBuscadorSugerencias() {
        JTextField txtSubunidad = (JTextField) camposFormulario.get("Subunidad responsable:");
        JTextField txtNumTrabajador = (JTextField) camposFormulario.get("N° de trabajador");

        if (txtSubunidad == null || txtNumTrabajador == null) return;

        txtSubunidad.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscarCoincidencias(); }
            public void removeUpdate(DocumentEvent e) { buscarCoincidencias(); }
            public void changedUpdate(DocumentEvent e) { buscarCoincidencias(); }

            private void buscarCoincidencias() {
                if (actualizandoCampos) return;

                SwingUtilities.invokeLater(() -> {
                    String texto = txtSubunidad.getText().trim().toLowerCase();
                    menuSugerencias.setVisible(false);
                    menuSugerencias.removeAll();

                    if (texto.isEmpty()) {
                        actualizandoCampos = true;
                        txtNumTrabajador.setText("");
                        actualizandoCampos = false;
                        return;
                    }

                    int coincidencias = 0;
                    for (Map.Entry<String, String> entry : subunidadATrabajador.entrySet()) {
                        String nombreReal = entry.getKey();
                        String numTrab = entry.getValue();

                        if (nombreReal.toLowerCase().contains(texto)) {
                            JMenuItem item = new JMenuItem(nombreReal + " (N°: " + numTrab + ")");
                            item.setFont(new Font("Arial", Font.PLAIN, 13));
                            item.addActionListener(evt -> {
                                actualizandoCampos = true;
                                txtSubunidad.setText(nombreReal);
                                txtNumTrabajador.setText(numTrab);
                                actualizandoCampos = false;
                                menuSugerencias.setVisible(false);
                            });
                            menuSugerencias.add(item);
                            coincidencias++;
                        }
                    }

                    if (coincidencias > 0) {
                        menuSugerencias.setFocusable(false);
                        menuSugerencias.show(txtSubunidad, 0, txtSubunidad.getHeight());
                        txtSubunidad.requestFocus();
                    }
                });
            }
        });

        txtNumTrabajador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscarPorNumero(); }
            public void removeUpdate(DocumentEvent e) { buscarPorNumero(); }
            public void changedUpdate(DocumentEvent e) { buscarPorNumero(); }

            private void buscarPorNumero() {
                if (actualizandoCampos) return;
                String num = txtNumTrabajador.getText().trim();

                if (trabajadorASubunidad.containsKey(num)) {
                    actualizandoCampos = true;
                    txtSubunidad.setText(trabajadorASubunidad.get(num));
                    actualizandoCampos = false;
                    menuSugerencias.setVisible(false);
                }
            }
        });
    }

    private String getValorCampo(String etiqueta) {
        Component comp = camposFormulario.get(etiqueta);
        if (comp instanceof JTextField) {
            return ((JTextField) comp).getText().trim();
        } else if (comp instanceof JComboBox) {
            return ((JComboBox<?>) comp).getSelectedItem().toString();
        }
        return "";
    }

    private void limpiarFormulario() {
        actualizandoCampos = true;
        for (Component comp : camposFormulario.values()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setSelectedIndex(0);
            }
        }
        actualizandoCampos = false;
    }

    private void guardarRegistro() {
        String numSerie = getValorCampo("N° de serie:");
        String descripcion = getValorCampo("Descripción del bien:");

        if (numSerie.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete al menos el N° de Serie y la Descripción del Bien.",
                "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection con = Conectar.getConexion();
        if (con == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo establecer conexión con la base de datos.",
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String moduloActual = MenuPrincipalSIGU.moduloSeleccionado;
        String tabla = moduloActual.equalsIgnoreCase("Bienes Muebles") ? "bienes_muebles" : "bienes_inmuebles";

        String sql = "INSERT INTO " + tabla + " ("
                + "num_serie, num_dependencia, unidad_responsable, num_trabajador, "
                + "subunidad_responsable, descripcion_bien, marca, color, material, "
                + "serie, estado_fisico, num_talon, num_cheque, folio_operacion, "
                + "num_factura_titulo, fecha_factura, num_proveedor, costo_adquisicion, "
                + "partida_generica, partida_especifica, tipo_recurso, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numSerie);
            ps.setString(2, getValorCampo("N° de dependencia:"));
            ps.setString(3, getValorCampo("unidad responsable:"));

            String numTrabStr = getValorCampo("N° de trabajador");
            if (numTrabStr.isEmpty()) {
                ps.setInt(4, 0);
            } else {
                try {
                    ps.setInt(4, Integer.parseInt(numTrabStr));
                } catch (NumberFormatException e) {
                    ps.setInt(4, 0);
                }
            }

            ps.setString(5, getValorCampo("Subunidad responsable:"));
            ps.setString(6, descripcion);
            ps.setString(7, getValorCampo("Marca:"));
            ps.setString(8, getValorCampo("Color:"));
            ps.setString(9, getValorCampo("Material:"));
            ps.setString(10, getValorCampo("Serie:"));

            String estadoFisico = getValorCampo("Estado físico:").toLowerCase();
            ps.setString(11, estadoFisico);

            ps.setString(12, getValorCampo("N° de talón:"));
            ps.setString(13, getValorCampo("N° de cheque:"));
            ps.setString(14, getValorCampo("Folio de operación:"));
            ps.setString(15, getValorCampo("N° de factura o título:"));
            ps.setString(16, getValorCampo("Fecha de factura o título:"));
            ps.setString(17, getValorCampo("Nombre. Proveedor:"));
            ps.setString(18, getValorCampo("Costo de adquisición:"));
            ps.setString(19, getValorCampo("Clasificador del objeto del gasto (partida genérica):"));
            ps.setString(20, getValorCampo("Clasificador del objeto por costo (partida específica):"));
            ps.setString(21, getValorCampo("tipo de recurso:"));
            ps.setString(22, getValorCampo("Observaciones:"));

            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, 
                    "¡Registro guardado exitosamente en " + moduloActual + "!",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
            }

        } catch (Exception ex) {
            System.err.println("Error SQL: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al guardar en la base de datos:\n" + ex.getMessage(),
                "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            Conectar.cerrarConexion(con);
        }
    }

    private void procesarCargaMasiva() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo CSV para Carga Masiva (" + MenuPrincipalSIGU.moduloSeleccionado + ")");
        fc.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));

        int res = fc.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = fc.getSelectedFile();
        String moduloActual = MenuPrincipalSIGU.moduloSeleccionado;
        String tabla = moduloActual.equalsIgnoreCase("Bienes Muebles") ? "bienes_muebles" : "bienes_inmuebles";

        // Ventana modal con la barra de progreso
        JDialog dialogProgreso = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Procesando Carga Masiva", true);
        dialogProgreso.setLayout(new BorderLayout(10, 10));
        dialogProgreso.setSize(420, 150);
        dialogProgreso.setLocationRelativeTo(this);
        dialogProgreso.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JLabel lblEstado = new JLabel("Contando líneas del archivo...", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 14));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setValue(0);

        JPanel pnlContenido = new JPanel(new GridLayout(2, 1, 10, 10));
        pnlContenido.setBorder(new EmptyBorder(15, 20, 15, 20));
        pnlContenido.add(lblEstado);
        pnlContenido.add(progressBar);

        dialogProgreso.add(pnlContenido, BorderLayout.CENTER);

        // Hilo secundario para procesar la lectura e inserción
        SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
            private int totalLineas = 0;

            @Override
            protected Integer doInBackground() throws Exception {
                // 1. Contar total de líneas reales del CSV para fijar el máximo de la barra
                try (BufferedReader readerCount = new BufferedReader(new FileReader(archivo))) {
                    while (readerCount.readLine() != null) {
                        totalLineas++;
                    }
                }
                if (totalLineas > 0) totalLineas--; // Restar encabezado

                String sql = "INSERT INTO " + tabla + " ("
                        + "num_serie, num_dependencia, unidad_responsable, num_trabajador, "
                        + "subunidad_responsable, descripcion_bien, marca, color, material, "
                        + "serie, estado_fisico, num_talon, num_cheque, folio_operacion, "
                        + "num_factura_titulo, fecha_factura, num_proveedor, costo_adquisicion, "
                        + "partida_generica, partida_especifica, tipo_recurso, observaciones) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                Connection con = Conectar.getConexion();
                if (con == null) {
                    throw new Exception("No se pudo establecer conexión con la base de datos.");
                }

                int registrosInsertados = 0;

                try (BufferedReader br = new BufferedReader(new FileReader(archivo));
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    con.setAutoCommit(false); // Transacción agrupada

                    String linea;
                    boolean primeraLinea = true;

                    while ((linea = br.readLine()) != null) {
                        if (linea.trim().isEmpty()) continue;

                        if (primeraLinea) {
                            primeraLinea = false;
                            if (linea.toLowerCase().contains("serie") || linea.toLowerCase().contains("dependencia")) {
                                continue;
                            }
                        }

                        // Parseo respetando comillas
                        String[] datos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        String[] valores = new String[22];

                        for (int i = 0; i < 22; i++) {
                            if (i < datos.length) {
                                String val = datos[i].trim();
                                if (val.startsWith("\"") && val.endsWith("\"")) {
                                    val = val.substring(1, val.length() - 1).replace("\"\"", "\"");
                                }
                                valores[i] = val;
                            } else {
                                valores[i] = "";
                            }
                        }

                        // Parámetros SQL
                        ps.setString(1, valores[0]);
                        ps.setString(2, valores[1]);
                        ps.setString(3, valores[2]);

                        try {
                            ps.setInt(4, valores[3].isEmpty() ? 0 : Integer.parseInt(valores[3]));
                        } catch (NumberFormatException e) {
                            ps.setInt(4, 0);
                        }

                        ps.setString(5, valores[4]);
                        ps.setString(6, valores[5]);
                        ps.setString(7, valores[6]);
                        ps.setString(8, valores[7]);
                        ps.setString(9, valores[8]);
                        ps.setString(10, valores[9]);

                        String estado = valores[10].toLowerCase();
                        ps.setString(11, estado.isEmpty() ? "bueno" : estado);

                        ps.setString(12, valores[11]);
                        ps.setString(13, valores[12]);
                        ps.setString(14, valores[13]);
                        ps.setString(15, valores[14]);
                        ps.setString(16, valores[15]);
                        ps.setString(17, valores[16]);
                        ps.setString(18, valores[17]);
                        ps.setString(19, valores[18]);
                        ps.setString(20, valores[19]);
                        ps.setString(21, valores[20]);
                        ps.setString(22, valores[21]);

                        ps.addBatch();
                        registrosInsertados++;

                        // Publicar avance para actualizar la barra en la UI
                        publish(registrosInsertados);

                        if (registrosInsertados % 500 == 0) {
                            ps.executeBatch();
                        }
                    }

                    ps.executeBatch();
                    con.commit();
                    return registrosInsertados;

                } catch (Exception ex) {
                    if (con != null) {
                        try { con.rollback(); } catch (Exception rollbackEx) { }
                    }
                    throw ex;
                } finally {
                    Conectar.cerrarConexion(con);
                }
            }

            @Override
            protected void process(List<Integer> chunks) {
                int ultimoRegistro = chunks.get(chunks.size() - 1);
                if (totalLineas > 0) {
                    int porcentaje = (int) (((double) ultimoRegistro / totalLineas) * 100);
                    progressBar.setValue(porcentaje);
                    lblEstado.setText("Insertando registro " + ultimoRegistro + " de " + totalLineas + "...");
                } else {
                    lblEstado.setText("Insertando registro " + ultimoRegistro + "...");
                }
            }

            @Override
            protected void done() {
                dialogProgreso.dispose(); // Cerrar ventana emergente
                try {
                    int total = get();
                    JOptionPane.showMessageDialog(PanelAgregarBien.this,
                            "¡Carga masiva completada exitosamente!\nSe registraron " + total + " elementos en " + moduloActual + ".",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    System.err.println("Error en Carga Masiva: " + causa.getMessage());
                    JOptionPane.showMessageDialog(PanelAgregarBien.this,
                            "Error al procesar la carga masiva:\n" + causa.getMessage(),
                            "Error de Carga Masiva", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        dialogProgreso.setVisible(true); // Bloquea la interacción del usuario mientras el worker se ejecuta
    }
}