package com.fortech.serviceapiimpl;

import com.fortech.model.entity.License;
import com.fortech.model.utils.GeneratedKey;
import com.fortech.model.utils.ValidationKey;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

@Service
public class LicenseEncryptionServiceImpl {

    private static final String KEY = "Fortech!2018";

    private LicenseEncryptionServiceImpl() {
    }

    public static String encrypt(final String text) {
        return Base64.encodeBase64String(xor(text.getBytes()));
    }

    public static String decrypt(final String hash) {
        try {
            return new String(xor(Base64.decodeBase64(hash.getBytes())), "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static byte[] xor(final byte[] input) {
        final byte[] output = new byte[input.length];
        final byte[] secret = KEY.getBytes();
        int spos = 0;
        for (int pos = 0; pos < input.length; ++pos) {
            output[pos] = (byte) (input[pos] ^ secret[spos]);
            spos += 1;
            if (spos >= secret.length) {
                spos = 0;
            }
        }
        return output;
    }

    public static License generate(License license) {
        GeneratedKey generatedKey = new GeneratedKey();
        ValidationKey validationKey = new ValidationKey();

        generatedKey.generateFromString(decrypt(license.getK1()));
        validationKey.generate(generatedKey, license.getStart(), license.getEnd());

        license.setK2(encrypt(validationKey.toString()));

        return license;
    }
}
