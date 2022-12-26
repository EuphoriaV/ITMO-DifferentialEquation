package expressions;

public class Variable implements Expression {

    @Override
    public double evaluate(double x) {
        return x;
    }

    @Override
    public Expression diff() {
        return new Const(1);
    }

    @Override
    public String toString() {
        return "x";
    }
}
