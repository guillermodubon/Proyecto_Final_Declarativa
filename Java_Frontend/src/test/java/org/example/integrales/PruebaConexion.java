package org.example.integrales;

import org.example.integrales.core.ClienteProlog;
import org.example.integrales.core.ServicioIntegracion.Error;
import org.example.integrales.core.ServicioIntegracion.Exito;
import org.example.integrales.core.ServicioIntegracion.Solicitud;
import org.example.integrales.ui.Modulo;

import java.nio.file.Path;

public final class PruebaConexion {
    public static void main(String[] args) throws Exception {
        Path backend = Path.of("Prolog_Backend");
        var real = new ClienteProlog(backend.resolve("src/cli.pl"));
        var simulada = new ClienteProlog(backend.resolve("tests/cli_simulada.pl"));

        var ok = simulada.resolver(new Solicitud("x", "1", "3"));
        if (!(ok instanceof Exito exito) || !exito.primitiva().equals("x^2/2") || exito.valor() != 4.0
                || exito.fa() != 0.5 || exito.fb() != 4.5) {
            throw new AssertionError("Respuesta simulada inesperada: " + ok);
        }

        var invalida = real.resolver(new Solicitud("x", "0", "mal"));
        if (!(invalida instanceof Error error) || !error.codigo().equals("LIMITES_INVALIDOS")
                || !error.mensaje().contains("límites")) {
            throw new AssertionError("Error de validación inesperado: " + invalida);
        }

        var noSoportada = real.resolver(new Solicitud("x^x", "1", "3"));
        if (!(noSoportada instanceof Error pendiente) || !pendiente.codigo().equals("NO_SOPORTADA")) {
            throw new AssertionError("Respuesta de caso no soportado inesperada: " + noSoportada);
        }

        for (Modulo modulo : Modulo.values()) {
            var aislado = new ClienteProlog(backend.resolve("tests/cli_modulo.pl"), modulo.id());
            var resultado = aislado.resolver(new Solicitud("x^x", "1", "3"));
            if (!(resultado instanceof Error errorModulo) || !errorModulo.codigo().equals("NO_SOPORTADA")) {
                throw new AssertionError("Respuesta inesperada de " + modulo.id() + ": " + resultado);
            }
        }

        System.out.println("Conexión Java–SWI-Prolog: 7 comprobaciones aprobadas.");
    }
}
