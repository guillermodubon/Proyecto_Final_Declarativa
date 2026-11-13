package org.example.integrales.ui;

import org.example.integrales.modulos.constantes.PanelConstantes;
import org.example.integrales.modulos.factores.PanelFactores;
import org.example.integrales.modulos.sumas.PanelSumas;
import org.example.integrales.modulos.trigonometricas.PanelTrigonometricas;

import java.util.function.Supplier;

public enum Modulo {
    CONSTANTES("constantes", "Constantes y potencias", PanelConstantes::new),
    FACTORES("factores", "Factores y exponenciales", PanelFactores::new),
    SUMAS("sumas", "Sumas, restas y tanh", PanelSumas::new),
    TRIGONOMETRICAS("trigonometricas", "Trigonométricas e hiperbólicas", PanelTrigonometricas::new);

    private final String id;
    private final String titulo;
    private final Supplier<PanelLiterales> panel;

    Modulo(String id, String titulo, Supplier<PanelLiterales> panel) {
        this.id = id;
        this.titulo = titulo;
        this.panel = panel;
    }

    public String id() { return id; }
    public String titulo() { return titulo; }
    PanelLiterales crearPanel() { return panel.get(); }

    public static Modulo buscar(String id) {
        for (Modulo modulo : values()) {
            if (modulo.id.equals(id)) return modulo;
        }
        throw new IllegalArgumentException("Módulo desconocido: " + id);
    }
}
