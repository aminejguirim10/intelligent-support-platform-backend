package intership.intelligentsupportplatformbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@SpringBootApplication
public class IntelligentSupportPlatformBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelligentSupportPlatformBackendApplication.class, args);
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of("status", "UP");
    }

}
