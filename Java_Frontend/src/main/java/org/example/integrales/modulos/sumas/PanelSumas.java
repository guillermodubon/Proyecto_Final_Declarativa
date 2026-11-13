package org.example.integrales.modulos.sumas;

import org.example.integrales.ui.PanelLiterales;

public final class PanelSumas extends PanelLiterales {
    public PanelSumas() {
        ejemplo("g) Suma de funciones", "x+sin(x)", "0", "1");
        ejemplo("h) Resta de funciones", "x^2-cos(x)", "0", "2");
        ejemplo("s) Tangente hiperbólica", "tanh(x)", "0", "1");
    }
}
