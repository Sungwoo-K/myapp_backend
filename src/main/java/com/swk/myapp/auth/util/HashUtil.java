package com.swk.myapp.auth.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class HashUtil {
    public String createHash(String cipherText) {

        return BCrypt
                .withDefaults()
                .hashToString(12, cipherText.toCharArray());
    }

    public boolean verifyHash(String ciphertext, String hash) {
        return BCrypt
                .verifyer()
                .verify(ciphertext.toCharArray(), hash)
                .verified;
    }
}
