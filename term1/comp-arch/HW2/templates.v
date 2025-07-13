module and_gate(a, b, out);
  input wire a, b;
  output wire out;

  wire nand_out;

  nand_gate nand_gate1(a, b, nand_out);
  not_gate not_gate1(nand_out, out);
endmodule

module nand_gate(a, b, out);
  input wire a, b;
  output out;

  supply1 pwr;
  supply0 gnd;

  wire nmos1_out;

  pmos pmos1(out, pwr, a);
  pmos pmos2(out, pwr, b);

  nmos nmos1(nmos1_out, gnd, b);
  nmos nmos2(out, nmos1_out, a);
endmodule

module not_gate(a, out);
  input wire a;
  output out;

  supply1 pwr;
  supply0 gnd;

  // 1 - сток, 2 - исток, 3 - база
  pmos pmos1(out, pwr, a);
  nmos nmos1(out, gnd, a);
endmodule

module my_nor(a, b, out);
  input wire a, b;
  output out;

  supply1 pwr;
  supply0 gnd;

  wire p1_out;

  pmos pmos1(p1_out, pwr, a);
  pmos pmos2(out, p1_out, b);

  nmos nmos1(out, gnd, a);
  nmos nmos2(out, gnd, b);
endmodule

module my_or(a, b, out);
  input wire a, b;
  output wire out;

  wire nor_o;

  my_nor norr1(a, b, nor_o);
  not_gate not1(nor_o, out);

endmodule

module my_xor(a, b, out);
  input wire a, b;
  output wire out;

  wire not_out1, not_out2, and_out1, and_out2;

  not_gate not1(a, not_out1);
  not_gate not2(b, not_out2);
  and_gate and1(not_out1, b, and_out1);
  and_gate and2(a, not_out2, and_out2);
  my_or or1(and_out1, and_out2, out);
endmodule

module half_adder(a, b, sum, carry);
  input wire a;
  input wire b;
  output wire sum;
  output wire carry;

  my_xor xor_gate1(a, b, sum);
  and_gate and_gate1(a, b, carry);
endmodule

module full_adder(a, b, cin, sum, cout);
  input a, b, cin;
  output sum, cout;

  wire s1, s2, c1;

  half_adder h1(a, b, s1, c1);  
  half_adder h2(s1, cin, sum, s2);
  my_or h3(c1, s2, cout);
endmodule

module alu(a, b, control, res);
  input [3:0] a, b; // Операнды
  input [2:0] control; // Управляющие сигналы для выбора операции

  output reg [3:0] res; // Результат
  // TODO: implementation

  wire [3:0] and_result, nand_result, or_result, nor_result, full_result;
  wire [3:0] carry;
  wire [3:0] sub_result;
  wire [3:0] borrow;

  // Операция AND
  and_gate and1(a[0], b[0], and_result[0]);
  and_gate and2(a[1], b[1], and_result[1]);
  and_gate and3(a[2], b[2], and_result[2]);
  and_gate and4(a[3], b[3], and_result[3]);

  // Операция NAND
  nand_gate nand1(a[0], b[0], nand_result[0]);
  nand_gate nand2(a[1], b[1], nand_result[1]);
  nand_gate nand3(a[2], b[2], nand_result[2]);
  nand_gate nand4(a[3], b[3], nand_result[3]);

  // Операция OR
  my_or or1(a[0], b[0], or_result[0]);
  my_or or2(a[1], b[1], or_result[1]);
  my_or or3(a[2], b[2], or_result[2]);
  my_or or4(a[3], b[3], or_result[3]);

  // Операция NOR
  my_nor nor1(a[0], b[0], nor_result[0]);
  my_nor nor2(a[1], b[1], nor_result[1]);
  my_nor nor3(a[2], b[2], nor_result[2]);
  my_nor nor4(a[3], b[3], nor_result[3]);

  // Операция SUM
  full_adder full1(a[0], b[0], 1'b0, full_result[0], carry[0]);
  full_adder full2(a[1], b[1], carry[0], full_result[1], carry[1]);
  full_adder full3(a[2], b[2], carry[1], full_result[2], carry[2]);
  full_adder full4(a[3], b[3], carry[2], full_result[3], carry[3]);

  always @*
  case (control)
    3'b000: res = and_result;
    3'b001: res = nand_result;
    3'b010: res = or_result;
    3'b011: res = nor_result;
    3'b100: res = full_result;
    //3'b101: res = sub_result;
    // ... остальные операции ...
    default: res = 4'b0;
  endcase
endmodule

module d_latch(clk, d, we, q);
  input clk; // Сигнал синхронизации
  input d; // Бит для записи в ячейку
  input we; // Необходимо ли перезаписать содержимое ячейки

  output reg q; // Сама ячейка
  // Изначально в ячейке хранится 0
  initial begin
    q <= 0;
  end
  // Значение изменяется на переданное на спаде сигнала синхронизации
  always @ (negedge clk) begin
    // Запись происходит при we = 1
    if (we) begin
      q <= d;
    end
  end
endmodule

module register_file(clk, rd_addr, we_addr, we_data, rd_data, we);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr, we_addr; // Номера регистров для чтения и записи
  input [3:0] we_data; // Данные для записи в регистровый файл
  input we; // Необходимо ли перезаписать содержимое регистра

  output [3:0] rd_data; // Данные, полученные в результате чтения из регистрового файла
  // TODO: implementation
endmodule

module counter(clk, addr, control, immediate, data);
  input clk; // Сигнал синхронизации
  input [1:0] addr; // Номер значения счетчика которое читается или изменяется
  input [3:0] immediate; // Целочисленная константа, на которую увеличивается/уменьшается значение счетчика
  input control; // 0 - операция инкремента, 1 - операция декремента

  output [3:0] data; // Данные из значения под номером addr, подающиеся на выход
  // TODO: implementation
endmodule
