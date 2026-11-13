package org.example.integrales.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public final class ClienteProlog implements ServicioIntegracion {
    private final Path cli;
    private final List<String> argumentos;

    public ClienteProlog(Path cli, String... argumentos) {
        this.cli = cli.toAbsolutePath().normalize();
        this.argumentos = List.of(argumentos);
    }

    @Override
    public Resultado resolver(Solicitud solicitud) throws Exception {
        List<String> campos = List.of(solicitud.expresion(), solicitud.a(), solicitud.b());
        if (campos.stream().anyMatch(s -> s.isBlank() || s.length() > 512
                || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0)) {
            return new Error("ENTRADA_INVALIDA", "Ingresá la expresión y ambos límites (máximo 512 caracteres por campo).");
        }
        if (!Files.isRegularFile(cli)) throw new IOException("No se encontró el archivo de inicio de Prolog.");
        List<String> comando = new ArrayList<>(List.of(System.getenv().getOrDefault("SWIPL", "swipl"),
                "-q", "-f", "none", "-s", cli.toString(), "-g", "integracion_cli:main", "-t", "halt"));
        if (!argumentos.isEmpty()) {
            comando.add("--");
            comando.addAll(argumentos);
        }
        Process proceso = new ProcessBuilder(comando)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        try {
            var lectura = CompletableFuture.supplyAsync(() -> leerSalida(proceso));
            try (var entrada = proceso.outputWriter(StandardCharsets.UTF_8)) {
                entrada.write(String.join("\n", campos));
                entrada.newLine();
            }
            if (!proceso.waitFor(10, TimeUnit.SECONDS)) throw new IOException("SWI-Prolog tardó demasiado en responder.");
            List<String> salida = lectura.get(1, TimeUnit.SECONDS);
            if (proceso.exitValue() != 0 || salida.isEmpty()) {
                throw new IOException("Respuesta inválida de SWI-Prolog.");
            }
            if (salida.get(0).equals("OK") && salida.size() == 5) {
                return new Exito(salida.get(1), Double.parseDouble(salida.get(2)),
                        Double.parseDouble(salida.get(3)), Double.parseDouble(salida.get(4)));
            }
            if (salida.get(0).equals("ERROR") && salida.size() == 3) {
                return new Error(salida.get(1), salida.get(2));
            }
            throw new IOException("Respuesta desconocida de SWI-Prolog.");
        } finally {
            if (proceso.isAlive()) proceso.destroyForcibly();
        }
    }

    private static List<String> leerSalida(Process proceso) {
        try (var salida = proceso.inputReader(StandardCharsets.UTF_8)) {
            return salida.lines().toList();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }
}
