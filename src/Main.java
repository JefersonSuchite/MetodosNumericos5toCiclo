import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ingresar la cantidad de puntos
        System.out.print("Ingresa la cantidad de puntos (3 o 4): ");
        int n = scanner.nextInt();

        double[][] points = new double[n][2];

        // Ingresar los puntos
        System.out.println("Ingresa los puntos (x, y) uno por uno:");
        for (int i = 0; i < n; i++) {
            System.out.printf("Punto %d - x: ", i + 1);
            points[i][0] = scanner.nextDouble();
            System.out.printf("Punto %d - y: ", i + 1);
            points[i][1] = scanner.nextDouble();
        }

        // Mostrar los puntos ingresados
        System.out.println("\nPolinomios de Lagrange:\n");
        for (int i = 0; i < n; i++) {
            System.out.printf("I[%d]: (%.1f, %.1f)\n", i, points[i][0], points[i][1]);
        }

        // Evaluar el polinomio en varios valores de x
        double[] xValues = {-10.0, -9.5, -9.0, -8.5, -8.0, -7.5, -7.0, -6.5, -6.0, -5.5, -5.0, -4.5, -4.0, -3.5, -3.0, -2.5, -2.0, -1.5, -1.0, 0, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10};
        scanner.nextLine(); // Limpiar el buffer del scanner

        for (double x : xValues) {
            System.out.printf("\nx = %.1f\n\n", x);
            System.out.println("Construcción del polinomio interpolado P(x):\n");

            StringBuilder polynomial = new StringBuilder();
            for (int i = 0; i < n; i++) {
                double term = points[i][1];
                StringBuilder termBuilder = new StringBuilder();

                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        if (termBuilder.length() > 0) {
                            termBuilder.append(" * ");
                        }
                        termBuilder.append("(x - ").append(points[j][0]).append(")");
                    }
                }

                if (i > 0) {
                    polynomial.append(" + ");
                }
                polynomial.append(String.format("%.1f * (%s)", term, termBuilder.toString()));
            }

            System.out.println("P(x) = " + polynomial);

            // Evaluación paso a paso
            System.out.println("\nPaso a paso:");
            for (int i = 0; i < n; i++) {
                double term = points[i][1];
                StringBuilder termBuilder = new StringBuilder();

                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        if (termBuilder.length() > 0) {
                            termBuilder.append(" * ");
                        }
                        termBuilder.append("(x - ").append(points[j][0]).append(")");
                    }
                }

                System.out.printf("f(x%d)L%d(x):\n", i, i);
                System.out.printf("%.1f * (%s)\n", term, termBuilder.toString());
            }

            // Mostrar polinomios individuales L_i(x)
            System.out.println("\nPolinomios individuales L_i(x):");
            for (int i = 0; i < n; i++) {
                System.out.printf("\nL%d(x):\n", i);
                double denominator = 1;
                StringBuilder numerator = new StringBuilder();
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        numerator.append("(x - ").append(points[j][0]).append(") ");
                        denominator *= (points[i][0] - points[j][0]);
                    }
                }

                System.out.printf("L%d(x) = %s / (%.1f)\n", i, numerator.toString(), denominator);

                String expandedNumerator = expandNumerator(points, i);
                System.out.printf("L%d(x) = %s / %.1f\n", i, expandedNumerator, denominator);

                String scaledNumerator = scaleNumerator(expandedNumerator, denominator);
                System.out.printf("L%d(x) = (1 / %.1f) * %s\n", i, denominator, scaledNumerator);
            }

            // Evaluación en x
            double result = evaluateLagrangePolynomial(x, points);
            System.out.printf("\nEvaluación en x = %.1f:\n", x);
            System.out.printf("P(%.1f) = %.6f\n", x, result);

            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine(); // Esperar Enter para continuar
        }

        scanner.close();
    }

    // Método para evaluar el polinomio interpolado de Lagrange en un valor x
    private static double evaluateLagrangePolynomial(double x, double[][] points) {
        int n = points.length;
        double result = 0;

        for (int i = 0; i < n; i++) {
            double term = points[i][1];
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term *= (x - points[j][0]) / (points[i][0] - points[j][0]);
                }
            }
            result += term;
        }

        return result;
    }

    // Método para expandir el numerador del polinomio L_i(x)
    private static String expandNumerator(double[][] points, int i) {
        int n = points.length;
        double[] coefficients = new double[n];
        coefficients[0] = 1; // Inicializa el término constante

        for (int j = 0; j < n; j++) {
            if (i != j) {
                for (int k = n - 1; k > 0; k--) {
                    coefficients[k] = coefficients[k] * -points[j][0] + (k > 0 ? coefficients[k - 1] : 0);
                }
                coefficients[0] *= -points[j][0];
            }
        }

        StringBuilder expanded = new StringBuilder();
        for (int k = n - 1; k >= 0; k--) {
            if (coefficients[k] != 0) {
                if (expanded.length() > 0) expanded.append(" + ");
                expanded.append(coefficients[k]).append("x^").append(k);
            }
        }

        return expanded.toString();
    }

    // Método para escalar el numerador del polinomio L_i(x) por el denominador
    private static String scaleNumerator(String expandedNumerator, double denominator) {
        String[] terms = expandedNumerator.split(" \\+ ");
        StringBuilder scaled = new StringBuilder();

        for (String term : terms) {
            if (term.contains("x^")) {
                int power = Integer.parseInt(term.split("\\^")[1].trim());
                double coeff = Double.parseDouble(term.split("x")[0].trim());
                scaled.append("(1 / ").append(denominator).append(") * ").append(coeff).append("x^").append(power);
            } else {
                double coeff = Double.parseDouble(term.trim());
                scaled.append("(1 / ").append(denominator).append(") * ").append(coeff);
            }
            scaled.append(" + ");
        }

        // Eliminar el último " + "
        if (scaled.length() > 0) {
            scaled.setLength(scaled.length() - 3);
        }

        return scaled.toString();
    }
}
