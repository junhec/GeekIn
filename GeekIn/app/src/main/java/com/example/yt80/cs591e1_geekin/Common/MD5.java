package com.example.yt80.cs591e1_geekin.Common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by haotianwu on 5/2/17.
 */

public class MD5 {

    public void MD5() {

    }

    public String generateMD5(String pwdToHash) {
        String generatedPWD = null;

        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(pwdToHash.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPWD = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        System.out.println(generatedPWD);
        return generatedPWD;
    }
}
