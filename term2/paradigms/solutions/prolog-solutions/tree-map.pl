map_build([], nil).
map_build([(Key, Value) | Tail], TreeMap) :-
    map_build(Tail, TempTree),
    map_put(TempTree, Key, Value, TreeMap).

map_put(nil, Key, Value, tree(Key, Value, nil, nil)).
map_put(tree(K, V, Left, Right), NewKey, NewValue, ResultTree) :-
    NewKey < K,
    map_put(Left, NewKey, NewValue, NewLeft),
    ResultTree = tree(K, V, NewLeft, Right).
map_put(tree(K, V, Left, Right), NewKey, NewValue, ResultTree) :-
    NewKey > K,
    map_put(Right, NewKey, NewValue, NewRight),
    ResultTree = tree(K, V, Left, NewRight).

map_get(tree(Key, Value, _, _), Key, Value).
map_get(tree(K, V, Left, _), SearchKey, Value) :-
    SearchKey < K,
    map_get(Left, SearchKey, Value).
map_get(tree(K, V, _, Right), SearchKey, Value) :-
    SearchKey > K,
    map_get(Right, SearchKey, Value).

map_lastKey(tree(Key, _, _, nil), Key).
map_lastKey(tree(_, _, _, Right), Key):-
    map_lastKey(Right, Key).

map_lastValue(tree(Key, Value, _, nil), Value).
map_lastValue(tree(_, _, _, Right), Value):-
    map_lastValue(Right, Value).

map_lastEntry(tree(Key, Value, _, nil), (Key, Value)).
map_lastEntry(tree(_, _, _, Right), Entry):-
    map_lastEntry(Right, Entry).




