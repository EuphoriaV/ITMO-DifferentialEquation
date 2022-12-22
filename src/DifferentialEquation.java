import expressions.*;

public class DifferentialEquation {
    private double a;
    private Expression b;

    public DifferentialEquation(double a, Expression b) {
        this.a = a;
        this.b = b;
    }

    public double solve(double x0, double y0) {
        Expression firstIntegral = ExpressionUtils.integrate(new Const(a));
        Expression secondIntegral = ExpressionUtils.integrate(ExpressionUtils.simplify(new Multiply(new Exponent(new Subtract(new Const(0), firstIntegral)), b)));
        double cc = new Exponent(firstIntegral).evaluate(x0);
        double z = secondIntegral.evaluate(x0);
        return y0 / (cc) - z;
    }
}
