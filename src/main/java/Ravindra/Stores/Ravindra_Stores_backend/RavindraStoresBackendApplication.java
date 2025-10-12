package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Ravindra.Stores.Ravindra_Stores_backend.FileStorageService;

@SpringBootApplication
public class RavindraStoresBackendApplication implements CommandLineRunner {

	@Autowired
	FileStorageService fileStorageService;

	public static void main(String[] args) {
		SpringApplication.run(RavindraStoresBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fileStorageService.init();
	}

}
