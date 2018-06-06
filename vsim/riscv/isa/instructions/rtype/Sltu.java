package vsim.riscv.isa.instructions.rtype;


public final class Sltu extends RType {

    public Sltu() {
        super(
            "sltu",
            "sltu rd, rs1, rs2",
            "set rd = 1 if rs1 < rs2 else 0, unsigned comparison"
        );
    }

    @Override
    protected int compute(int rs1, int rs2) {
        return (Integer.compareUnsigned(rs1, rs2) < 0) ? 1 : 0;
    }

}