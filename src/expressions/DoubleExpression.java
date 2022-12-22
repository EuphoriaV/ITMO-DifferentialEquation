package expressions;

public abstract class DoubleExpression implements Expression {
    protected Expression exp1, exp2;

    public DoubleExpression(Expression exp1, Expression exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    abstract double f(double x, double y);

    @Override
    public double evaluate(double x) {
        return f(exp1.evaluate(x), exp2.evaluate(x));
    }

    public Expression getExp1() {
        return exp1;
    }

    public Expression getExp2() {
        return exp2;
    }

    public void setExp1(Expression exp1) {
        this.exp1 = exp1;
    }

    public void setExp2(Expression exp2) {
        this.exp2 = exp2;
    }
}
