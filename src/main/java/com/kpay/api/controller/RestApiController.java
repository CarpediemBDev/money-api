package com.kpay.api.controller;

import com.kpay.api.handler.ApiResponseMessage;
import com.kpay.api.model.SownActivity;
import com.kpay.api.service.RestApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
class RestApiController {

    @Autowired
    private RestApiService restApiService;


    @PostMapping("/money")
    public ResponseEntity<ApiResponseMessage> moneySprinkling(
            @RequestHeader(value = "X-USER-ID") long userId,
            @RequestHeader(value = "X-ROOM-ID") String roomId,
            @RequestBody SownActivity sownActivity)  {


        sownActivity.setUserId(userId);
        sownActivity.setRoomId(roomId);


        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("token",restApiService.insertActivity(sownActivity));

        return ResponseEntity.ok(new ApiResponseMessage("Success", resultMap));
    }

    @PutMapping("/money/{token}")
    public ResponseEntity<ApiResponseMessage> reciveMoney(
            @RequestHeader(value = "X-USER-ID") long userId,
            @RequestHeader(value = "X-ROOM-ID") String roomId,
            @PathVariable String token
    ){
        Map<String, BigDecimal> resultMap = new HashMap();

        resultMap.put("receive_amount", restApiService.updateActivity(userId,roomId,token));
        return ResponseEntity.ok(new ApiResponseMessage("Success", resultMap));
    }

    @GetMapping("/money/{token}")
    public ResponseEntity<ApiResponseMessage> getMoneyList(
            @RequestHeader(value = "X-USER-ID") long userId,
            @RequestHeader(value = "X-ROOM-ID") String roomId,
            @PathVariable String token
    ){
        int days = 7; // 7일동안만 조회가 되도록 설정
        Map<String, Object> map = new HashMap();
        map.put("sownActivity",restApiService.getActivity(userId,roomId,token, days));
        return ResponseEntity.ok(new ApiResponseMessage("Success",map));
    }

}