package com.panda.lp.utils;

import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ScrollPositionCodec {

    // Encode ScrollPosition to Base64 String
    public static String encode(ScrollPosition position) {
        if(position == null || position.isInitial()) return null;

        if(position instanceof KeysetScrollPosition keysetScrollPosition) {
            Map<String, Object> keys = keysetScrollPosition.getKeys();

            // Create a simple string representation: key1=value1,key2=value2
            StringBuilder sb = new StringBuilder();
            keys.forEach((key,value) -> {
                if(sb.length() > 0) sb.append(",");
                sb.append(key).append("=").append(value);
            });

            return Base64.getEncoder().encodeToString(sb.toString().getBytes());
        }

        return null;
    }

    // Decode Base64 string to ScrollPosition
    public static ScrollPosition decode(String encodedPosition) {
        if(encodedPosition == null || encodedPosition.isBlank()) {
            return ScrollPosition.keyset();
        }

        try{
            String decoded = new String(Base64.getDecoder().decode(encodedPosition)); // key1=value1, key2=value2
            String[] pairs = decoded.split(",");

            Map<String, Object> keys = new HashMap<>();

            for(String pair : pairs) { // string -> key1=value1
                String[] keyValue = pair.split("="); // key1,value1
                if(keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];

                    // Try to parse as Long first, then as String
                    try {
                        keys.put(key, Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        keys.put(key, value);
                    }
                }
            }

            return ScrollPosition.forward(keys);
        } catch (Exception e) {
            return ScrollPosition.keyset();
        }
    }
}
