package expressions;

public class Negate implements Expression {
    private Expression exp;

    public Negate(Expression exp) {
        this.exp = exp;
    }

    @Override
    public double evaluate(double x) {
        return -exp.evaluate(x);
    }

    @Override
    public Expression diff() {
        return new Negate(exp.diff());
    }

    @Override
    public String toString() {
        return "(-" + exp.toString() + ")";
    }

    public Expression getExp() {
        return exp;
    }
}
