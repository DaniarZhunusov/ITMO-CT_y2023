"use strict";

function Variable(name) {
    this.name = name;
}

Variable.prototype.evaluate = function (x, y, z) {
    const variables = { 'x': x, 'y': y, 'z': z };
    return variables[this.name];
};

Variable.prototype.toString = function () {
    return this.name;
};

function Const(value) {
    this.value = value;
}

Const.prototype.evaluate = function () {
    return this.value;
};

Const.prototype.toString = function () {
    return this.value.toString();
};

// :NOTE: copy-paste
function BinaryOperation(func, operation) {
    function BinaryOp(left, right) {
        this.left = left;
        this.right = right;
    }

    BinaryOp.prototype.prefix = function () {
        return "(" + operation + ' ' + this.left.prefix() + ' ' + this.right.prefix() + ")";
    }

    BinaryOp.prototype.evaluate = function (x, y, z) {
        return func(this.left.evaluate(x, y, z), this.right.evaluate(x, y, z));
    };

    BinaryOp.prototype.toString = function () {
        return this.left.toString() + ' ' + this.right.toString() + ' ' + operation;
    };

    return BinaryOp;
}

function UnaryOperation(func, operation) {
    function UnaryOp(expr) {
        this.expr = expr;
    }

    UnaryOp.prototype.prefix = function () {
        return "(" + operation + ' ' + this.expr.prefix() + ")";
    };

    UnaryOp.prototype.evaluate = function (x, y, z) {
        return func(this.expr.evaluate(x, y, z));
    };

    UnaryOp.prototype.toString = function () {
        return this.expr.toString() + ' ' + operation;
    };

    return UnaryOp;
}

const Add = BinaryOperation((a, b) => (a + b), "+");
const Subtract = BinaryOperation((a, b) => (a - b), "-");
const Multiply = BinaryOperation((a, b) => (a * b), "*");
const Divide = BinaryOperation((a, b) => (a / b), "/");
const Negate = UnaryOperation((a) => (-a), "negate");
const Sin = UnaryOperation((a) => Math.sin(a), "sin");
const Cos = UnaryOperation((a) => Math.cos(a), "cos");

function Mean(...args) {
    this.args = args;
}

Mean.prototype.evaluate = function(x, y, z) {
    let sum = 0;
    for (let i = 0; i < this.args.length; i++) {
        sum += this.args[i].evaluate(x, y, z);
    }
    return sum / this.args.length;
};

Mean.prototype.prefix = function() {
    return "(mean " + this.args.map(function(arg) { return arg.prefix(); }).join(" ") + ")";

};

function Var(...args) {
    this.args = args;
}

Var.prototype.evaluate = function(x, y, z) {
    const mean = this.args.reduce((acc, curr) => acc + curr.evaluate(x, y, z), 0) / this.args.length;
    const variance = this.args.reduce((acc, curr) => acc + Math.pow(curr.evaluate(x, y, z) - mean, 2), 0) / this.args.length;
    return variance;
};

Var.prototype.prefix = function() {
    return "(var " + this.args.map(function(arg) { return arg.prefix(); }).join(" ") + ")";
};

function reverse_pop(stack, n) {
    return Array.from({ length: n }, () => stack.pop()).reverse();
}

function getOperations() {
    return {
        '+': Add,
        '-': Subtract,
        '/': Divide,
        '*': Multiply,
        'sin': Sin,
        'cos': Cos,
        'negate': Negate
    };
}

function parse(input) {
    const split = input.trim().split(/\s+/);
    const stack = [];
    const operations = getOperations();

    for (let i = 0; i < split.length; i++) {
        const token = split[i];
        if (token in operations) {
            let operation = operations[token];
            let operands = reverse_pop(stack, operation.length);
            stack.push(new operation(...operands));
        } else {
            let constant = parseInt(token);
            if (!isNaN(constant)) {
                stack.push(new Const(constant));
            } else {
                stack.push(new Variable(token));
            }
        }
    }
    return stack.pop();
}

Const.prototype.prefix = function () {
    return this.value.toString();
};

Variable.prototype.prefix = function () {
    return this.name;
};

function parsePrefix(string) {
    let str = string.trim();
    if (str.length === 0) throw Error("Empty input.");
    if (str.startsWith("(") && str.endsWith(")")) {
        let { operation, newIndex } = extractOperation(str, 1);
        let argv = extractArguments(str, newIndex);
        return parseOperation(operation, argv);
    } else {
        return parseOperand(str);
    }
}

function parseOperation(op, argv) {
    // :NOTE: copy-paste
    switch (op) {
        case "+":
            checkBinaryArguments(argv);
            return new Add(...argv.map(parsePrefix));
        case "-":
            checkBinaryArguments(argv);
            return new Subtract(...argv.map(parsePrefix));
        case "/":
            checkBinaryArguments(argv);
            return new Divide(...argv.map(parsePrefix));
        case "*":
            checkBinaryArguments(argv);
            return new Multiply(...argv.map(parsePrefix));
        case "negate":
            checkUnaryArguments(argv);
            return new Negate(...argv.map(parsePrefix));
        case "mean":
            checkMeanVarArguments(argv);
            return new Mean(...argv.map(parsePrefix));
        case "var":
            checkMeanVarArguments(argv);
            return new Var(...argv.map(parsePrefix));
        default:
            throw Error("Incorrect operation.");
    }
}

function checkMeanVarArguments(argv) {
    if (argv.length < 1) throw Error("At least one argument required.");
}

function checkBinaryArguments(argv) {
    if (argv.length !== 2) throw Error("Incorrect number of arguments for binary operation.");
}

function checkUnaryArguments(argv) {
    if (argv.length !== 1) throw Error("Incorrect number of arguments for unary operation.");
}

function parseOperand(operand) {
    if (["x", "y", "z"].includes(operand))
        return new Variable(operand);
    else {
        let number = +operand;
        if (isNaN(number))
            throw Error("Incorrect const.");
        return new Const(number);
    }
}

function extractOperation(string, startIndex) {
    let op = "";
    let i = startIndex;
    while (string[i] === " " && i < string.length - 1) {
        i++;
    }
    while (string[i] !== " " && string[i] !== "(" && i < string.length - 1) {
        op += string[i];
        i++;
    }
    return { operation: op, newIndex: i };
}

function OpenBracket(string, startIndex, buf, argv) {
    let balance = 1;
    let i = startIndex;
    buf += string[i++];
    while (balance !== 0 && i < string.length - 1) {
        if (string[i] === "(") balance++;
        if (string[i] === ")") balance--;
        buf += string[i];
        i++;
    }
    if (balance !== 0)
        throw Error("Incorrect sequence.");
    argv.push(buf);
    return { newIndex: i };
}

function ResultBracket(string, startIndex, buf, argv) {
    let i = startIndex;
    while (string[i] !== " " && string[i] !== "(" && i < string.length - 1) {
        buf += string[i++];
    }
    argv.push(buf);
    return { newIndex: i };
}

function extractArguments(string, startIndex) {
    let argv = [];
    let buf = "";
    let i = startIndex;
    while (i < string.length - 1) {
        if (string[i] === "(") {
            let result = OpenBracket(string, i, buf, argv);
            i = result.newIndex;
            buf = "";
        } else {
            if (string[i] !== " ") {
                let result = ResultBracket(string, i, buf, argv);
                i = result.newIndex;
                buf = "";
            } else {
                i++;
            }
        }
    }
    return argv;
}








































