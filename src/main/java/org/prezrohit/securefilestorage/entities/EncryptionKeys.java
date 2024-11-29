package org.prezrohit.securefilestorage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "encryption_keys")
public class EncryptionKeys {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "encryptionKeysId")
    @JsonIgnore
    private User user;

    @Lob
    @Column(columnDefinition = "text")
    private String privateKey;

    @Lob
    @Column(columnDefinition = "text")
    private String publicKey;

    @Lob
    @Column(columnDefinition = "text")
    private String symmetricKey;
}

