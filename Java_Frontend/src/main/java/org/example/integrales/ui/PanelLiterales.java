package org.example.integrales.ui;

import org.example.integrales.core.ServicioIntegracion.Solicitud;

import java.util.ArrayList;
import java.util.List;

public abstract class PanelLiterales {
    private final List<Ejemplo> ejemplos = new ArrayList<>();

    protected final void ejemplo(String titulo, String expresion, String a, String b) {
        ejemplos.add(new Ejemplo(titulo, new Solicitud(expresion, a, b)));
    }

    final List<Ejemplo> ejemplos() { return List.copyOf(ejemplos); }

    record Ejemplo(String titulo, Solicitud solicitud) {}
}
