package com.broadleafcommerce.autoconfigure;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class CustomFunctions {

    public static String fromHex(String hex) {
        try {
            return new String(Hex.decodeHex(hex.toCharArray()));
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

}
