package org.example.integrales.ui;

import org.example.integrales.core.ServicioIntegracion.Solicitud;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

final class Notacion {
    private static final Pattern POTENCIA = Pattern.compile("\\^(?:\\(([+-]?\\d+)\\)|([+-]?\\d+)(?![\\d.^(]))");
    private static final String NORMAL = "0123456789+-";
    private static final String EXPONENTE = "⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻";
    private final JPanel panel = Tema.superficie(Tema.PROFUNDO, null);
    private final JLabel inferior = etiqueta(15);
    private final JLabel superior = etiqueta(15);
    private final JLabel expresion = etiqueta(29);

    Notacion() {
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        panel.setPreferredSize(new Dimension(380, 116));
        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 2;
        panel.add(etiquetaIntegral(), c);
        c.gridheight = 1;
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        panel.add(superior, c);
        c.gridy = 1;
        panel.add(inferior, c);
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.insets = new Insets(0, 8, 0, 0);
        panel.add(expresion, c);
    }

    JPanel panel() { return panel; }

    void actualizar(Solicitud solicitud) {
        String formula = solicitud.expresion().isBlank() ? "f(x)" : formatear(solicitud.expresion());
        inferior.setText(solicitud.a().isBlank() ? "a" : acortar(solicitud.a(), 8));
        superior.setText(solicitud.b().isBlank() ? "b" : acortar(solicitud.b(), 8));
        expresion.setText(acortar(formula, 20) + " dx");
        String descripcion = "Integral de " + formula + " entre " + inferior.getText() + " y " + superior.getText();
        panel.getAccessibleContext().setAccessibleName(descripcion);
        panel.setToolTipText(descripcion);
    }

    static String formatear(String expresion) {
        return POTENCIA.matcher(expresion).replaceAll(potencia -> {
            String numero = potencia.group(1) != null ? potencia.group(1) : potencia.group(2);
            StringBuilder alto = new StringBuilder();
            for (int i = 0; i < numero.length(); i++) alto.append(EXPONENTE.charAt(NORMAL.indexOf(numero.charAt(i))));
            return alto.toString();
        }).replace('*', '·').replace('-', '−');
    }

    static String numero(double valor) {
        if (valor == 0) return "0";
        double magnitud = Math.abs(valor);
        var formato = new DecimalFormat(magnitud >= 1e7 || magnitud < 1e-6 ? "0.######E0" : "0.######",
                DecimalFormatSymbols.getInstance(Locale.ROOT));
        return formato.format(valor).replace('-', '−');
    }

    private static JLabel etiquetaIntegral() {
        JLabel integral = etiqueta(54);
        integral.setText("∫");
        return integral;
    }

    private static JLabel etiqueta(int tamaño) {
        JLabel etiqueta = new JLabel("", SwingConstants.LEFT);
        etiqueta.putClientProperty("html.disable", true);
        etiqueta.setFont(Tema.formula(tamaño));
        etiqueta.setForeground(Tema.CLARO);
        return etiqueta;
    }

    private static String acortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 1) + "…";
    }
}
