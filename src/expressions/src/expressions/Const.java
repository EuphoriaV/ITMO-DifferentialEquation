package expressions;

public class Const implements Expression {
    private double constant;

    public Const(double constant) {
        this.constant = constant;
    }

    @Override
    public double evaluate(double x) {
        return constant;
    }

    @Override
    public Expression diff() {
        return new Const(0);
    }

    @Override
    public String toString() {
        return String.valueOf(constant);
    }
}
