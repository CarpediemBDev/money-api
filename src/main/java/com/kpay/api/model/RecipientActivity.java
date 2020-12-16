package com.kpay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Component
@Entity
@Data
public class RecipientActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private BigInteger idx;
    @JsonIgnore
    private BigInteger sownActivityIdx;
    @JsonIgnore
    private String token;

    private long recipientId;

    private BigDecimal receiveAmount;

    @UpdateTimestamp//-> DB Update시에 자동으로 수정
    @JsonIgnore
    private LocalDateTime createdTime;

//    단방향 ManyToOne
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "sownActivityIdx" , insertable=false, updatable=false)
//    private SownActivity sownActivity;

}
