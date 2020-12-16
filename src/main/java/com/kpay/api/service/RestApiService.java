package com.kpay.api.service;

import com.kpay.api.common.TokenUtil;
import com.kpay.api.handler.ApiException;
import com.kpay.api.handler.ErrorCode;
import com.kpay.api.model.RecipientActivity;
import com.kpay.api.model.SownActivity;
import com.kpay.api.repository.RecipientActivityRepository;
import com.kpay.api.repository.SownActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional()
public class RestApiService {

    @Autowired
    private SownActivityRepository sownRepository;

    @Autowired
    private RecipientActivityRepository recipientRepository;

    @Autowired
    private RecipientActivity recipientActivity;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 뿌리기
     *
     * @param sownActivity  뿌리려는 총 금액
     * @return
     */
    @Transactional
    public String insertActivity(SownActivity sownActivity)  {

        if(sownActivity.getTotalCount() < 1 || sownActivity.getTotalAmount().intValue() < 1){
            throw new ApiException(ErrorCode.E1000);
        }

        if(sownActivity.getTotalCount()  > sownActivity.getTotalAmount().intValue()){
            throw new ApiException(ErrorCode.E2000);
        }

        String token = tokenUtil.getToken();
        sownActivity.setToken(token);
        sownRepository.save(sownActivity);


        List<RecipientActivity> newList = createRow(sownActivity);
        recipientRepository.saveAll(newList);

        return token;
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BigDecimal updateActivity(long userId, String roomId, String token){

        LocalDateTime time = LocalDateTime.now();
        time = time.minusMinutes(10);
        Optional<SownActivity> optionalSownActivity = sownRepository.
                findByTokenAndRoomIdAndCreatedTimeAfter(token,roomId, time);

        optionalSownActivity.orElseThrow(()->new ApiException
                (ErrorCode.E3000));

        if(optionalSownActivity.get().getUserId() == userId){
            throw new ApiException
                    (ErrorCode.E4000);
        }

        BigInteger idx = optionalSownActivity.get().getIdx();

        // 이미 동일한 뿌리기건에 대하여 받은적이 있는지 체크한다.
        boolean isRecived = recipientRepository.
                findTop1BySownActivityIdxAndRecipientIdOrderByIdxAsc(idx,userId).isPresent();

        if(isRecived){
            throw new ApiException
                    (ErrorCode.E5000);
        }
        Optional<RecipientActivity> optionalRecipientActivity = recipientRepository.
                findTop1BySownActivityIdxAndRecipientIdOrderByIdxAsc(idx,0); // userId 0이면 받은적이 없는 경우임
        optionalRecipientActivity.orElseThrow(()->new ApiException
                (ErrorCode.E6000));

        //놀랍쥬? 이게 바로 update문이여유? JPA는 자동이여유
        optionalRecipientActivity.get().setRecipientId(userId);

        return optionalRecipientActivity.get().getReceiveAmount();
    }

    /**
     * 
     * @param userId 조회자
     * @param roomId 방번호
     * @param token  토큰
     * @return
     */
    public SownActivity  getActivity(long userId, String roomId, String token, int days){
        LocalDateTime time = LocalDateTime.now();
        time = time.minusDays(days);
        Optional<SownActivity> optionalSownActivity = sownRepository.
                findByTokenAndUserIdAndCreatedTimeAfter(token, userId, time);
        optionalSownActivity.orElseThrow(()->new ApiException
                (ErrorCode.E7000));

        SownActivity result = optionalSownActivity.get();
        // filter를 활용하여 recipentId가 0인 +유저(아무도 받지 않았을때)는 제외
        result.setRecipientActivities(result.getRecipientActivities().stream().
                filter(r->r.getRecipientId() > 0).collect(Collectors.toList()));
        // 받은금액
        int sumAmount = result.getRecipientActivities().stream().mapToInt(num -> num.getReceiveAmount().intValue()).sum();

        //받은금액을 vo에 넣고 리턴한다.
        BigDecimal amount = new BigDecimal(sumAmount);
        result.setRecivedAmount(amount);
        return result;

    }

    /**
     * 뿌릴 금액을 인원 수에 맞게 분배한다. 
     *
     * @param totalAmount 뿌리려는 총 금액
     * @param totalCount 뿌릴 인원 명수
     * @return
     */
    private BigDecimal[] getRandomAmount(BigDecimal totalAmount, int totalCount) {

        BigDecimal[] amounts = new BigDecimal[totalCount];
        int total = totalAmount.intValue();

        Random rand = new Random();
        for (int i = 0; i < amounts.length - 1; i++) {
            amounts[i] = new BigDecimal(rand.nextInt(total));
            total -= amounts[i].intValue();
        }
        amounts[amounts.length - 1] = new BigDecimal(total);

        return amounts;
    }

    /**
     * TotalCount 수 만큼 Recipient 테이블에 row 수를 미리 만들어 놓는다.
     *
     * @param sownActivity
     * @return
     */
    private List<RecipientActivity> createRow(SownActivity sownActivity){
        List<RecipientActivity> newList = new ArrayList<>();
        BigDecimal[] amounts = getRandomAmount(sownActivity.getTotalAmount(), sownActivity.getTotalCount());

        for(BigDecimal amount:amounts){
            RecipientActivity recipientActivity = new RecipientActivity();
            recipientActivity.setToken(sownActivity.getToken());
            recipientActivity.setReceiveAmount(amount);
            recipientActivity.setSownActivityIdx(sownActivity.getIdx());
            newList.add(recipientActivity);
        }
        return newList;
    }
}