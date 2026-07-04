package com.localmart.login;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserTypeAndUserIdOrderByLoginTimeDesc(LoginHistory.UserType userType, Long userId);
}
