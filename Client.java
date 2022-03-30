
/**
 * Start of the server in cmd : javac Client.java && java Client localhost
 */
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Client {
    static Socket socket;
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

            message = c.lireEtDecoder();
            System.out.println(message);
        }
        System.out.println("END");
        socket.close();
        instream.close();
        outstream.close();

    }

    /**
     * Premier message envoyé par le client
     * 
     * @throws IOException
     */
    private static void sendFirst() throws IOException {
        c = new Connection("RSA", instream, outstream);
        byte[] message = c.getPublicKey().getEncoded();
        outstream.write(message);
        try {
            byte[] cle = c.lireDES();
            SecretKey key = new SecretKeySpec(cle, 0, cle.length, "DES");

            c = new Connection("DES", instream, outstream);
            c.setPrivateKey(key);
            c.setPublicKey(key);

            // Try to send the message to the server using the DES key and read it back.
            message = c.coder("Hello world");
            outstream.write(message);
            String mess = c.lireEtDecoder();
            if (mess.equals("Hello world")) {
                System.out.println("DES OK");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Initialisation du client
     * 
     * @param ip
     * @throws Exception
     */
    private static void init(String arg) {

        try {
            socket = new Socket(arg, 6020);
            instream = new DataInputStream(socket.getInputStream());
            outstream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
