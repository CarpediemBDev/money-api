package com.kpay.api.repository;

import com.kpay.api.model.RecipientActivity;
import com.kpay.api.model.SownActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;



@Repository
public interface SownActivityRepository extends JpaRepository<SownActivity, BigInteger> {

    Optional<SownActivity> findByTokenAndRoomIdAndCreatedTimeAfter(String token, String roomId, LocalDateTime time);
    Optional<SownActivity> findByTokenAndUserIdAndCreatedTimeAfter(String token, long userId, LocalDateTime time);

}