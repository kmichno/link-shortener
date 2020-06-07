package com.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class LinkShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkShortenerApplication.class, args);
	}

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000");
//                registry.addMapping("/link/short").allowedOrigins("http://localhost:3000");
//                registry.addMapping("/link/delete/**").allowedOrigins("http://localhost:3000");
//                registry.addMapping("/link/**").allowedOrigins("http://localhost:3000");
//                registry.addMapping("/link/edit/**").allowedOrigins("http://localhost:3000");
                //                registry.addMapping("/links").allowedOrigins("http://192.168.100.9:3000");
                //                registry.addMapping("/link/short").allowedOrigins("http://192.168.100.9:3000");
                //                registry.addMapping("/link/delete/**").allowedOrigins("http://192.168.100.9:3000");
                //                registry.addMapping("/link/**").allowedOrigins("http://192.168.100.9:3000");
                //                registry.addMapping("/link/edit/**").allowedOrigins("http://192.168.100.9:3000");
            }
        };
    }

}
