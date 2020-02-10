import java.util.Base64;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class Security {
    public static String publicKey;
    private static Key privateKey;

    public static void generateRSA() {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair kP = keyGen.genKeyPair();
            publicKey = encoder.encodeToString(kP.getPublic().getEncoded());
            privateKey = kP.getPrivate();
        } catch (Exception e) {
            //e.printStackTrace();
			System.out.println("Erreur generation clef RSA!");
            System.exit(1);
        }
    }

	public static String encrypt(String mess, String publicKey) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pK = keyFactory.generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pK);
        byte[] encryptedBytes = cipher.doFinal(mess.getBytes());
        return new String(encoder.encode(encryptedBytes));
	}

	public static String decrypt(byte[] data) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey pK = keyFactory.generatePrivate(keySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, pK);
        byte[] decryptedBytes = decoder.decode(data);
        return new String(cipher.doFinal(decryptedBytes));
 	}
}