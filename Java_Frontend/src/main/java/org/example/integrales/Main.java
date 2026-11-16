package org.example.integrales;

import org.example.integrales.core.ClienteProlog;
import org.example.integrales.ui.Ventana;

import javax.swing.SwingUtilities;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
    private static final Path RUTA_PROLOG = Path.of("Prolog_Backend", "src", "integrales.pl");

    public static void main(String[] args) throws Exception {
        Path cli = args.length == 0 ? localizarCli() : Path.of(args[0]);
        SwingUtilities.invokeLater(() -> new Ventana(new ClienteProlog(cli)).setVisible(true));
    }

    private static Path localizarCli() throws Exception {
        Path[] origenes = {
                Path.of("").toAbsolutePath(),
                Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
        };
        for (Path origen : origenes) {
            Path carpeta = Files.isDirectory(origen) ? origen : origen.getParent();
            while (carpeta != null) {
                Path cli = carpeta.resolve(RUTA_PROLOG);
                if (Files.isRegularFile(cli)) return cli;
                carpeta = carpeta.getParent();
            }
        }
        return RUTA_PROLOG;
    }
}
