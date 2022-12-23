package expressions;

public class Add extends DoubleExpression {
    public Add(Expression exp1, Expression exp2) {
        super(exp1, exp2);
    }

    @Override
    double f(double x, double y) {
        return x + y;
    }

    @Override
    public Expression diff() {
        return new Add(exp1.diff(), exp2.diff());
    }

    @Override
    public String toString() {
        return "(" + exp1.toString() + " + " + exp2.toString() + ")";
    }
}
