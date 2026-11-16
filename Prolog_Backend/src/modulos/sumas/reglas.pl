:- encoding(utf8).
:- module(reglas_sumas, [regla/4]).
:- meta_predicate regla(+, +, 2, -).

regla(_, _, _, _) :- fail.

% g) Integral de la suma de dos funciones.
regla(U+V, _, Integrar, FU+FV) :-
    call(Integrar, U, FU),
    call(Integrar, V, FV).

% h) Integral de la resta de dos funciones.
regla(U-V, _, Integrar, FU-FV) :-
    call(Integrar, U, FU),
    call(Integrar, V, FV).

% s) Integral de la tangente hiperbolica.
regla(tanh(x), _, _, log(cosh(x))).