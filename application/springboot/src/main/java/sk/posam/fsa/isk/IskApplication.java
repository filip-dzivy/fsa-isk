package sk.posam.fsa.isk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
public class IskApplication {
    public static void main (String[] args) {
        SpringApplication.run(IskApplication.class, args);
    }
}
