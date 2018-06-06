package vsim.riscv.isa.instructions.stype;

import vsim.Globals;
import vsim.riscv.hardware.Register;
import vsim.riscv.isa.instructions.Code;
import vsim.riscv.isa.instructions.Format;
import vsim.riscv.isa.instructions.Instruction;


abstract class SType extends Instruction {

    protected SType(String mnemonic, String usage, String description) {
        super(Format.S, mnemonic, usage, description);
    }

    protected abstract void set(int rs1, int rs2, int imm);

    @Override
    public void execute(Code code) {
        Register rs1 = Globals.regfile.getRegister(code.getRs1());
        Register rs2 = Globals.regfile.getRegister(code.getRs2());
        int imm = code.getImm(Format.S);
        this.set(rs1.getValue(), rs2.getValue(), imm);
    }

}