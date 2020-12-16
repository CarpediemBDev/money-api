package com.kpay.api.common;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class TokenUtil {

    private String token;

    public String getToken(){
        token = createUUID();
        return token;
    }

    /**
     * 3 Random String
     * @param
     * @return token
     */
    private String createUUID(){
        UUID temp = UUID.randomUUID();
        String strTemp = temp.toString().replace("-", "");
        return strTemp.substring(0,3); // substring(start, end);

    }

    /**
     * 3 Random String
     * English(Lower + Upper) + Number
     *
     * @param
     * @return token
     */
    private String createToken(){
        Random rd = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 3; i++) { // 3 Random String
            int index = rd.nextInt(3);
            switch (index){
                case 0:
                    sb.append((char)(rd.nextInt(26)) + 97); // Lower
                    break;
                case 1:
                    sb.append((char)(rd.nextInt(26)) + 65); // Upper
                    break;
                case 2:
                    sb.append(rd.nextInt(10));// Number
                    break;

            }
        }
        return sb.toString();
    }
}
