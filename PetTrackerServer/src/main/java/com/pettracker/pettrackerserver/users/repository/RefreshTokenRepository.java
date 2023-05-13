package com.pettracker.pettrackerserver.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pettracker.pettrackerserver.users.models.RefreshToken;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM refreshtoken WHERE user_id = ?1", nativeQuery = true)
	void deleteByUser(Long user_id);
}