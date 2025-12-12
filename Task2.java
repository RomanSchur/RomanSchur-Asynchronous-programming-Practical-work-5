import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
class SoftwareProduct {
    String name;
    double price;
    int functionalityScore;
    int supportScore;
    public SoftwareProduct(String name, double price, int func, int supp) {
        this.name = name;
        this.price = price;
        this.functionalityScore = func;
        this.supportScore = supp;
    }
    public double getRating() {
        if (price == 0) return 1000;
        return (functionalityScore + supportScore) / price * 100;
    }
    @Override
    public String toString() {
        return String.format("%-15s | Ціна: %6.2f$ | Функц: %2d/10 | Підтримка: %2d/10 | Рейтинг: %5.2f",
                name, price, functionalityScore, supportScore, getRating());
    }
}

public class Task2{

    public static void main(String[] args) {
        System.out.println("===АНАЛІЗ РИНКУ ПЗ===");
        long startTime = System.currentTimeMillis();
        CompletableFuture<SoftwareProduct> soft1 = CompletableFuture.supplyAsync(() ->
                fetchData("SuperCode IDE", 150.0, 9, 8, 3));
        CompletableFuture<SoftwareProduct> soft2 = CompletableFuture.supplyAsync(() ->
                fetchData("DevStudio Pro", 200.0, 10, 9, 5));
        CompletableFuture<SoftwareProduct> soft3 = CompletableFuture.supplyAsync(() ->
                fetchData("FreeEdit Lite", 50.0, 6, 4, 2));

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(soft1, soft2, soft3);

        System.out.println("Очікування відповідей...");
        allTasks.join();

        long endTime = System.currentTimeMillis();
        System.out.println("\n---Всі дані отримано за " + (endTime - startTime) + " мс!---");

        SoftwareProduct p1 = soft1.join();
        SoftwareProduct p2 = soft2.join();
        SoftwareProduct p3 = soft3.join();

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);

        SoftwareProduct bestOption = Stream.of(p1, p2, p3)
                .max((a, b) -> Double.compare(a.getRating(), b.getRating()))
                .orElseThrow();
        System.out.println("\nРекомендований вибір: " + bestOption.name);
    }
    private static SoftwareProduct fetchData(String name, double price, int func, int supp, int delaySec) {
        try {
            TimeUnit.SECONDS.sleep(delaySec);
            System.out.println("[Завантажено] Дані про " + name);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return new SoftwareProduct(name, price, func, supp);
    }
}
