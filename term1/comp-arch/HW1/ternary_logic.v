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


module and_gate(a, b, out);
  input wire a, b;
  output wire out;

  wire nand_out;

  nand_gate nand_gate1(a, b, nand_out);
  not_gate not_gate1(nand_out, out);
endmodule

module half_adder(a, b, c_out, s);
  input wire a;
  input wire b;
  output wire c_out;
  output wire s;

  and_gate and_gate1(a, b, c_out);
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


module ternary_min(a, b, out);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire or_out, xor_out, and_out2, and_out3;
  and_gate and1(a[1], b[1], out[1]);
  my_or or1(a[1], b[1], or_out);
  my_xor xor1(a[0], b[0], xor_out);
  and_gate and2(or_out, xor_out, and_out2);
  and_gate and3(a[0], b[0], and_out3);
  my_or or2(and_out2, and_out3, out[0]);
endmodule

module ternary_max(a, b, out);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire nor_out, xor_out, and_out2, and_out3;
  my_or or1(a[1], b[1], out[1]);
  my_nor nor1(a[1], b[1], nor_out);
  my_xor xor1(a[0], b[0], xor_out);
  and_gate and2(nor_out, xor_out, and_out2);
  and_gate and3(a[0], b[0], and_out3);
  my_or or2(and_out2, and_out3, out[0]);
endmodule

module ternary_any(a, b, out);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire xor_out1, xor_out2, and_out2, and_out3; 
  my_xor xor1(a[1], b[1], xor_out1);
  my_xor xor2(a[0], b[0], xor_out2);
  and_gate and2(xor_out1, xor_out2, and_out2);
  and_gate and3(a[1], b[1], and_out3);
  my_or or2(and_out2, and_out3, out[1]);

  wire xor_out4, not_out2, and_out4, and_out5, xor_out5;
  my_xor xor4(a[0], b[0], xor_out4);
  not_gate not2(xor_out4, not_out2);
  my_xor xor5(a[1], b[1], xor_out5);
  and_gate and4(xor_out5, not_out2, and_out4);
  and_gate and5(a[0], b[0], and_out5);
  my_or or3(and_out5, and_out4, out[0]);
endmodule

module ternary_consensus(a, b, out);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire xor_out5, or_out4;
  and_gate and8(a[1], b[1], out[1]);
  my_xor xor6(a[1], b[1], xor_out5);
  my_or or4(a[0], b[0], or_out4);
  my_or or5(xor_out5, or_out4, out[0]);
endmodule
