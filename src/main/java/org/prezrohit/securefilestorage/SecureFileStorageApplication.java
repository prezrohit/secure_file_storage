package org.prezrohit.securefilestorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SecureFileStorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureFileStorageApplication.class, args);
	}

}
