package com.gymtracker.app.repository.jpa.token;

import com.gymtracker.app.entity.RefreshTokenEntity;
import com.gymtracker.app.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaRefreshTokenRepository extends CrudRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> getRefreshTokenEntityByTokenHash(String tokenHash);
    List<RefreshTokenEntity> user(UserEntity user);
}
