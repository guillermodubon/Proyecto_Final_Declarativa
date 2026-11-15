:- encoding(utf8).
:- module(reglas_constantes, [regla/4]).
:- meta_predicate regla(+, +, 2, -).

:- use_module('../../entrada.pl', [error_integracion/2]).

% a) Integral de una constante numérica: ∫ k dx = k*x
regla(K, _, _, Primitiva) :-
    number(K), !,
    constante_por_x(K, Primitiva).

% b) Integral de la variable: ∫ x dx = x^2/2
regla(x, _, _, x^2/2) :- !.


% d) Integral de una potencia entera: ∫ x^n dx
regla(x^N, Intervalo, _, log(abs(x))) :-
    integer(N), N =:= -1, !,
    sin_cero(Intervalo).

% n < -1  ->  x^(n+1)/(n+1) = -1/(m*x^m), con m = -(n+1) > 0
regla(x^N, Intervalo, _, Primitiva) :-
    integer(N), N < -1, !,
    sin_cero(Intervalo),
    M is -(N + 1),
    potencia_negativa(M, Primitiva).

% n >= 0  ->  x^(n+1)/(n+1)
regla(x^N, _, _, Primitiva) :-
    integer(N), N >= 0, !,
    M is N + 1,
    potencia_positiva(M, Primitiva).

% Auxiliares para dejar la primitiva simplificada
constante_por_x(0, 0) :- !.
constante_por_x(1, x) :- !.
constante_por_x(K, K*x).

potencia_positiva(1, x) :- !.
potencia_positiva(M, x^M/M).

potencia_negativa(1, -1/x) :- !.
potencia_negativa(M, -1/(M*x^M)).

% Con exponente negativo, f(x) no está definida en x = 0: si el
% intervalo [a, b] contiene al 0 la integral impropia diverge, aunque
% la primitiva sí se pueda evaluar en los extremos.
sin_cero(intervalo(A, B)) :-
    (   A =< 0, 0 =< B
    ->  error_integracion('DOMINIO',
            "Con exponente negativo el intervalo no puede contener a x = 0 (la integral diverge).")
    ;   true
    ).
