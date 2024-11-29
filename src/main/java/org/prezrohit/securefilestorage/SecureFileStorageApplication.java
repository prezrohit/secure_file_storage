package org.prezrohit.securefilestorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EntityScan("org.prezrohit.securefilestorage.entities")
public class SecureFileStorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureFileStorageApplication.class, args);
	}

}
