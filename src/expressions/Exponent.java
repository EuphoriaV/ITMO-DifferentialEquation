package expressions;

public class Exponent implements Expression {
    private Expression exp;

    public Exponent(Expression exp) {
        this.exp = exp;
    }

    @Override
    public double evaluate(double x) {
        return Math.exp(exp.evaluate(x));
    }

    @Override
    public Expression diff() {
        return new Multiply(this, exp.diff());
    }


    @Override
    public String toString() {
        return "(e ^ " + exp.toString() + ")";
    }
}
