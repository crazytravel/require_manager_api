package cc.iteck.rm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class RequireManagerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequireManagerApiApplication.class, args);
    }

}
