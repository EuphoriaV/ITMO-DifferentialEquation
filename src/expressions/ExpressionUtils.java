package expressions;

public class ExpressionUtils {
    private static int pos;

    public static Expression parse(String s) {
        pos = 0;
        String[] arr = s.replace(")", " ) ").replace("(", " ( ").replace("+", " + ").
                replace("-", " - ").replace("*", " * ").trim().split("\\s+");
        return parseRec(arr);
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
        if (expression instanceof DoubleExpression) {
            ((DoubleExpression) expression).setExp1(simplify(((DoubleExpression) expression).getExp1()));
            ((DoubleExpression) expression).setExp2(simplify(((DoubleExpression) expression).getExp2()));
        }
        if (expression instanceof Negate && ((Negate) expression).getExp() instanceof Negate) {
            expression = simplify(((Negate) ((Negate) expression).getExp()).getExp());
        }
        if (expression instanceof Negate && ((Negate) expression).getExp() instanceof Add) {
            expression = new Add(new Negate(simplify(((Add) ((Negate) expression).getExp()).getExp1())),
                    new Negate(simplify(((Add) ((Negate) expression).getExp()).getExp2())));
        }
        if (expression instanceof Negate && ((Negate) expression).getExp() instanceof Multiply) {
            expression = new Multiply(new Negate(simplify(((Multiply) ((Negate) expression).getExp()).getExp1())),
                    simplify(((Multiply) ((Negate) expression).getExp()).getExp2()));
        }
        if (expression instanceof Exponent) {
            ((Exponent) expression).setExp(simplify(((Exponent) expression).getExp()));
        }
        if (expression instanceof DoubleExpression doubleExpression) {
            if (doubleExpression.getExp1() instanceof Const && doubleExpression.getExp1().evaluate(1) == 0) {
                if (expression instanceof Add) {
                    expression = doubleExpression.getExp2();
                } else if (expression instanceof Multiply) {
                    expression = new Const(0);
                }
            }
            if (doubleExpression.getExp2() instanceof Const && doubleExpression.getExp2().evaluate(1) == 0) {
                if (expression instanceof Add) {
                    expression = doubleExpression.getExp1();
                } else if (expression instanceof Multiply) {
                    expression = new Const(0);
                }
            }
            if (doubleExpression.getExp1() instanceof Const && doubleExpression.getExp1().evaluate(1) == 1 && expression instanceof Multiply) {
                expression = doubleExpression.getExp2();
            }
            if (doubleExpression.getExp2() instanceof Const && doubleExpression.getExp2().evaluate(1) == 1 && expression instanceof Multiply) {
                expression = doubleExpression.getExp1();
            }
            if (doubleExpression.getExp1() instanceof Const && doubleExpression.getExp2() instanceof Const) {
                expression = new Const(expression.evaluate(0));
            }
        }
        return simplifyMult(expression);
    }

    private static Expression simplifyMult(Expression expression) {
        if (expression instanceof Multiply multiply) {
            //(a+b)*(c+d) = a*c + b*c + a*d + b*d
            if (multiply.getExp1() instanceof Add && multiply.getExp2() instanceof Add) {
                expression = new Add(new Add(new Multiply(((Add) multiply.getExp1()).getExp1(), ((Add) multiply.getExp2()).getExp1()),
                        new Multiply(((Add) multiply.getExp1()).getExp1(), ((Add) multiply.getExp2()).getExp2())), new Add(
                        new Multiply(((Add) multiply.getExp1()).getExp2(), ((Add) multiply.getExp2()).getExp1()),
                        new Multiply(((Add) multiply.getExp1()).getExp2(), ((Add) multiply.getExp2()).getExp2())
                ));
            }
            //(a+b)*c = a*c + b*c
            else if (multiply.getExp1() instanceof Add) {
                expression = new Add(new Multiply(((Add) multiply.getExp1()).getExp1(), multiply.getExp2()),
                        new Multiply(((Add) multiply.getExp1()).getExp2(), multiply.getExp2()));
            }
            //a*(b+c) = a*b + a*c
            else if (multiply.getExp2() instanceof Add) {
                expression = new Add(new Multiply(((Add) multiply.getExp2()).getExp1(), multiply.getExp1()),
                        new Multiply(((Add) multiply.getExp2()).getExp2(), multiply.getExp1()));
            }
        }
        if (expression instanceof DoubleExpression) {
            ((DoubleExpression) expression).setExp1(simplifyMult(((DoubleExpression) expression).getExp1()));
            ((DoubleExpression) expression).setExp2(simplifyMult(((DoubleExpression) expression).getExp2()));
        }
        if (expression instanceof Negate) {
            ((Negate) expression).setExp(simplifyMult(((Negate) expression).getExp()));
        }
        if (expression instanceof Exponent) {
            ((Exponent) expression).setExp(simplifyMult(((Exponent) expression).getExp()));
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
        return new Multiply(new Variable(), powerOf(n - 1));
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

    public static boolean bad(Expression expression) {
        if (expression instanceof Negate) {
            return bad(((Negate) expression).getExp());
        } else if (expression instanceof Exponent) {
            return bad(((Exponent) expression).getExp());
        } else if (expression instanceof Add) {
            return bad(((DoubleExpression) expression).getExp2()) || bad(((DoubleExpression) expression).getExp1());
        } else if (expression instanceof Multiply) {
            return hasAdd(expression);
        } else {
            return false;
        }
    }

    public static boolean hasAdd(Expression expression) {
        if (expression instanceof Add) {
            return true;
        } else if (expression instanceof Multiply) {
            return hasAdd(((Multiply) expression).getExp1()) || hasAdd(((Multiply) expression).getExp2());
        } else {
            return false;
        }
    }

    public static Expression integrateSimple(Expression expression) {
        return integrate(simplify(expression));
    }

    public static Expression integrate(Expression expression) {
        Expression ans = null;
        if (expression instanceof Exponent) {
            if (expression.diff().evaluate(0) == 0) {
                ans = integrateSimple(new Const(1));
            } else {
                ans = new Multiply(expression, new Const(1 / expression.diff().evaluate(0)));
            }
        } else if (expression instanceof Const) {
            if (expression.evaluate(1) == 0) {
                return new Const(0);
            }
            ans = new Multiply(expression, new Variable());
        } else if (expression instanceof Variable) {
            ans = new Multiply(new Const(0.5), new Multiply(new Variable(), new Variable()));
        } else if (expression instanceof Add) {
            ans = new Add(integrateSimple(((Add) expression).getExp1()), integrateSimple(((Add) expression).getExp2()));
        } else if (expression instanceof Negate) {
            ans = new Negate(integrateSimple(((Negate) expression).getExp()));
        } else if (expression instanceof Multiply && getExponent(expression) == null) {
            double constant = getConst((Multiply) expression);
            int power = getPower((Multiply) expression);
            ans = new Multiply(new Const(constant / (power + 1)), powerOf(power + 1));
        } else if (expression instanceof Multiply) {
            double constant = getConst((Multiply) expression);
            int power = getPower((Multiply) expression);
            if (power == 0) {
                if (expression.diff().evaluate(0) == 0) {
                    ans = integrateSimple(new Const(constant));
                } else {
                    ans = new Multiply(expression, new Const(constant / expression.diff().evaluate(0)));
                }
            } else {
                Expression dv = getExponent(expression);
                Expression u = new Multiply(new Const(constant), powerOf(power));
                Expression du = u.diff();
                Expression v = integrateSimple(dv);
                ans = new Add(new Multiply(u, v), new Negate(integrateSimple(new Multiply(du, v))));
            }
        }
        return ans;
    }
}
