package org.example.integrales.ui;

import org.example.integrales.core.ServicioIntegracion;
import org.example.integrales.core.ServicioIntegracion.Error;
import org.example.integrales.core.ServicioIntegracion.Exito;
import org.example.integrales.core.ServicioIntegracion.Resultado;
import org.example.integrales.core.ServicioIntegracion.Solicitud;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public final class Ventana extends JFrame {
    private final JTextField expresion = campo("Función f(x)");
    private final JTextField inferior = campo("Límite inferior (a)");
    private final JTextField superior = campo("Límite superior (b)");
    private final JComboBox<Opcion> ejemplos = new JComboBox<>();
    private final JButton calcular = Tema.botonResolver();
    private final Notacion notacion = new Notacion();
    private final JLabel estado = etiqueta("", Tema.CLARO_SUAVE, 14, Font.PLAIN);
    private final JLabel primitiva = etiqueta("", Tema.CLARO, 19, Font.PLAIN);
    private final JLabel evaluacion = etiqueta("", Tema.CLARO, 19, Font.PLAIN);
    private final JLabel valor = etiqueta("", Tema.MENTA, 39, Font.BOLD);
    private final JPanel detalles = vertical();
    private boolean cargandoEjemplo;

    public Ventana(ServicioIntegracion servicio) {
        this(servicio, Modulo.values());
    }

    public Ventana(ServicioIntegracion servicio, Modulo... disponibles) {
        super("Calculadora de integrales");
        JPanel contenido = new JPanel(new BorderLayout(0, 19));
        contenido.setBackground(Tema.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contenido.add(encabezado(disponibles), BorderLayout.NORTH);

        JPanel columnas = new JPanel(new GridLayout(1, 2, 15, 0));
        columnas.setOpaque(false);
        columnas.add(editor());
        columnas.add(resultado());
        contenido.add(columnas, BorderLayout.CENTER);
        setContentPane(contenido);

        DocumentListener cambios = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
            private void actualizar() {
                if (cargandoEjemplo) return;
                if (ejemplos.getSelectedIndex() > 0) ejemplos.setSelectedIndex(0);
                pendiente();
            }
        };
        for (JTextField campo : new JTextField[]{expresion, inferior, superior}) {
            campo.getDocument().addDocumentListener(cambios);
        }
        ejemplos.addActionListener(e -> {
            if (ejemplos.getSelectedItem() instanceof Opcion opcion && opcion.solicitud() != null) {
                cargar(opcion.solicitud());
            }
        });
        for (int i = 1; i < ejemplos.getItemCount(); i++) {
            if (ejemplos.getItemAt(i).solicitud().equals(new Solicitud("x^2", "0", "3"))) {
                ejemplos.setSelectedIndex(i);
                break;
            }
        }
        pendiente();
        calcular.addActionListener(e -> consultar(servicio));
        getRootPane().setDefaultButton(calcular);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(820, 480));
        setSize(995, 520);
        setLocationRelativeTo(null);
    }

    private JPanel encabezado(Modulo[] disponibles) {
        JPanel cabecera = new JPanel(new BorderLayout(16, 0));
        cabecera.setOpaque(false);
        cabecera.add(etiqueta("Calculadora de integrales", Tema.TINTA, 39, Font.BOLD), BorderLayout.CENTER);
        ejemplos.setFont(Tema.texto(Font.PLAIN, 14));
        Tema.selector(ejemplos);
        ejemplos.setPrototypeDisplayValue(new Opcion("r) cosh(x)", null));
        ejemplos.addItem(new Opcion("Seleccionar…", null));
        for (Modulo modulo : disponibles) {
            for (var ejemplo : modulo.crearPanel().ejemplos()) {
                int finLiteral = ejemplo.titulo().indexOf(')');
                String literal = ejemplo.titulo().substring(0, finLiteral + 1);
                ejemplos.addItem(new Opcion(literal + " " + ejemplo.solicitud().expresion(), ejemplo.solicitud()));
            }
        }
        ejemplos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object dato, int indice,
                                                          boolean seleccionado, boolean foco) {
                Object texto = dato instanceof Opcion opcion ? opcion.texto() : dato;
                JLabel celda = (JLabel) super.getListCellRendererComponent(lista, texto, indice, seleccionado, foco);
                celda.setFont(Tema.texto(Font.PLAIN, 14));
                celda.setOpaque(indice >= 0);
                celda.setBackground(seleccionado ? Tema.DISTINTIVO : Tema.SUPERFICIE);
                celda.setForeground(ejemplos.isEnabled() ? Tema.TINTA : Tema.SUAVE);
                celda.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                return celda;
            }
        });
        ejemplos.setPreferredSize(new Dimension(270, 42));
        cabecera.add(campoConEtiqueta("Cargar ejemplo", ejemplos), BorderLayout.EAST);
        return cabecera;
    }

    private JPanel editor() {
        JPanel editor = new JPanel(new BorderLayout(0, 12));
        editor.setOpaque(false);
        JPanel entrada = panel(Tema.SUPERFICIE, Tema.BORDE);
        entrada.add(titulo("01", "Expresión", false), BorderLayout.NORTH);
        entrada.add(campoConEtiqueta("Función f(x)", expresion), BorderLayout.CENTER);
        editor.add(entrada, BorderLayout.NORTH);

        JPanel intervalo = panel(Tema.SUPERFICIE, Tema.BORDE);
        intervalo.add(titulo("02", "Intervalo", false), BorderLayout.NORTH);
        JPanel limites = new JPanel(new GridLayout(1, 2, 9, 0));
        limites.setOpaque(false);
        limites.add(campoConEtiqueta("Límite inferior (a)", inferior));
        limites.add(campoConEtiqueta("Límite superior (b)", superior));
        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.add(limites, BorderLayout.NORTH);
        calcular.setFont(Tema.texto(Font.BOLD, 15));
        calcular.setHorizontalAlignment(SwingConstants.LEFT);
        calcular.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 32));
        cuerpo.add(calcular, BorderLayout.SOUTH);
        intervalo.add(cuerpo, BorderLayout.CENTER);
        editor.add(intervalo, BorderLayout.CENTER);
        return editor;
    }

    private JPanel resultado() {
        JPanel panel = panel(Tema.PETROLEO, null);
        JPanel arriba = new JPanel(new BorderLayout(0, 12));
        arriba.setOpaque(false);
        arriba.add(titulo("03", "Resultado", true), BorderLayout.NORTH);
        arriba.add(notacion.panel(), BorderLayout.CENTER);
        panel.add(arriba, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        primitiva.setFont(Tema.formula(20));
        evaluacion.setFont(Tema.formula(20));
        detalles.add(fila("Primitiva F(x)", primitiva, Tema.CLARO_SUAVE, true));
        detalles.add(fila("Evaluación F(b) − F(a)", evaluacion, Tema.CLARO_SUAVE, true));
        detalles.add(fila("Integral definida", valor, Tema.MENTA, false));
        cuerpo.add(detalles, BorderLayout.NORTH);
        cuerpo.add(estado, BorderLayout.SOUTH);
        panel.add(cuerpo, BorderLayout.CENTER);
        return panel;
    }

    private Solicitud solicitud() {
        return new Solicitud(expresion.getText().trim(), inferior.getText().trim(), superior.getText().trim());
    }

    private void cargar(Solicitud ejemplo) {
        cargandoEjemplo = true;
        expresion.setText(ejemplo.expresion());
        inferior.setText(ejemplo.a());
        superior.setText(ejemplo.b());
        cargandoEjemplo = false;
        pendiente();
    }

    private void pendiente() {
        notacion.actualizar(solicitud());
        mostrarEstado("Presiona 'Resolver Integral' para obtener el resultado.", false);
    }

    private void mostrarEstado(String mensaje, boolean error) {
        detalles.setVisible(false);
        estado.setVisible(true);
        estado.setText("<html>" + escapar(mensaje) + "</html>");
        estado.setForeground(error ? Tema.ERROR : Tema.CLARO_SUAVE);
    }

    private void consultar(ServicioIntegracion servicio) {
        Solicitud solicitud = solicitud();
        if (solicitud.expresion().isBlank() || solicitud.a().isBlank() || solicitud.b().isBlank()) {
            mostrarEstado("Ingresá la función y ambos límites.", true);
            return;
        }
        habilitar(false);
        mostrarEstado("Calculando…", false);
        new SwingWorker<Resultado, Void>() {
            @Override
            protected Resultado doInBackground() throws Exception {
                return servicio.resolver(solicitud);
            }

            @Override
            protected void done() {
                try {
                    Resultado respuesta = get();
                    if (respuesta instanceof Exito exito) {
                        primitiva.setText(Notacion.formatear(exito.primitiva()) + " + C");
                        primitiva.setToolTipText(primitiva.getText());
                        String fa = Notacion.numero(exito.fa());
                        evaluacion.setText(Notacion.numero(exito.fb()) + " − " + (exito.fa() < 0 ? "(" + fa + ")" : fa));
                        evaluacion.setToolTipText(evaluacion.getText());
                        valor.setText(Notacion.numero(exito.valor()));
                        estado.setVisible(false);
                        detalles.setVisible(true);
                    } else if (respuesta instanceof Error error) {
                        mostrarEstado(error.mensaje(), true);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarEstado("Se interrumpió el cálculo.", true);
                } catch (ExecutionException e) {
                    mostrarEstado("No se pudo consultar SWI-Prolog: " + e.getCause().getMessage(), true);
                } finally {
                    habilitar(true);
                }
            }
        }.execute();
    }

    private void habilitar(boolean activo) {
        calcular.setEnabled(activo);
        ejemplos.setEnabled(activo);
        expresion.setEditable(activo);
        inferior.setEditable(activo);
        superior.setEditable(activo);
    }

    private static JTextField campo(String nombre) {
        JTextField campo = new JTextField();
        campo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        Tema.control(campo, 8, 10);
        campo.setCaretColor(Tema.TINTA);
        campo.setSelectionColor(Tema.DISTINTIVO);
        campo.setSelectedTextColor(Tema.TINTA);
        campo.getAccessibleContext().setAccessibleName(nombre);
        return campo;
    }

    private static JPanel campoConEtiqueta(String nombre, JComponent campo) {
        JPanel bloque = new JPanel(new BorderLayout(0, 4));
        bloque.setOpaque(false);
        JLabel label = etiqueta(nombre, Tema.SUAVE, 13, Font.BOLD);
        label.setLabelFor(campo);
        bloque.add(label, BorderLayout.NORTH);
        bloque.add(campo, BorderLayout.CENTER);
        return bloque;
    }

    private static JPanel panel(Color fondo, Color borde) {
        JPanel panel = Tema.superficie(fondo, borde);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return panel;
    }

    private static JPanel vertical() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private static JPanel titulo(String paso, String texto, boolean resultado) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fila.setOpaque(false);
        JPanel distintivo = Tema.superficie(resultado ? Tema.DISTINTIVO_RESULTADO : Tema.DISTINTIVO, null, 10);
        distintivo.setLayout(new BorderLayout());
        distintivo.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        distintivo.add(etiqueta(paso, resultado ? Tema.MENTA : Tema.TEXTO_DISTINTIVO, 12, Font.BOLD));
        fila.add(distintivo);
        fila.add(Box.createHorizontalStrut(9));
        fila.add(etiqueta(texto, Tema.TINTA, 17, Font.BOLD));
        return fila;
    }

    private static JPanel fila(String nombre, JLabel dato, Color color, boolean linea) {
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, linea ? 1 : 0, 0, Tema.SEPARADOR),
                BorderFactory.createEmptyBorder(7, 0, 7, 0)));
        fila.add(etiqueta(nombre, color, 13, Font.PLAIN), BorderLayout.WEST);
        dato.setHorizontalAlignment(SwingConstants.RIGHT);
        fila.add(dato, BorderLayout.CENTER);
        return fila;
    }

    private static JLabel etiqueta(String texto, Color color, int tamaño, int estilo) {
        JLabel label = new JLabel(texto);
        label.setForeground(color);
        label.setFont(Tema.texto(estilo, tamaño));
        return label;
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private record Opcion(String texto, Solicitud solicitud) {
        @Override public String toString() { return texto; }
    }
}
