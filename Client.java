/**
 * Start of the server in cmd : javac Client.java && java Client localhost
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.logging.ConsoleHandler;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Client {
    static Socket socket;
    static BufferedReader ins;
    static PrintWriter outs;
    static Connection c;
    static DataInputStream instream;
    static DataOutputStream outstream;

    public static void main(String[] args) throws Exception {

        System.out.println("START");
        Console cons = System.console();
        init(args[0]);
        sendFirst();
        System.out.println("Ready");
        while (true) {
            String message = cons.readLine();
            outstream.write(c.coder(message));
            System.out.println("Client : " + message);
            if (message.equals("fin")) {
                break;
            }
            
            
            byte[] tab = new byte[1024];
            int nbRead = instream.read(tab, 0, 1024);
            byte[] tab2 = new byte[nbRead];
            System.arraycopy(tab, 0, tab2, 0, nbRead);
            message = c.decoder(tab2);
            System.out.println(message);
        }
        System.out.println("END");
        socket.close();
        ins.close();
        outs.close();

    }

    /**
     * Premier message envoyé par le client
     * @throws IOException
     */
    private static void sendFirst() throws IOException {
        c = new Connection("RSA");
        byte[] message = c.getPublicKey().getEncoded();
        outstream.write(message);
        try {
            byte[] tab = new byte[128];
            int nbRead = instream.read(tab, 0, 128);
            byte[] cle = c.decoderDES(tab,nbRead);
            SecretKey key = new SecretKeySpec(cle,0,cle.length, "DES");
            
            
            
            c = new Connection("DES");
            c.setPrivateKey(key);
            c.setPublicKey(key);

            // Try to send the message to the server using the DES key and read it back.
            message = c.coder("Hello world");
            outstream.write(message);
            byte[] tab2 = new byte[1024];
            int nbRead2 = instream.read(tab2, 0, 1024);
            tab = new byte[nbRead2];
            System.arraycopy(tab2, 0, tab, 0, nbRead2);
            String mess = c.decoder(tab);
            if(mess.equals("Hello world")){
                System.out.println("OK");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Initialisation du client
     * @param ip
     * @throws Exception
     */
    private static void init(String arg) {

        try {
            socket = new Socket(arg, 6020);
            instream = new DataInputStream(socket.getInputStream());
            outstream = new DataOutputStream(socket.getOutputStream());

            ins = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            outs = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
