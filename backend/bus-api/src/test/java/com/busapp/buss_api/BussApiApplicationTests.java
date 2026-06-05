package com.busapp.buss_api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires MySQL database - run with TestDatabaseLauncher or docker-compose up")
class BussApiApplicationTests {

    @Test
    void contextLoads() {
        // Context load thành công = kết nối DB thành công
    }
}
