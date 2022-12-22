import expressions.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Expression expression = new Multiply(new Multiply(new Variable(), new Variable()), new Multiply(new Exponent(new Multiply(new Const(3), new Variable())), new Multiply(new Const(0.5), new Variable()
        )));
        Expression expo = ExpressionUtils.getExp(expression);
        Scanner sc = new Scanner(System.in);
        double a = sc.nextDouble();
        sc.nextLine();
        Expression b = ExpressionUtils.parse(sc.nextLine());
        DifferentialEquation equation = new DifferentialEquation(a, b);
        double x0 = sc.nextDouble(), y0 = sc.nextDouble();
        double c = equation.solve(x0, y0);
        System.out.println(c);
    }
}