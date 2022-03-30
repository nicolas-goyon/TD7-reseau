
/**
 * Connexion
 */
import java.security.*;

import javax.crypto.*;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

import java.security.PublicKey;

import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import java.security.spec.X509EncodedKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Connection {
    Cipher cipher;
    private Key privateKey;
    private Key publicKey;
    DataInputStream instream;
    DataOutputStream outstream;
    Socket socket;

    public Connection(String algo) {
        try {
            init(algo);
            cipher = Cipher.getInstance(algo);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte[] coder(String message) {
        try {
            return coder(message.getBytes("UTF-8"));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;

    }

    public byte[] coder(byte[] message) {
        try {
            // Définit le mode : Cryptage ou Décryptage du texte
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            byte[] code = cipher.doFinal(message);
            return code;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;

    }

    public String decoder(byte[] message) {
        try {

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String code = new String(cipher.doFinal(message), "UTF-8");
            return code;
        } catch (Exception e) {
            // TODO: handle exception
        }

        return null;
    }

    public byte[] decoderDES(byte[] message,int taille) {
        try {

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] code = cipher.doFinal(message,0,taille);
            return code;
        } catch (Exception e) {
            // TODO: handle exception
        }

        return null;

    }

    public Key getPublicKey() {
        return this.publicKey;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }
    public Key getPrivateKey() {
        return privateKey;
    }
    public Cipher getCipher() {
        return cipher;
    }

    private void init(String algo) throws Exception {
        if (algo.equals("RSA")) {
            KeyPairGenerator keyGen;
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keys = keyGen.genKeyPair();
            privateKey = keys.getPrivate();
            publicKey = keys.getPublic();
            return;
        } else if (algo.equals("DES")) {
            KeyGenerator keygen;
            keygen = KeyGenerator.getInstance("DES");
            keygen.init(56);
            privateKey = keygen.generateKey();
            publicKey = privateKey;
        }
        // instream = new DataInputStream(socket.getInputStream());
        // outstream = new DataOutputStream(socket.getOutputStream());
    }

    public static PublicKey loadPublicKey(String publicKeyStr)
            throws Exception {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr));
            PublicKey pubKey = kf.generatePublic(keySpecX509);
            return pubKey;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("");
        } catch (InvalidKeySpecException e) {
            throw new Exception("");
        } catch (NullPointerException e) {
            throw new Exception("");
        }
    }

    public byte[] lire() throws IOException{
        byte[] tab = new byte[1024];
        int nbRead = instream.read(tab, 0, 1024);
        byte[] tab2 = new byte[nbRead];
        System.arraycopy(tab, 0, tab2, 0, nbRead);
        return tab2;
    }

}
