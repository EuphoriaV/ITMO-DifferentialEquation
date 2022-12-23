import expressions.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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