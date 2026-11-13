package org.example.integrales.core;

@FunctionalInterface
public interface ServicioIntegracion {
    Resultado resolver(Solicitud solicitud) throws Exception;

    record Solicitud(String expresion, String a, String b) {}

    sealed interface Resultado permits Exito, Error {}

    record Exito(String primitiva, double valor, double fa, double fb) implements Resultado {}

    record Error(String codigo, String mensaje) implements Resultado {}
}
