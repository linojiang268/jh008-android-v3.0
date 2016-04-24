package com.gather.android.http;

import com.gather.android.Constant;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by Administrator on 2015/6/26.
 */
public class EncryptBuilder {

    private static final MediaType CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded");

    private final StringBuilder content = new StringBuilder();
    private final StringBuilder signString = new StringBuilder();

    /**
     * Add new key-value pair.
     */
    public EncryptBuilder add(String name, String value) {
        if (content.length() > 0) {
            content.append('&');
        }
        try {
            content.append(URLEncoder.encode(name, "UTF-8"))
                    .append('=')
                    .append(URLEncoder.encode(value, "UTF-8"));
            if (name.matches("^\\w+\\[\\d+\\]$")) {

            } else {
                if (signString.length() > 0) {
                    signString.append('&');
                }
                signString.append(URLEncoder.encode(name, "UTF-8"))
                        .append('=')
                        .append(URLEncoder.encode(value, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
        return this;
    }

    public RequestBody build() {
        if (content.length() == 0) {
            throw new IllegalStateException("Form encoded body must have at least one part.");
        }
        addSign();

        // Convert to bytes so RequestBody.create() doesn't add a charset to the content-type.
        byte[] contentBytes = content.toString().getBytes(Util.UTF_8);
        return RequestBody.create(CONTENT_TYPE, contentBytes);
    }

    private void addSign() {
        String key = "";
        if (Constant.SHOW_LOG) {
            key = "key=F1C86DC81A8CBCE4EEB9D219B68D6E66&";
        } else {
            key = "key=RDPTQSUB1AKR7LO9Y17BTK2YC0PBAJ0L&";
        }
        key += signString.toString();
        Logger.i(key);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(key.getBytes());
            StringBuffer buf = new StringBuffer();
            byte[] bits = md.digest();
            for (int i = 0; i < bits.length; i++) {
                int a = bits[i];
                if (a < 0)
                    a += 256;
                if (a < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(a));
            }
            add("sign", URLEncoder.encode(buf.toString(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
