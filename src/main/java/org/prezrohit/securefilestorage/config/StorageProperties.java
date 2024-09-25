package org.prezrohit.securefilestorage.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("storage")
public class StorageProperties {
    @Value("${storage.location}")
    private String location;

    // TODO - can add other sources here, like cloud storage
}
