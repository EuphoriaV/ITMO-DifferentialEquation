import expressions.*;

import java.io.*;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 48; i++) {
            new Main().solve(i);
        }
    }

    private void solve(int x) throws IOException {
        Scanner sc = new Scanner(new File("test" + x + ".in")).useLocale(Locale.US);
        double a = sc.nextDouble();
        sc.nextLine();
        Expression b = ExpressionUtils.parse(sc.nextLine());
        DifferentialEquation equation = new DifferentialEquation(a, b);
        double x0 = sc.nextDouble(), y0 = sc.nextDouble();
        double c = equation.solve(x0, y0);
        BufferedWriter writer = new BufferedWriter(new FileWriter("test" + x + ".out"));
        writer.write(c + "");
        writer.close();
    }
}