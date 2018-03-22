package club.nsdn.nyasamarailway.util;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2018.3.21.
 */
public class HashMap extends LinkedHashMap<String, String> {

    public HashMap() { super(); }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        forEach((key, value) -> {
            builder.append(key);
            builder.append(":");
            builder.append(value);
            builder.append(";");
        });

        return builder.toString();
    }

    public void fromString(String value) {
        if (value.isEmpty()) return;
        String[] pairs = value.split(";");
        if (pairs.length == 0) return;

        String[] pair;
        for (String p : pairs) {
            if (p.isEmpty()) continue;
            pair = p.split(":");
            if (pair.length != 2) continue;
            if (containsKey(pair[0])) remove(pair[0]);
            put(pair[0], pair[1]);
        }
    }

}
