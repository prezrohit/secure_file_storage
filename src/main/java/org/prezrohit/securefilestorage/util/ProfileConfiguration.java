package org.prezrohit.securefilestorage.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("environment")
public class ProfileConfiguration {
    private String name;
}
