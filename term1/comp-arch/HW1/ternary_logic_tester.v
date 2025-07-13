`include "ternary_logic.v"

module mux_4_1(data0, data1, data2, data3, control, out);
    input [31:0] control;
    input [1:0] data0, data1, data2, data3;
    output reg [1:0] out;
    always @(data0 or data1 or data2 or data3 or control) begin
        case (control)
            0: out <= data0;
            1: out <= data1;
            2: out <= data2;
            3: out <= data3;
        endcase
    end
endmodule

module tester();
    reg [1:0] a, b, oute;			    // values from testvectors
    wire [1:0] min_out, max_out, any_out, consensus_out;
    wire [1:0] out;
    reg [31:0] vectornum, errors, i;  // bookkeeping variables
    reg [5:0] testvectors[0:8];		// array of testvectors
    reg verdict;
    ternary_min min_to_test(a, b, min_out);
    ternary_max max_to_test(a, b, max_out);
    ternary_any any_to_test(a, b, any_out);
    ternary_consensus consensus_to_test(a, b, consensus_out);
    mux_4_1 mux(min_out, max_out, any_out, consensus_out, i, out);
    initial begin
        verdict = 1;
        for (i = 0; i < 4; i = i + 1) begin
            case (i)
                0: begin $display("Test min"); $readmemb("min.mem", testvectors); end
                1: begin $display("Test max"); $readmemb("max.mem", testvectors); end
                2: begin $display("Test any"); $readmemb("any.mem", testvectors); end
                3: begin $display("Test consensus"); $readmemb("consensus.mem", testvectors); end
            endcase
            errors = 0;
            for (vectornum = 0; vectornum < 9; vectornum = vectornum + 1) begin
                {a, b, oute} = testvectors[vectornum];
                #1
                if ((out[0] !== oute[0]) || (out[1] !== oute[1]))
                    begin
                        $display("Error: inputs a=0b%b b=0b%b", a, b);
                        $display("  outputs: expected 0b%b, actual 0b%b", oute, out);
                        errors = 1 + errors;
                    end
            end
            $display("%d tests completed with %d errors", vectornum, errors);
            // End simulation:
            if (errors !== 0)
                begin
                    $display("FAIL");
                    verdict = 0;
                end
            else
                begin
                    $display("OK");
                end
            #5;
        end
        if (verdict) $display("Verdict: OK");
        else $display("Verdict: FAIL");
        $finish;
    end
endmodule
