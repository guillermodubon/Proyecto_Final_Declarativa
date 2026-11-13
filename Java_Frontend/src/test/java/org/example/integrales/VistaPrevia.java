package org.example.integrales;

import org.example.integrales.core.ServicioIntegracion.Error;
import org.example.integrales.core.ServicioIntegracion.Exito;
import org.example.integrales.core.ServicioIntegracion;
import org.example.integrales.ui.Ventana;

import javax.swing.SwingUtilities;

public final class VistaPrevia {
    public static void main(String[] args) {
        ServicioIntegracion simulacion = switch (args.length) {
            case 0 -> VistaPrevia::resolverEjemplo;
            case 4 -> {
                double fa = Double.parseDouble(args[2]), fb = Double.parseDouble(args[3]);
                Exito muestra = new Exito(args[1], fb - fa, fa, fb);
                yield solicitud -> solicitud.expresion().equals(args[0]) ? muestra
                        : new Error("SIMULACION", "Modo de prueba: ingresá " + args[0] + ".");
            }
            default -> throw new IllegalArgumentException("Usá: VistaPrevia [expresión primitiva F(a) F(b)]");
        };
        SwingUtilities.invokeLater(() -> {
            Ventana ventana = new Ventana(simulacion);
            ventana.setTitle("Calculadora de integrales (simulación)");
            ventana.setVisible(true);
        });
    }

    private static ServicioIntegracion.Resultado resolverEjemplo(ServicioIntegracion.Solicitud solicitud) {
        boolean potencia = solicitud.expresion().equals("x^2");
        if (!potencia && !solicitud.expresion().equals("x")) {
            return new Error("SIMULACION", "Modo de prueba: ingresá x o x^2 y dos límites numéricos.");
        }
        try {
            double a = Double.parseDouble(solicitud.a());
            double b = Double.parseDouble(solicitud.b());
            if (!Double.isFinite(a) || !Double.isFinite(b) || a > b) {
                return new Error("SIMULACION", "Ingresá límites finitos con a ≤ b.");
            }
            double fa = potencia ? a * a * a / 3 : a * a / 2;
            double fb = potencia ? b * b * b / 3 : b * b / 2;
            return new Exito(potencia ? "x^3/3" : "x^2/2", fb - fa, fa, fb);
        } catch (NumberFormatException e) {
            return new Error("SIMULACION", "Ingresá límites numéricos para la prueba.");
        }
    }
}
