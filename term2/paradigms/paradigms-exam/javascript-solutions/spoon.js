// :NOTE: * как сделать ограничение только на память?
function runProgram(program, input, maxSteps = 10000, maxMemory = 30000) {
    const commands = {
        '1': '+',
        '000': '-',
        '010': '>',
        '011': '<',
        '00100': '[',
        '0011': ']',
        '0010110': ',',
        '001010': '.'
    };

    let bfProgram = '';
    let i = 0;
    while (i < input.length) {
        for (const [spoonCommand, bfCommand] of Object.entries(commands)) {
            if (input.startsWith(spoonCommand, i)) {
                bfProgram += bfCommand;
                i += spoonCommand.length;
                break;
            }
        }
    }

    let pc = 0;
    let pointer = 0;
    const memory = new Uint8Array(maxMemory).fill(0);
    const inputBuffer = input.split('');
    let inputPointer = 0;
    let output = '';
    let steps = 0;
    const loopStack = [];

    while (pc < bfProgram.length) {
        if (steps >= maxSteps) {
            console.error('Error: Maximum steps exceeded');
            return;
        }
        steps++;

        switch (bfProgram[pc]) {
            case '>':
                pointer++;
                // :NOTE: * копипаста
                if (pointer >= maxMemory) {
                    console.error('Error: Memory pointer out of bounds');
                    return;
                }
                break;
            case '<':
                pointer--;
                if (pointer < 0) {
                    console.error('Error: Memory pointer out of bounds');
                    return;
                }
                break;
            case '+':
                // :NOTE: * модуль 256
                memory[pointer]++;
                break;
            case '-':
                memory[pointer]--;
                break;
            case '.':
                output += String.fromCharCode(memory[pointer]);
                break;
            case ',':
                if (inputPointer < inputBuffer.length) {
                    memory[pointer] = inputBuffer[inputPointer].charCodeAt(0);
                    inputPointer++;
                } else {
                    memory[pointer] = 0;
                }
                break;
            case '[':
                if (memory[pointer] === 0) {
                    let openBrackets = 1;
                    while (openBrackets > 0) {
                        pc++;
                        if (pc >= bfProgram.length) {
                            console.error('Error: Loop not closed');
                            return;
                        }
                        if (bfProgram[pc] === '[') openBrackets++;
                        if (bfProgram[pc] === ']') openBrackets--;
                    }
                } else {
                    loopStack.push(pc);
                }
                break;
            case ']':
                if (loopStack.length === 0) {
                    console.error('Error: Loop closing without opening');
                    return;
                }
                if (memory[pointer] !== 0) {
                    pc = loopStack[loopStack.length - 1];
                } else {
                    loopStack.pop();
                }
                break;
            default:
                break;
        }
        pc++;
    }

    console.log(output);
}

// Пример использования
const program = '';
const input = '11111111110010001011111110101111111111010111010101101101101100000110101100101001010010101111111001010001010111001010010110010100110111111111111111110010100100010101110010100000000000000000000010100000000000000000000000000010100101001010010001010';

runProgram(program, input); // Ожидаемый вывод: "Hello World!"
