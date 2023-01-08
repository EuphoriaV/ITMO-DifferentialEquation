import expressions.Expression;
import expressions.ExpressionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
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
        double c = round(equation.solve(x0, y0), 8);
        BufferedWriter writer = new BufferedWriter(new FileWriter("test" + x + ".out"));
        writer.write(c + "");
        writer.close();
    }

    public double round(double n, int digits) {
        String s = new BigDecimal(n).toPlainString();
        int start = digits;
        if (n < 0) {
            start++;
        }
        if (s.substring(0, start).contains(".")) {
            start++;
        }
        StringBuilder sb = new StringBuilder(s);
        for (int i = start; i < s.length(); i++) {
            if (sb.charAt(i) != '.') {
                sb.setCharAt(i, '0');
            }
        }
        return Double.parseDouble(sb.toString());
    }
}