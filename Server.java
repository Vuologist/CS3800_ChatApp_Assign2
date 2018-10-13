import java.io.*;
import java.net.*;
import java.util.Vector;

public class Server
{
    protected static Vector<ClientHandler> clientList = new Vector<ClientHandler>();
    protected static int clientCount = 0;

    public static void main(String[] args) throws Exception
    {
        ServerSocket sersock = new ServerSocket(3000);
        Socket sock;

        System.out.println("Server  Initialized...");

        while(true)
        {
            sock = null;
            try {
                sock = sersock.accept();
                System.out.println("A new client is connected: " + sock);

                DataInputStream dis = new DataInputStream(sock.getInputStream());
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

                System.out.println("Assigning new thread for this client");

                ClientHandler mtch = new ClientHandler(s, "client " + clientCount, dis, dos);
                Thread t = new Thread(mtch);

                clientList.add(mtch);
                t.start();
                clientCount++;

            } catch (Exception e) {
                sock.close();
                e.printStackTrace();
            }


            sock = sersock.accept();
            newClient(sock);


        }
    }

    private static void newClient (Socket s) {
        System.out.println("New client request received: " + s);
        try {

            String clientName = dis.readUTF();
            ClientHandler user = new ClientHandler(s, clientName, dis, dos);
            Thread t = new Thread(user);
            clientList.add(user);
            t.start();

            System.out.printf("Added \"%s\" to active client list.", clientName);

            clientCount++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable
{
    private String name;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private Socket s;
    private boolean isloggedin;

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {
        try {
            dos.writeUTF("Connected...");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}