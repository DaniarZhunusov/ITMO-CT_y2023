"use strict";

chapter("Types");
section("Variables are typeless");

let a = 1;
example("a");

a = "Hello";
example("a");

section("Values are typed");
let as = ["'Hello'", 1, 1.1, true, false, [1, 2, 3], new Array(1, 2, 3), null, undefined];
for (let i = 0; i < as.length; i++) {
    println("a =", as[i]);
    println("    typeof(a) ->", typeof(as[i]));
}

section("Ordinary comparison");
example("'1' == '1'");
example("'1' == 1");
example("'1.0' == 1");
example("undefined == undefined");
example("undefined == null");
example("null == null");
example("0 == []");
example("'10' == [10]");

section("Strict comparison");
example("'1' === '1'");
example("'1' === 1");
example("undefined === undefined");
example("undefined === null");
example("null === null");
example("0 === []");
example("'10' === [10]");

section("Calculations");
println("Addition");
example("2 + 3");
example("2.1 + 3.1");
example("'2.1' + '3.1'");
example("'Hello, ' + 'world!'");

println("Subtraction");
example("2 - 3");
example("2.1 - 3.1");
example("'2.1' - '3.1'");
example("'Hello, ' - 'world!'");

section("Arrays");
as = [10, 20, 30];
println("as -> [" + as +"]");
example("as[2]");
example("as[3]");
example("as['2']");
example("as[2.0]");
example("as['2.0']");

section("Arrays are mutable");
example("as = new Array(10, 20, 30)");
example("as.push(40)");
example("as");
example("as.pop()");
example("as");
example("as.unshift(50)");
example("as");
example("as.shift()");
example("as");
