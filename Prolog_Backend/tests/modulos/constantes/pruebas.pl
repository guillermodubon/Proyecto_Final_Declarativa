:- encoding(utf8).
:- begin_tests(constantes).

:- use_module('../../../src/modulos/constantes/reglas.pl', [regla/4]).
:- use_module('../../../src/evaluacion.pl', [resolver_con/5]).

sin_regla(_, _) :- fail.

% Integra solo con este módulo.
solo_constantes(Intervalo, Expresion, Primitiva) :-
    regla(Expresion, Intervalo, sin_regla, Primitiva).

test(no_reclama_funciones_ajenas, [fail]) :-
    regla(sin(x), intervalo(0, 1), sin_regla, _).

test(no_reclama_exponente_no_entero, [fail]) :-
    regla(x^0.5, intervalo(0, 1), sin_regla, _).

% a) ∫ k dx
test(a_constante, [true(F == 5*x)]) :-
    regla(5, intervalo(0, 2), sin_regla, F).

test(a_constante_negativa, [true(F == -3*x)]) :-
    regla(-3, intervalo(0, 2), sin_regla, F).

test(a_constante_decimal, [true(F == 2.5*x)]) :-
    regla(2.5, intervalo(0, 2), sin_regla, F).

test(a_uno, [true(F == x)]) :-
    regla(1, intervalo(0, 2), sin_regla, F).

test(a_area) :-
    resolver_con(solo_constantes, "5", "0", "2", ok(_, V, _, _)),
    assertion(V =:= 10).

% b) ∫ x dx
test(b_variable, [true(F == x^2/2)]) :-
    regla(x, intervalo(0, 4), sin_regla, F).

test(b_area) :-
    resolver_con(solo_constantes, "x", "0", "4", ok(_, V, Fa, Fb)),
    assertion(V =:= 8), assertion(Fa =:= 0), assertion(Fb =:= 8).

% d) ∫ x^n dx
test(d_cuadrado, [true(F == x^3/3)]) :-
    regla(x^2, intervalo(0, 3), sin_regla, F).

test(d_exponente_cero, [true(F == x)]) :-
    regla(x^0, intervalo(0, 3), sin_regla, F).

test(d_area_cubo) :-
    resolver_con(solo_constantes, "x^3", "0", "2", ok(F, V, _, _)),
    assertion(F == x^4/4), assertion(V =:= 4).

test(d_menos_uno, [true(F == log(abs(x)))]) :-
    regla(x^(-1), intervalo(1, 2), sin_regla, F).

test(d_menos_uno_area) :-
    resolver_con(solo_constantes, "x^(-1)", "1", "2", ok(_, V, _, _)),
    assertion(abs(V - log(2)) < 1.0e-12).

test(d_menos_dos, [true(F == -1/x)]) :-
    regla(x^(-2), intervalo(1, 2), sin_regla, F).

test(d_menos_tres_area) :-
    resolver_con(solo_constantes, "x^(-3)", "1", "2", ok(F, V, _, _)),
    assertion(F == -1/(2*x^2)), assertion(abs(V - 0.375) < 1.0e-12).

test(d_negativo_en_intervalo_negativo) :-
    resolver_con(solo_constantes, "x^(-2)", "-2", "-1", ok(_, V, _, _)),
    assertion(abs(V - 0.5) < 1.0e-12).

test(d_negativo_que_cruza_cero) :-
    resolver_con(solo_constantes, "x^(-2)", "-1", "1", error('DOMINIO', _)).

test(d_negativo_desde_cero) :-
    resolver_con(solo_constantes, "x^(-1)", "0", "1", error('DOMINIO', _)).

:- end_tests(constantes).
