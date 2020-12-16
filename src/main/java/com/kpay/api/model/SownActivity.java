package com.kpay.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;

@Component
@Entity
@Data
@NoArgsConstructor
public class SownActivity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) //-> sequence 자동증가
    @JsonIgnore
    private BigInteger idx;

    @JsonIgnore
    private String roomId;
    @JsonIgnore
    private long userId;

    @JsonIgnore
    private String token;

    private BigDecimal totalAmount;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int totalCount;

    @Transient //-> recivedAmount 필드는 JPA 필드 매핑시 제외
    private BigDecimal recivedAmount;

    @CreationTimestamp //-> DB Insert 시에 DateTime을 자동으로 입력
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    // 단방향 OneToMany
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "sownActivityIdx" , insertable=false, updatable=false)
    private Collection<RecipientActivity> recipientActivities;

    @Builder
    public SownActivity(BigDecimal totalAmount, int totalCount, BigDecimal recivedAmount){
        this.totalAmount = totalAmount;
        this.totalCount = totalCount;
        this.recivedAmount = recivedAmount;
    }
}
