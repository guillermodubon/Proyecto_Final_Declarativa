package org.example.integrales;

import org.example.integrales.core.ClienteProlog;
import org.example.integrales.ui.Modulo;
import org.example.integrales.ui.Ventana;

import javax.swing.SwingUtilities;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DesarrolloModulo {
    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("Indicá el nombre de tu módulo.");
        Modulo modulo = Modulo.buscar(args[0]);
        Path ruta = Path.of("Prolog_Backend", "tests", "cli_modulo.pl");
        if (!Files.isRegularFile(ruta)) ruta = Path.of("..").resolve(ruta);
        var servicio = new ClienteProlog(ruta, modulo.id());
        SwingUtilities.invokeLater(() -> new Ventana(servicio, modulo).setVisible(true));
    }
}
