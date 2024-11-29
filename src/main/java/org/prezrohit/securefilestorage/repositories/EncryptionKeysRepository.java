package org.prezrohit.securefilestorage.repositories;

import org.prezrohit.securefilestorage.entities.EncryptionKeys;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncryptionKeysRepository extends JpaRepository<EncryptionKeys, Long> {
}
