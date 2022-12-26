package expressions;

public class Multiply extends DoubleExpression {
    public Multiply(Expression exp1, Expression exp2) {
        super(exp1, exp2);
    }

    @Override
    double f(double x, double y) {
        return x * y;
    }

    @Override
    public Expression diff() {
        return new Add(new Multiply(exp1, exp2.diff()), new Multiply(exp1.diff(), exp2));
    }

    @Override
    public String toString() {
        return "(" + exp1.toString() + " * " + exp2.toString() + ")";
    }
}
