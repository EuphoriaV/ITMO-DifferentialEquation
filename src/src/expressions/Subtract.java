package expressions;

public class Subtract extends DoubleExpression {
    public Subtract(Expression exp1, Expression exp2) {
        super(exp1, exp2);
    }

    @Override
    double f(double x, double y) {
        return x - y;
    }

    @Override
    public Expression diff() {
        return new Subtract(exp1.diff(), exp2.diff());
    }

    @Override
    public String toString() {
        return "(" + exp1.toString() + " - " + exp2.toString() + ")";
    }
}
