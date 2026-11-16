:- encoding(utf8).
:- begin_tests(factores).

:- use_module('../../../src/modulos/factores/reglas.pl', [regla/4]).
:- use_module('../../../src/evaluacion.pl', [resolver_con/5]).

sin_regla(_, _) :- fail.

integracion_simulada(x, x^2/2).
integracion_simulada(x^2, x^3/3).
integracion_simulada(cos(x), sin(x)).

% Integra solo con este módulo.
solo_factores(Intervalo, Expresion, Primitiva) :-
    regla(Expresion, Intervalo, sin_regla, Primitiva).

% Permite resolver u con una tabla de primitivas simulada.
factores_simulados(Intervalo, Expresion, Primitiva) :-
    regla(Expresion, Intervalo, integracion_simulada, Primitiva).

test(no_reclama_funciones_ajenas, [fail]) :-
    regla(sin(x), intervalo(0, 1), sin_regla, _).

% c) ∫ k*x dx
test(c_constante_por_x, [true(F == 3*x^2/2)]) :-
    regla(3*x, intervalo(0, 2), sin_regla, F).

test(c_conmuta, [true(F == 5*x^2/2)]) :-
    regla(x*5, intervalo(0, 2), sin_regla, F).

test(c_uno, [true(F == x^2/2)]) :-
    regla(1*x, intervalo(0, 2), sin_regla, F).

test(c_cero, [true(F == 0)]) :-
    regla(0*x, intervalo(0, 2), sin_regla, F).

test(c_area) :-
    resolver_con(solo_factores, "3*x", "0", "2", ok(F, V, Fa, Fb)),
    assertion(F == 3*x^2/2),
    assertion(V =:= 6), assertion(Fa =:= 0), assertion(Fb =:= 6).

% f) ∫ k*u dx
test(f_constante_por_funcion, [true(F == 2*sin(x))]) :-
    regla(2*cos(x), intervalo(0, 1), integracion_simulada, F).

test(f_uno, [true(F == sin(x))]) :-
    regla(1*cos(x), intervalo(0, 1), integracion_simulada, F).

test(f_cero, [true(F == 0)]) :-
    regla(0*cos(x), intervalo(0, 1), sin_regla, F).

test(f_area_coseno) :-
    resolver_con(factores_simulados, "2*cos(x)", "0", "1", ok(F, V, Fa, Fb)),
    assertion(F == 2*sin(x)),
    assertion(abs(V - 2*sin(1)) < 1.0e-12),
    assertion(abs(Fa) < 1.0e-12),
    assertion(abs(Fb - 2*sin(1)) < 1.0e-12).

test(f_area_polinomio) :-
    resolver_con(factores_simulados, "2*x^2", "0", "3", ok(F, V, _, _)),
    assertion(F == 2*(x^3/3)),
    assertion(abs(V - 18) < 1.0e-12).

% i) ∫ a^x dx
test(i_exponencial, [true(F == 2^x/log(2))]) :-
    regla(2^x, intervalo(0, 3), sin_regla, F).

test(i_base_uno, [true(F == x)]) :-
    regla(1^x, intervalo(0, 2), sin_regla, F).

test(i_base_decimal, [true(F == 0.5^x/log(0.5))]) :-
    regla(0.5^x, intervalo(0, 2), sin_regla, F).

test(i_base_cero, [fail]) :-
    regla(0^x, intervalo(0, 1), sin_regla, _).

test(i_base_negativa, [fail]) :-
    regla((-2)^x, intervalo(0, 1), sin_regla, _).

test(i_area) :-
    resolver_con(solo_factores, "2^x", "0", "3", ok(F, V, _, _)),
    assertion(F == 2^x/log(2)),
    assertion(abs(V - 7/log(2)) < 1.0e-12).

:- end_tests(factores).
