package org.example.integrales.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

final class Tema {
    static final Color FONDO = new Color(0x101a22);
    static final Color SUPERFICIE = new Color(0x182733);
    static final Color TINTA = new Color(0xf2f7fb);
    static final Color SUAVE = new Color(0xadbfcc);
    static final Color PETROLEO = new Color(0x102d3e);
    static final Color PROFUNDO = new Color(0x0a2332);
    static final Color CLARO = new Color(0xf3f9fc);
    static final Color CLARO_SUAVE = new Color(0xc2d7e2);
    static final Color SEPARADOR = new Color(0x3c5868);
    static final Color DISTINTIVO = new Color(0x26454d);
    static final Color DISTINTIVO_RESULTADO = new Color(0x244557);
    static final Color TEXTO_DISTINTIVO = new Color(0x99e8dd);
    static final Color MENTA = new Color(0x86e3d4);
    static final Color ERROR = new Color(0xffb6c4);
    private static final Color BOTON = new Color(0x89e3d4);
    private static final Color BOTON_ACTIVO = new Color(0xa0ebde);
    private static final Color BOTON_FOCO = new Color(0xc2f0e7);
    private static final Color TEXTO_BOTON = new Color(0x0e3435);
    private static final int CURVA_CONTROL = 12;
    private static final Font ROBOTO = cargarRoboto();

    private Tema() {}

    static Font texto(int estilo, int tamaño) { return ROBOTO.deriveFont(estilo, (float) tamaño); }

    static Font formula(int tamaño) {
        Font cambria = new Font("Cambria Math", Font.PLAIN, tamaño);
        return cambria.getFamily().equalsIgnoreCase("Cambria Math") ? cambria : new Font(Font.SERIF, Font.PLAIN, tamaño);
    }

    static JPanel superficie(Color relleno) {
        return superficie(relleno, 20);
    }

    static JPanel superficie(Color relleno, int curva) {
        return new JPanel() {
            {
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D dibujo = trazado(g);
                dibujo.setColor(relleno);
                dibujo.fillRoundRect(0, 0, getWidth(), getHeight(), curva, curva);
                dibujo.dispose();
            }
        };
    }

    static void control(JComponent componente, Color fondo, int margenVertical, int margenHorizontal) {
        componente.setOpaque(false);
        componente.setBackground(fondo);
        componente.setForeground(TINTA);
        componente.setBorder(BorderFactory.createEmptyBorder(
                margenVertical, margenHorizontal, margenVertical, margenHorizontal));
        componente.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { componente.repaint(); }
            @Override public void focusLost(FocusEvent e) { componente.repaint(); }
        });
    }

    static void pintarControl(Graphics g, JComponent componente) {
        Graphics2D dibujo = trazado(g);
        dibujo.setColor(componente.hasFocus() ? DISTINTIVO : componente.getBackground());
        dibujo.fillRoundRect(0, 0, componente.getWidth(), componente.getHeight(), CURVA_CONTROL, CURVA_CONTROL);
        dibujo.dispose();
    }

    static void selector(JComboBox<?> lista) {
        lista.setUI(new BasicComboBoxUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                pintarControl(g, c);
                super.paint(g, c);
            }

            @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean focus) {}

            @Override
            protected JButton createArrowButton() {
                JButton flecha = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D dibujo = trazado(g);
                        dibujo.setColor(isEnabled() ? TINTA : SUAVE);
                        dibujo.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int x = getWidth() / 2, y = getHeight() / 2;
                        dibujo.drawPolyline(new int[]{x - 4, x, x + 4}, new int[]{y - 2, y + 2, y - 2}, 3);
                        dibujo.dispose();
                    }
                };
                flecha.setBorderPainted(false);
                flecha.setContentAreaFilled(false);
                flecha.setFocusable(false);
                return flecha;
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup menu = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane desplazamiento = super.createScroller();
                        desplazamiento.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                            @Override protected JButton createDecreaseButton(int orientacion) { return sinFlecha(); }
                            @Override protected JButton createIncreaseButton(int orientacion) { return sinFlecha(); }

                            @Override
                            protected void paintTrack(Graphics g, JComponent c, Rectangle zona) {
                                g.setColor(PROFUNDO);
                                g.fillRect(zona.x, zona.y, zona.width, zona.height);
                            }

                            @Override
                            protected void paintThumb(Graphics g, JComponent c, Rectangle zona) {
                                g.setColor(SEPARADOR);
                                g.fillRect(zona.x, zona.y, zona.width, zona.height);
                            }
                        });
                        return desplazamiento;
                    }
                };
                menu.setBorder(BorderFactory.createEmptyBorder());
                menu.getList().setBackground(SUPERFICIE);
                menu.getList().setSelectionForeground(TINTA);
                return menu;
            }
        });
        control(lista, SUPERFICIE, 1, 1);
    }

    private static JButton sinFlecha() {
        JButton boton = new JButton();
        boton.setPreferredSize(new Dimension(0, 0));
        return boton;
    }

    static JButton botonResolver() {
        JButton boton = new JButton("Resolver integral") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D dibujo = trazado(g);
                dibujo.setColor(!isEnabled() ? DISTINTIVO
                        : hasFocus() ? BOTON_FOCO : getModel().isRollover() ? BOTON_ACTIVO : BOTON);
                dibujo.fillRoundRect(0, 0, getWidth(), getHeight(), CURVA_CONTROL, CURVA_CONTROL);
                dibujo.setColor(isEnabled() ? TEXTO_BOTON : SUAVE);
                int x = getWidth() - 14, y = getHeight() / 2;
                dibujo.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                dibujo.drawLine(x - 12, y, x, y);
                dibujo.drawPolyline(new int[]{x - 4, x, x - 4}, new int[]{y - 4, y, y + 4}, 3);
                dibujo.dispose();
                super.paintComponent(g);
            }
        };
        boton.setForeground(TEXTO_BOTON);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setRolloverEnabled(true);
        return boton;
    }

    private static Graphics2D trazado(Graphics g) {
        Graphics2D dibujo = (Graphics2D) g.create();
        dibujo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return dibujo;
    }

    private static Font cargarRoboto() {
        try (var fuente = Tema.class.getResourceAsStream("/fonts/Roboto.ttf")) {
            if (fuente != null) return Font.createFont(Font.TRUETYPE_FONT, fuente);
        } catch (IOException | FontFormatException ignored) {}
        return new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    }
}
