:- load_library('alice.tuprolog.lib.DCGLibrary').

op(Sym) --> { atom_chars(Sym, Operation) }, Operation.

op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].
op_p(op_negate) --> op('negate').
op_p(op_square) --> op('square').
op_p(op_sqrt) --> op('sqrt').

lookup(K, [(K, V) | _], V).
lookup(K, [_ | T], V) :- lookup(K, T, V).

nonvar(V, _) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

evaluate(const(Value), _, Value).
evaluate(variable(Var), Vars, Value) :- atom_chars(Var, [V | _]), lookup(V, Vars, Value).
evaluate(operation(Op, Exp1, Exp2), Vars, Result) :-
    evaluate(Exp1, Vars, Val1),
    evaluate(Exp2, Vars, Val2),
    apply_operation(Op, Val1, Val2, Result).
evaluate(operation(Op, Exp1), Vars, Result) :-
    evaluate(Exp1, Vars, Val1),
    apply_operation(Op, Val1, Result).

apply_operation(op_add, A, B, Result) :- Result is A + B.
apply_operation(op_subtract, A, B, Result) :- Result is A - B.
apply_operation(op_multiply, A, B, Result) :- Result is A * B.
apply_operation(op_divide, A, B, Result) :- Result is A / B.
apply_operation(op_negate, A, Result) :- Result is -A.
apply_operation(op_square, A, Result) :- Result is A * A.
apply_operation(op_sqrt, A, Result) :- Result is sqrt(A).

var_p([]) --> [].
var_p([X | T]) --> [X], { member(X, ['x', 'y', 'z']) }, var_p(T).

variable(Var, variable(Var)).
expr_p(variable(Var)) -->
  { nonvar(Var, atom_chars(Var, Chars)) },
  wsp_p, var_p(Chars), wsp_p,
  { Chars = [_ | _], atom_chars(Var, Chars) }.

digits_p([]) --> [].
digits_p([H | T]) -->
  { member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-'])},
  [H],
  digits_p(T).

const(Value, const(Value)).
expr_p(const(Value)) -->
  { nonvar(Value, number_chars(Value, Chars)) },
  wsp_p, digits_p(Chars), wsp_p,
  { Chars = [_ | _], \+ Chars = ['-'], number_chars(Value, Chars) }.

wsp_p --> [].
wsp_p --> [' '], wsp_p.

expr_p(operation(Op, A, B)) --> wsp_p, ['('], wsp_p, op_p(Op), [' '], wsp_p, expr_p(A), [' '], wsp_p, expr_p(B), wsp_p, [')'], wsp_p.
expr_p(operation(Op, A)) --> wsp_p, ['('], wsp_p, op_p(Op), [' '], wsp_p, expr_p(A), wsp_p, [')'], wsp_p.

prefix_str(E, A) :- ground(E), phrase(expr_p(E), C), atom_chars(A, C), !.
prefix_str(E, A) :-   atom(A), atom_chars(A, C), phrase(expr_p(E), C), !.


