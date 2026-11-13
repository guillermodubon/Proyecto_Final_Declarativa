:- encoding(utf8).
:- module(entrada_integracion, [leer_solicitud/6, error_integracion/2]).

leer_solicitud(Texto, Inferior, Superior, Expresion, A, B) :-
    ( leer_termino(Texto, Expresion), expresion_permitida(Expresion) -> true
    ; error_integracion('EXPRESION_INVALIDA', "Usá x, números, operadores y funciones admitidas.")
    ),
    ( limite(Inferior, A), limite(Superior, B) -> true
    ; error_integracion('LIMITES_INVALIDOS', "Los límites deben ser números finitos.")
    ),
    ( A =< B -> true
    ; error_integracion('INTERVALO_INVALIDO', "El límite inferior debe ser menor o igual al superior.")
    ).

leer_termino(Texto, Termino) :-
    string(Texto), string_length(Texto, N), between(1, 512, N),
    string_chars(Texto, Chars), maplist(caracter, Chars),
    string_concat(Texto, "\n.", Fuente),
    catch(setup_call_cleanup(open_string(Fuente, Stream),
        (read_term(Stream, Termino, []), read_term(Stream, Fin, []), Fin == end_of_file),
        close(Stream)), error(syntax_error(_), _), fail),
    ground(Termino).

caracter(C) :- (char_type(C, alnum) ; char_type(C, space) ; memberchk(C, ['.', '+', '-', '*', '/', '^', '(', ')'])), !.

expresion_permitida(N) :- number(N), !, finito(N).
expresion_permitida(x) :- !.
expresion_permitida(T) :-
    compound(T), compound_name_arguments(T, Op, Args),
    operador(Op, Args), maplist(expresion_permitida, Args).

operador(Op, [_, _]) :- memberchk(Op, [+, -, *, /, ^]).
operador(Op, [_]) :- memberchk(Op, [+, -, sin, cos, cosh, tanh]).

limite(Texto, N) :-
    string(Texto), normalize_space(string(Limpio), Texto),
    number_string(N, Limpio), finito(N).

finito(N) :- ( float(N) -> float_class(N, Clase), memberchk(Clase, [zero, subnormal, normal]) ; true ).

error_integracion(Codigo, Mensaje) :- throw(error(integracion(Codigo, Mensaje), _)).
