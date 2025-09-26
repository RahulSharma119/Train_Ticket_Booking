package com.train.user.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class GeneratePublicKey {
	public static PrivateKey loadPrivateKey(String privateKeyPem) throws Exception {
        String privateKeyPEM = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    public static PublicKey derivePublicKey(PrivateKey privateKey) throws Exception {
        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                rsaPrivateKey.getModulus(),
                rsaPrivateKey.getPublicExponent()
        );
        return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
    }

    public static String toPemFormat(PublicKey publicKey) {
        String encoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" +
                encoded.replaceAll("(.{64})", "$1\n") +
                "\n-----END PUBLIC KEY-----";
    }

    public static void main(String[] args) throws Exception {
        // Example private key string (PKCS8 format with BEGIN/END)
        String privateKeyPem = """
                -----BEGIN PRIVATE KEY-----
                YOUR_PRIVATE_KEY_STRING_HERE
                -----END PRIVATE KEY-----
                """;

        PrivateKey privateKey = loadPrivateKey(privateKeyPem);
        PublicKey publicKey = derivePublicKey(privateKey);

        System.out.println(toPemFormat(publicKey));
    }
}
