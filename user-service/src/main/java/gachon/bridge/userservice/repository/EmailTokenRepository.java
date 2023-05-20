package gachon.bridge.userservice.repository;

import gachon.bridge.userservice.domain.EmailConfirmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTokenRepository extends JpaRepository<EmailConfirmToken,String> {
}