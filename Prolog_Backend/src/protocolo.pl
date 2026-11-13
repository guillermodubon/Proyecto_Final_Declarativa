:- encoding(utf8).
:- module(protocolo_integracion, [ejecutar/1]).

:- use_module(library(readutil)).
:- meta_predicate ejecutar(4).

ejecutar(Resolver) :-
    set_stream(user_input, encoding(utf8)),
    set_stream(user_output, encoding(utf8)),
    read_line_to_string(user_input, Expresion),
    read_line_to_string(user_input, A),
    read_line_to_string(user_input, B),
    ( maplist(string, [Expresion, A, B]) ->
        catch(call(Resolver, Expresion, A, B, Respuesta), Error, respuesta_interna(Error, Respuesta))
    ; Respuesta = error('ENTRADA_INVALIDA', "Se esperaban la expresión y ambos límites.")
    ),
    escribir(Respuesta).

respuesta_interna(Error, error('ERROR_INTERNO', "No se pudo completar el cálculo en SWI-Prolog.")) :-
    print_message(error, Error).

escribir(ok(Primitiva, Valor, Fa, Fb)) :-
    term_string(Primitiva, Texto, [quoted(true)]),
    format('OK~n~s~n~w~n~w~n~w~n', [Texto, Valor, Fa, Fb]).
escribir(error(Codigo, Mensaje)) :- format('ERROR~n~w~n~s~n', [Codigo, Mensaje]).
