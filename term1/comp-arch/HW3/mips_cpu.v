`include "util.v"

module control_unit(opcode, funct, memtoreg, memwrite, bne, beq,
                    alusrc, regdst, regwrite, alucontrol, jump, jal, jr);

input wire [5:0] opcode, funct;
output wire memtoreg, memwrite, bne, beq, alusrc, regdst, regwrite, jump, jal, jr;
output reg [2:0] alucontrol;

reg MemtoReg, MemWrite, BNE, BEQ, ALUSrc, RegDst, RegWrite, Jump, Jal, Jr;

reg [1:0] ALUop;


always @* begin
    RegWrite = 0;
    RegDst = 0;
    ALUSrc = 0;
    MemtoReg = 0;
    Jal = 0;
    BNE = 0;
    BEQ = 0;
    MemWrite = 0;
    ALUop = 2'b00;
    Jump = 0;
    Jr = 0;
    case (opcode)
        6'b000000: begin
            RegWrite = 1;
            RegDst = 1;
            ALUop = 2'b10;
        end
        // lw
        6'b100011: begin
            RegWrite = 1;
            ALUSrc = 1;
            MemtoReg = 1;
        end
        // sw
        6'b101011: begin
            ALUSrc = 1;
            MemWrite = 1;
        end
        // beq
        6'b000100: begin
            BEQ = 1;
            ALUop = 2'b01;
        end
        // bne
        6'b000101: begin
            BNE = 1;
            ALUop = 2'b01;
        end
        // addi
        6'b001000: begin
            RegWrite = 1;
            ALUSrc = 1;
        end
        // andi
        6'b001100: begin
            RegWrite = 1;
            ALUSrc = 1;
            ALUop = 2'b11;
        end
        // j
        6'b000010: begin
            Jump = 1;
            ALUop = 2'b10;
        end
        // jal
        6'b000011: begin
            RegWrite = 1;
            Jal = 1;
            ALUop = 2'b10;
            Jump = 1;
        end
    endcase

if (ALUop == 2'b00)
begin
  alucontrol = 3'b010;
end else if (ALUop == 2'b11) 
begin
  alucontrol = 3'b000;
end else if (ALUop == 2'b01)
begin
  alucontrol = 3'b110;
end else begin
  case (funct)
  6'b100000: 
          begin
              alucontrol = 3'b010;
          end
  6'b100010: 
          begin
              alucontrol = 3'b110;
          end
  6'b100100: 
          begin
              alucontrol = 3'b000;
          end
  6'b100101: 
          begin
              alucontrol = 3'b001;
          end
  6'b101010: 
          begin
              alucontrol = 3'b111;
          end
  6'b001000: 
          begin 
  end
  endcase
end
end

assign {memtoreg, regdst, alusrc, regwrite, beq, memwrite, bne, jump, jal, jr} = {MemtoReg, RegDst, ALUSrc, RegWrite, BEQ, MemWrite, BNE, Jump, Jal, Jr};

endmodule


module mips_cpu(clk, pc, pc_new, instruction_memory_a, instruction_memory_rd, data_memory_a, data_memory_rd, data_memory_we, data_memory_wd,
                register_a1, register_a2, register_a3, register_we3, register_wd3, register_rd1, register_rd2);
  // сигнал синхронизации
  input clk;
  // текущее значение регистра PC
  inout [31:0] pc;
  // новое значение регистра PC (адрес следующей команды)
  output [31:0] pc_new;
  // we для памяти данных
  output data_memory_we;
  // адреса памяти и данные для записи памяти данных
  output [31:0] instruction_memory_a, data_memory_a, data_memory_wd;
  // данные, полученные в результате чтения из памяти
  inout [31:0] instruction_memory_rd, data_memory_rd;
  // we3 для регистрового файла
  output register_we3;
  // номера регистров
  output [4:0] register_a1, register_a2, register_a3;
  // данные для записи в регистровый файл
  output [31:0] register_wd3;
  // данные, полученные в результате чтения из регистрового файла
  inout [31:0] register_rd1, register_rd2;

  assign instruction_memory_a = pc;

  wire memtoreg, memwrite, bne, beq, alusrc, regdst, regwrite, jump, jal, jr;
  wire [2:0] alucontrol;
  control_unit control(instruction_memory_rd[31:26], instruction_memory_rd[5:0], memtoreg, memwrite, bne, beq, alusrc, regdst, regwrite, alucontrol, jump, jal, jr);

  assign register_a1 = instruction_memory_rd[25:21];
  assign register_a2 = instruction_memory_rd[20:16];
  wire [4:0] RegWrite;
  mux2_5 writing1(instruction_memory_rd[20:16], instruction_memory_rd[15:11], regdst, RegWrite);
  mux2_5 writing2(RegWrite, 5'b11111, jal, register_a3);

  wire [31:0] const, src, res, Reg, counter1, counter2, shiftedConst, PC1, Jump, extends, PC2;
  wire zero, alu5, alu6, AddressControl, nott, nol, source;

  sign_extend se(instruction_memory_rd[15:0], const);

  mux2_32 operation(register_rd2, const, alusrc, src);
  alu alu1(register_rd1, src, alucontrol, res, zero);

  assign {register_we3, data_memory_a, data_memory_wd, data_memory_we} = {regwrite, res, register_rd2, memwrite};

  mux2_32 write1(res, data_memory_rd, memtoreg, Reg);
  mux2_32 write2(Reg, counter1, jal, register_wd3);

  shl_2 s(const, shiftedConst);
  alu alu2(pc, 4, 3'b010, counter1, alu5);
  alu alu3(counter1, shiftedConst, 3'b010, counter2, alu6);

  mux2_1 mx1(bne, beq, zero, AddressControl);
  assign nott = !zero;
  
  mux2_1 mx2(nott, zero, beq, nol);

  and_gate n1(AddressControl, nol, source);

  mux2_32 pc5(counter1, counter2, source, PC1);

  extend se1(instruction_memory_rd[25:0], Jump);
  shl_2 sl1(Jump, extends);
  mux2_32 pc6(PC1, extends, jump, PC2);
  mux2_32 pc7(PC2, register_rd1, jr, pc_new);
endmodule
