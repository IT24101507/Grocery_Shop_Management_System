package com.ravindrastores.grocery_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(
    scanBasePackages = {
        "com.ravindrastores.grocery_system",
        "Ravindra.Stores.Ravindra_Stores_backend"
    }
)
@EnableJpaRepositories(basePackages = {
    "com.ravindrastores.grocery_system",
    "Ravindra.Stores.Ravindra_Stores_backend"
})
@EntityScan(basePackages = {
    "com.ravindrastores.grocery_system",
    "Ravindra.Stores.Ravindra_Stores_backend"
})
public class GrocerySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrocerySystemApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") 
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true);
            }
        };
    }
}
