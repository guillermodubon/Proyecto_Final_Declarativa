:- encoding(utf8).
:- module(reglas_factores, [regla/4]).
:- meta_predicate regla(+, +, 2, -).

% c) Integral de una constante por la variable: ∫ k*x dx = k*x^2/2
regla(K*x, _, _, Primitiva) :-
    number(K), !,
    constante_por_x(K, Primitiva).
regla(x*K, _, _, Primitiva) :-
    number(K), !,
    constante_por_x(K, Primitiva).

% f) Integral de una constante por una función: ∫ k*u dx = k*∫u dx
regla(K*U, _Intervalo, Integrar, Primitiva) :-
    number(K), contiene_x(U), !,
    constante_por_funcion(K, U, Integrar, Primitiva).

% i) Integral de una exponencial de base constante: ∫ a^x dx = a^x/log(a)
% Válida para a > 0; si a = 1 la primitiva es x. Para a <= 0 la
% exponencial no está definida en los reales y la regla no aplica.
regla(A^x, _, _, Primitiva) :-
    number(A), A > 0, !,
    exponencial(A, Primitiva).

% Auxiliares para dejar la primitiva simplificada
constante_por_x(0, 0) :- !.
constante_por_x(1, x^2/2) :- !.
constante_por_x(K, K*x^2/2).

constante_por_funcion(0, _, _, 0) :- !.
constante_por_funcion(1, U, Integrar, Primitiva) :- !,
    call(Integrar, U, Primitiva).
constante_por_funcion(K, U, Integrar, K*Primitiva) :-
    call(Integrar, U, Primitiva).

exponencial(1, x) :- !.
exponencial(A, A^x/log(A)).

% u debe ser una función de x para no reclamar productos de constantes.
contiene_x(x) :- !.
contiene_x(Termino) :-
    compound(Termino),
    compound_name_arguments(Termino, _, Argumentos),
    member(Argumento, Argumentos),
    contiene_x(Argumento).
