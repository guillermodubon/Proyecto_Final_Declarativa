:- begin_tests(motor_integracion).

:- use_module('../src/motor.pl').

integracion_simulada(_, x, x^2/2).
integracion_simulada(_, 1/x, log(x)).

test(evalua_primitiva_simulada) :-
    resolver_con(integracion_simulada, "x", "1", "3", ok(F, Valor, Fa, Fb)),
    assertion(F == x^2/2),
    assertion(Valor =:= 4),
    assertion(Fa =:= 0.5),
    assertion(Fb =:= 4.5).

test(conserva_signo) :-
    resolver_con(integracion_simulada, "x", "-3", "-1", ok(_, Valor, _, _)),
    assertion(Valor =:= -4).

test(rechaza_dominio_en_extremo) :-
    resolver_con(integracion_simulada, "1/x", "-1", "2", error('DOMINIO', _)).

test(rechaza_intervalo_invertido) :-
    resolver("x", "2", "1", error('INTERVALO_INVALIDO', _)).

test(rechaza_limite_no_numerico) :-
    resolver("x", "a", "1", error('LIMITES_INVALIDOS', _)).

test(rechaza_entrada_con_termino_adicional) :-
    resolver("x. cos(x).", "0", "1", error('EXPRESION_INVALIDA', _)).

test(rechaza_variable_libre) :-
    resolver("X", "0", "1", error('EXPRESION_INVALIDA', _)).

test(rechaza_expresion_fuera_del_lenguaje) :-
    resolver("consulta(x)", "0", "1", error('EXPRESION_INVALIDA', _)).

test(informa_caso_no_soportado) :-
    resolver("x^x", "1", "2", error('NO_SOPORTADA', _)).

:- end_tests(motor_integracion).
