prime(2).
prime(3).
prime(N) :- N > 3, N mod 2 =\= 0, \+ check_prime(N, 3).

check_prime(N, P) :- N mod P =:= 0.
check_prime(N, P) :- P * P < N, P2 is P + 2, check_prime(N, P2).

composite(N) :- \+ prime(N).

prime_divisors(N, Divisors) :-
    prime_divisors(N, 2, [], Divisors).

prime_divisors(1, _, Divisors, Divisors) :-!.
prime_divisors(N, D, A, Divisors) :-
    (   N mod D =:= 0 ->
        append(A, [D], NewA),
        N1 is N // D,
        prime_divisors(N1, D, NewA, Divisors)
    ;   NextD is D + 1,
        prime_divisors(N, NextD, A, Divisors)
    ).

nth_prime(1, 2) :- !.
nth_prime(N, P) :-
    N > 1,
    next_prime(2, [2], N, P).

next_prime(P, _, 1, P) :- !.
next_prime(Cur, A, N, P) :-
    next_candidate(Cur, Next),
    (   is_prime(Next, A)
    ->  append(A, [Next], NewA),
        N1 is N - 1,
        next_prime(Next, NewA, N1, P)
    ;   next_prime(Next, A, N, P)
    ).

next_candidate(2, 3) :- !.
next_candidate(N, Next) :- Next is N + 2.

is_prime(N, []) :- !.
is_prime(N, [P | T]) :-
    P * P > N,
    !.
is_prime(N, [P | T]) :-
    \+ has_factor(N, P),
    is_prime(N, T).

has_factor(N, P) :-
    N mod P =:= 0.




