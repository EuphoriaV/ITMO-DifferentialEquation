import expressions.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateTest {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i <= 15; i++) {
            for (int j = 0; j < 3; j++) {
                BufferedWriter writer = new BufferedWriter(new FileWriter("test" + (i * 3 + j) + ".in"));
                double a = new Random().nextDouble() * 5 + 1;
                double x0 = new Random().nextDouble() * 5 + 1, y0 = new Random().nextDouble() * 5 + 1;
                writer.write(a + "\n" + recGen(i) + "\n" + x0 + "\n" + y0);
                writer.close();
            }
        }
    }

    private static Expression recGen(int n) {
        if (n == 0) {
            boolean constant = (new Random().nextInt() % 2 + 2) % 2 == 0;
            if (constant) {
                return new Const(new Random().nextDouble() * 10 + 1);
            } else {
                return new Variable();
            }
        } else {
            return switch ((new Random().nextInt() % 4 + 4) % 4) {
                case 0, 1 ->
                        new Multiply(recGen((new Random().nextInt() % n + n) % n), recGen((new Random().nextInt() % n + n) % n));
                case 2 ->
                        new Add(recGen((new Random().nextInt() % n + n) % n), recGen((new Random().nextInt() % n + n) % n));
                case 3 ->
                        new Subtract(recGen((new Random().nextInt() % n + n) % n), recGen((new Random().nextInt() % n + n) % n));
                default -> null;
            };
        }
    }
}
