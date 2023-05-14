package com.learn.unidbg.middem.bili;


import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/* compiled from: BL */
/* loaded from: classes.dex */
public final class SignedQuery {

    /* renamed from: c */
    private static final char[] f106300c = "0123456789ABCDEF".toCharArray();

    public static final String FIELD_DELIMITER = "&";
    public static final String KEY_VALUE_DELIMITER = "=";


    /* renamed from: a */
    public final String f106301a;

    /* renamed from: b */
    public final String f106302b;

    public SignedQuery(String str, String str2) {
        this.f106301a = str;
        this.f106302b = str2;
    }

    /* renamed from: a */
    private static boolean m79205a(char c, String str) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || !((c < '0' || c > '9') && "-_.~".indexOf(c) == -1 && (str == null || str.indexOf(c) == -1));
    }

    /* renamed from: b */
    static String m79204b(String str) {
        return m79203c(str, null);
    }

    /* renamed from: c */
    static String m79203c(String str, String str2) {
        StringBuilder sb = null;
        if (str == null) {
            return null;
        }
        int length = str.length();
        int i = 0;
        while (i < length) {
            int i2 = i;
            while (i2 < length && m79205a(str.charAt(i2), str2)) {
                i2++;
            }
            if (i2 == length) {
                if (i == 0) {
                    return str;
                }
                sb.append((CharSequence) str, i, length);
                return sb.toString();
            }
            if (sb == null) {
                sb = new StringBuilder();
            }
            if (i2 > i) {
                sb.append((CharSequence) str, i, i2);
            }
            i = i2 + 1;
            while (i < length && !m79205a(str.charAt(i), str2)) {
                i++;
            }
            try {
                byte[] bytes = str.substring(i2, i).getBytes("UTF-8");
                int length2 = bytes.length;
                for (int i3 = 0; i3 < length2; i3++) {
                    sb.append('%');
                    sb.append(f106300c[(bytes[i3] & 240) >> 4]);
                    sb.append(f106300c[bytes[i3] & 15]);
                }
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        }
        return sb == null ? str : sb.toString();
    }




    /* renamed from: r */
    public static String m79203c(Map<String, String> map) {
        if (!(map instanceof SortedMap)) {
            map = new TreeMap(map);
        }
        StringBuilder sb = new StringBuilder(256);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!StringUtils.isEmpty(key)) {
                sb.append(m79204b(key));
                sb.append(KEY_VALUE_DELIMITER);
                String value = entry.getValue();
                sb.append(value == null ? "" : m79204b(value));
                sb.append(FIELD_DELIMITER);
            }
        }
        int length = sb.length();
        if (length > 0) {
            sb.deleteCharAt(length - 1);
        }
        if (length == 0) {
            return null;
        }
        return sb.toString();
    }

    public String toString() {
        String str = this.f106301a;
        if (str == null) {
            return "";
        }
        if (this.f106302b == null) {
            return str;
        }
        return this.f106301a + "&sign=" + this.f106302b;
    }
}