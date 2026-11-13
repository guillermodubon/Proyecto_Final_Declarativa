package org.example.integrales.modulos.factores;

import org.example.integrales.ui.PanelLiterales;

public final class PanelFactores extends PanelLiterales {
    public PanelFactores() {
        ejemplo("c) Constante por x", "3*x", "0", "2");
        ejemplo("f) Constante por función", "2*cos(x)", "0", "1");
        ejemplo("i) Exponencial", "2^x", "0", "2");
    }
}
