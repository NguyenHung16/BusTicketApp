package com.busapp.buss_api.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * =====================================================
 * BusApp Test Database Launcher
 * =====================================================
 *
 * Khởi chạy MySQL mock database (Testcontainers) và chạy ứng dụng Spring Boot
 * với cấu hình test.
 *
 * Cách dùng:
 *   1. Đảm bảo Docker đang chạy
 *   2. Chạy class này trực tiếp từ IDE (Run/Debug)
 *   3. Ứng dụng sẽ tự động:
 *      - Pull image mysql:8.0 nếu chưa có
 *      - Start container với schema + seed data
 *      - Chạy Spring Boot app kết nối vào database
 *
 * Sau khi stop (Ctrl+C):
 *   - Container sẽ tự dừng và remove
 *   - Muốn giữ container: docker start busapp-mysql-test
 *
 * =====================================================
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TestDatabaseLauncher {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   BusApp Test Database Launcher                    ║");
        System.out.println("║   MySQL 8.0 via Testcontainers                     ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Yêu cầu:");
        System.out.println("  - Docker Desktop đang chạy");
        System.out.println("  - Port 3307 và 8080 chưa bị chiếm dụng");
        System.out.println();

        // Chạy Spring Boot với profile "test"
        SpringApplication app = new SpringApplication(TestDatabaseLauncher.class);
        app.setAdditionalProfiles("test");

        ConfigurableApplicationContext context = app.run(args);

        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   BusApp đang chạy với Test Database!              ║");
        System.out.println("║   Swagger UI: http://localhost:8080/swagger-ui      ║");
        System.out.println("║   Database: mysql:3307 (Testcontainers)             ║");
        System.out.println("║   Stop: nhấn Ctrl+C để dừng                      ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        // Đăng ký shutdown hook để dọn container
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n Đang dừng Test Database...");
            SpringApplication.exit(context, () -> 0);
        }));
    }
}
