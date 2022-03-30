
/**
 * Start of the server in cmd : javac Serveur.java && java Serveur
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
            String message = c.lireEtDecoder();

            System.out.println("Serveur : " + message);

            outstream.write(c.coder("Recu : " + message));

            if (message.equals("fin")) {
                break;
            }
        }
        System.out.println("END");
        socket.close();
        serverSocket.close();
        instream.close();
        outstream.close();
    }

    /**
     * Premier message envoyé par le client
     */
    private static void recieveFirst() throws Exception {
        Connection c2 = new Connection("RSA", instream, outstream);
        c = new Connection("DES", instream, outstream);
        byte[] tab = c2.lire();
        c2.loadPublicKey(tab);
        try {
            byte[] newkey = (c.getPublicKey()).getEncoded();
            byte[] desCode = c2.coder(newkey);
            outstream.write(desCode);

            // Check the DES key is correct
            // Try to read the message from the client and write it back using the DES key
            String cle = c.lireEtDecoder();
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
            e.printStackTrace();
        }
    }

}