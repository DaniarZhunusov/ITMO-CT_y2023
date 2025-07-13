"use strict";
const cnst = (value) => () => value;

let variable = (name) => {
    const index = { x: 0, y: 1, z: 2 }[name];
    return (...args) => args[index];
};

const createBinaryOperation = (operation) => (a, b) => (...args) => operation(a(...args), b(...args));

const add = createBinaryOperation((a, b) => a + b);

const subtract = createBinaryOperation((a, b) => a - b);

const multiply = createBinaryOperation((a, b) => a * b);

const divide = createBinaryOperation((a, b) => a / b);

const pi = cnst(Math.PI);
const e = cnst(Math.E);


const createUnaryOperation = (operation) => (a) => (...args) => operation(a(...args));

const negate = createUnaryOperation((x) => -x);

const square = (a) => multiply(a, a);

const sqrt = createUnaryOperation((x) => Math.sqrt(Math.abs(x)));




