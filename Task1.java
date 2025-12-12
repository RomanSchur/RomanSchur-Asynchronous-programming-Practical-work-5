import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
public class Task1{
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Введіть вагу товару (кг): ");
            double weightKg = scanner.nextDouble();
            System.out.print("Введіть ціну за кг (грн): ");
            double pricePerKg = scanner.nextDouble();
            System.out.print("Введіть знижку (%): ");
            double discountPercent = scanner.nextDouble();
            System.out.print("Введіть відстань доставки (км): ");
            double distanceKm = scanner.nextDouble();
            System.out.println("\nДані прийнято");
            long startTime = System.currentTimeMillis();

            CompletableFuture<Double> productTask = CompletableFuture.supplyAsync(() -> {
                System.out.println("[Потік Product] Обчислення вартості товару...");
                simulateProcessing(6);
                double basePrice = weightKg * pricePerKg;
                double discountAmount = basePrice * (discountPercent / 100);
                double finalPrice = basePrice - discountAmount;
                return finalPrice;
            });

            CompletableFuture<Double> shippingTask = CompletableFuture.supplyAsync(() -> {
                System.out.println("[Потік Shipping] Обчислення доставки...");
                simulateProcessing(2);
                double ratePerKm = (weightKg > 20) ? 15.0 : 10.0;
                double shippingCost = distanceKm * ratePerKm;
                return shippingCost;
            });

            CompletableFuture<Double> totalTask = productTask.thenCombine(shippingTask, (productPrice, shippingCost) -> {
                System.out.println("[Main]Об'єднання результатів...");
                System.out.printf("Вартість товару: %.2f грн%n", productPrice);
                System.out.printf("Вартість доставки: %.2f грн%n", shippingCost);
                return productPrice + shippingCost;
            });
            Double totalResult = totalTask.join();
            long endTime = System.currentTimeMillis();
            System.out.printf("ВСЬОГО ДО СПЛАТИ: %.2f грн%n", totalResult);
            System.out.println("=====================================");
            System.out.println("Час виконання програми: "+(endTime - startTime)+" мс");
        } catch (Exception e) {
            System.err.println("Помилка введення даних! Переконайтеся, що вводите числа (використовуйте кому або крапку залежно від налаштувань системи).");
        }
    }
    private static void simulateProcessing(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}