package expressions;

public interface Expression {
    double evaluate(double x);

    Expression diff();
}
