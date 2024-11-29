package org.prezrohit.securefilestorage.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "encryption_keys")
public class EncryptionKeys {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] privateKey;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] publicKey;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] symmetricKey;

    public EncryptionKeys setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public EncryptionKeys setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public EncryptionKeys setSymmetricKey(byte[] symmetricKey) {
        this.symmetricKey = symmetricKey;
        return this;
    }
}

