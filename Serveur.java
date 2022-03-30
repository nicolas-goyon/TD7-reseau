/**
 * Start of the server in cmd : javac Serveur.java && java Serveur
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Serveur {
    static ServerSocket serverSocket;
    static Socket socket;
    static Connection c;
    static DataInputStream instream;
    static DataOutputStream outstream;

    public static void main(String[] args) throws Exception {
        System.out.println("START");
        init();
        recieveFirst();
        System.out.println("Ready");
        while (true) {
            byte[] tab = new byte[1024];
            int nbRead = instream.read(tab, 0, 1024);
            byte[] tab2 = new byte[nbRead];
            System.arraycopy(tab, 0, tab2, 0, nbRead);
            String message = c.decoder(tab2);
            System.out.println("Serveur : " + message);
            outstream.write(c.coder("Recu : "+message));
            
            if (message.equals("fin")) {
                break;
            }
        }
        System.out.println("END");
        socket.close();
        serverSocket.close();
    }

/**
 * Premier message envoyé par le client
 */
    private static void recieveFirst() throws Exception {
        Connection c2 = new Connection("RSA");
        c = new Connection("DES");
        byte[] tab = new byte[1024];
        int nbRead = instream.read(tab, 0, 1024);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(tab));
        c2.setPublicKey(publicKey);
        try {
            byte[] newkey = (c.getPublicKey()).getEncoded();
            byte[] desCode = c2.coder(newkey);
            outstream.write(desCode);

            // Check the DES key is correct
            // Try to read the message from the client and write it back using the DES key
            byte[] tab2 = new byte[1024];
            int nbRead2 = instream.read(tab2, 0, 1024);
            tab = new byte[nbRead2];
            // Resize the array to the correct size
            System.arraycopy(tab2, 0, tab, 0, nbRead2);
            String cle = c.decoder(tab);
            // Print the message
            System.out.println(cle);
            byte[] renvoi = c.coder(cle);
            // Send back the message without changes
            outstream.write(renvoi);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Initialisation du serveur
     */
    private static void init() {

        try {
            serverSocket = new ServerSocket(6020);
            socket = serverSocket.accept();
            instream = new DataInputStream(socket.getInputStream());
            outstream = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


}