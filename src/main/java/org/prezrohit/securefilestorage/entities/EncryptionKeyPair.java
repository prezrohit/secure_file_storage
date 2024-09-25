package org.prezrohit.securefilestorage.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "encryption_key_pairs")
public class EncryptionKeyPair {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(mappedBy = "encryptionKeyPairId")
    private User user;

    @Lob
    @Column(columnDefinition = "text")
    private String privateKey;

    @Lob
    @Column(columnDefinition = "text")
    private String publicKey;
}

