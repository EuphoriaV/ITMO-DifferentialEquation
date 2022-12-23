package expressions;

public class ExpressionUtils {
    private static int pos;

    public static Expression parse(String s) {
        pos = 0;
        String[] arr = s.replace(")", " ) ").replace("(", " ( ").replace("+", " + ").
                replace("-", " - ").replace("*", " * ").trim().split("\\s+");
        return parseRec(arr);
    }

    private static Expression mult(Expression exp1, Expression exp2) {
        return simplify(new Multiply(exp1, exp2));
    }

    private static Expression add(Expression exp1, Expression exp2) {
        return simplify(new Add(exp1, exp2));
    }

    private static Expression neg(Expression exp) {
        return simplify(new Negate(exp));
    }

    private static Expression parseRec(String[] arr) {
        if (arr[pos].equals("x")) {
            pos++;
            return new Variable();
        }
        try {
            double constant = Double.parseDouble(arr[pos]);
            pos++;
            return new Const(constant);
        } catch (NumberFormatException ignored) {
            if (arr[pos].equals("(")) {
                pos++;
                Expression a = parseRec(arr);
                String op = arr[pos];
                pos++;
                Expression b = parseRec(arr);
                pos++;
                switch (op) {
                    case "+":
                        return new Add(a, b);
                    case "-":
                        return new Add(a, new Negate(b));
                    case "*":
                        return new Multiply(a, b);
                }
            }
        }
        return null;
    }

    public static Expression simplify(Expression expression) {
        expression = simplifyMult(expression);
        if (expression instanceof DoubleExpression doubleExpression) {
            ((DoubleExpression) expression).setExp1(simplify(((DoubleExpression) expression).getExp1()));
            ((DoubleExpression) expression).setExp2(simplify(((DoubleExpression) expression).getExp2()));
            if (doubleExpression.getExp1() instanceof Const && doubleExpression.getExp1().evaluate(1) == 0) {
                if (expression instanceof Add) {
                    return simplify(doubleExpression.getExp2());
                } else if (expression instanceof Multiply) {
                    return new Const(0);
                }
            }
            if (doubleExpression.getExp2() instanceof Const && doubleExpression.getExp2().evaluate(1) == 0) {
                if (expression instanceof Add) {
                    return simplify(doubleExpression.getExp1());
                } else if (expression instanceof Multiply) {
                    return new Const(0);
                }
            }
            if (doubleExpression.getExp1() instanceof Const && doubleExpression.getExp1().evaluate(1) == 1 && expression instanceof Multiply) {
                return simplify(doubleExpression.getExp2());
            }
            if (doubleExpression.getExp2() instanceof Const && doubleExpression.getExp2().evaluate(1) == 1 && expression instanceof Multiply) {
                return simplify(doubleExpression.getExp1());
            }
            if (doubleExpression.getExp1() instanceof Const && doubleExpression.getExp2() instanceof Const) {
                return new Const(expression.evaluate(0));
            }
        }
        return expression;
    }

    private static Expression simplifyMult(Expression expression) {
        if (expression instanceof DoubleExpression doubleExpression) {
            doubleExpression.setExp1(simplify(doubleExpression.getExp1()));
            doubleExpression.setExp2(simplify(doubleExpression.getExp2()));
        }
        if (expression instanceof Multiply multiply) {
            //(a+b)*(c+d) = a*c + b*c + a*d + b*d
            if (multiply.getExp1() instanceof Add && multiply.getExp2() instanceof Add) {
                expression = add(add(mult(((Add) multiply.getExp1()).getExp1(), ((Add) multiply.getExp2()).getExp1()),
                        mult(((Add) multiply.getExp1()).getExp1(), ((Add) multiply.getExp2()).getExp2())), add(
                        mult(((Add) multiply.getExp1()).getExp2(), ((Add) multiply.getExp2()).getExp1()),
                        mult(((Add) multiply.getExp1()).getExp2(), ((Add) multiply.getExp2()).getExp2())
                ));
            }
            //(a+b)*c = a*c + b*c
            else if (multiply.getExp1() instanceof Add) {
                expression = add(mult(((Add) multiply.getExp1()).getExp1(), multiply.getExp2()),
                        mult(((Add) multiply.getExp1()).getExp2(), multiply.getExp2()));
            }
            //a*(b+c) = a*b + a*c
            else if (multiply.getExp2() instanceof Add) {
                expression = add(mult(((Add) multiply.getExp2()).getExp1(), multiply.getExp1()),
                        mult(((Add) multiply.getExp2()).getExp2(), multiply.getExp1()));
            }
        }
        return expression;
    }

    private static double getConst(Multiply multiply) {
        Expression expo = getExponent(multiply);
        if (expo == null) {
            return multiply.evaluate(1);
        } else {
            return multiply.evaluate(1) / expo.evaluate(1);
        }
    }

    private static int getPower(Multiply multiply) {
        Expression expo = getExponent(multiply);
        if (expo == null) {
            return (int) (0.01 + Math.log(multiply.evaluate(Math.E) / getConst(multiply)));
        } else {
            return (int) (0.01 + Math.log(multiply.evaluate(Math.E) / (expo.evaluate(Math.E) * getConst(multiply))));
        }
    }

    private static Expression powerOf(int n) {
        if (n == 1) {
            return new Variable();
        }
        return mult(new Variable(), powerOf(n - 1));
    }

    private static Expression getExponent(Expression expression) {
        if (expression instanceof Exponent) {
            return expression;
        }
        if (expression instanceof Multiply) {
            Expression expression1 = getExponent(((Multiply) expression).getExp1());
            Expression expression2 = getExponent(((Multiply) expression).getExp2());
            if (expression1 instanceof Exponent) {
                return expression1;
            }
            if (expression2 instanceof Exponent) {
                return expression2;
            }
        }
        return null;
    }

    public static Expression integrate(Expression expression) {
        Expression ans = null;
        if (expression instanceof Exponent) {
            ans = mult(expression, new Const(1 / expression.diff().evaluate(0)));
        } else if (expression instanceof Const) {
            if (expression.evaluate(1) == 0) {
                return new Const(0);
            }
            ans = mult(expression, new Variable());
        } else if (expression instanceof Variable) {
            ans = integrate(mult(new Const(1), expression));
        } else if (expression instanceof Add) {
            ans = add(integrate(((Add) expression).getExp1()), integrate(((Add) expression).getExp2()));
        } else if (expression instanceof Negate) {
            ans = neg(integrate(((Negate) expression).getExp()));
        } else if (expression instanceof Multiply && getExponent(expression) == null) {
            double constant = getConst((Multiply) expression);
            int power = getPower((Multiply) expression);
            ans = mult(new Const(constant / (power + 1)), powerOf(power + 1));
        } else if (expression instanceof Multiply) {
            double constant = getConst((Multiply) expression);
            int power = getPower((Multiply) expression);
            if (power == 0) {
                ans = mult(expression, new Const(constant / expression.diff().evaluate(0)));
            } else {
                Expression dv = getExponent(expression);
                Expression u = mult(new Const(constant), powerOf(power));
                Expression du = u.diff();
                Expression v = integrate(simplify(dv));
                return add(mult(u, v), neg(integrate(simplify(mult(du, v)))));
            }
        }
        return simplify(ans);
    }
}
