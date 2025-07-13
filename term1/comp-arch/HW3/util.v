// модуль, который реализует расширенение
// 16-битной знаковой константы до 32-битной
module sign_extend(in, out);
  input [15:0] in;
  output [31:0] out;

  assign out = {{16{in[15]}}, in};
endmodule

//Расширение 26-битной константы до 32-битной
module extend(input [25:0] in, output [31:0] out);
  assign out = {{6{in[25]}}, in};
endmodule

// модуль, который реализует побитовый сдвиг числа
// влево на 2 бита
module shl_2(in, out);
  input [31:0] in;
  output [31:0] out;

  assign out = {in[29:0], 2'b00};
endmodule

// 32 битный сумматор
module adder(a, b, out);
  input [31:0] a, b;
  output [31:0] out;

  assign out = a + b;
endmodule

// 32-битный мультиплексор
module mux2_32(d0, d1, a, out);
  input [31:0] d0, d1;
  input a;
  output [31:0] out;
  assign out = a ? d1 : d0;
endmodule

// 5 - битный мультиплексор
module mux2_5(d0, d1, a, out);
  input [4:0] d0, d1;
  input a;
  output [4:0] out;
  assign out = a ? d1 : d0;
endmodule

// Арифметико-логическое устройство
module alu(
    input signed [31:0] a, b,
    input [2:0] control,
    output reg [31:0] result,
    output reg zero
);

    always @* begin
        case (control)
            3'b000: result = a & b;
            3'b001: result = a | b;
            3'b010: result = a + b;
            3'b100: result = a & (~b);
            3'b101: result = a | (~b);
            3'b110: result = a - b;
            3'b111: result = (a < b) ? 1 : 0;
            default: result = 32'b0;
        endcase
    end

    always @(result) begin
        zero = (result == 0) ? 1 : 0;
    end
endmodule


module and_gate(input a, input b, output y);
  assign y = a & b;
endmodule


module mux2_1(d0, d1, a, out);
  input d0, d1;
  input a;
  output out;
  assign out = a ? d1 : d0;
endmodule


