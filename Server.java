import java.io.*;
import java.net.*;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    protected static Vector<ClientHandler> clientList = new Vector<ClientHandler>();
    protected static int clientCount = 0;

    public static void main(String[] args) throws Exception {
        ServerSocket sersock = new ServerSocket(3000);
        Socket sock;

        System.out.println("Server  Initialized...");

        while (true) {
            sock = null;
            try {
                sock = sersock.accept();
                System.out.println("New client connecting on: " + sock);

                DataInputStream dis = new DataInputStream(sock.getInputStream());
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

                String username = dis.readUTF();

                ClientHandler mtch = new ClientHandler(sock, username, dis, dos);
                Thread t = new Thread(mtch);

                clientList.add(mtch);

                t.start();
                clientCount++;

                System.out.println(username +" started...");
            } catch (Exception e) {
                sock.close();
                e.printStackTrace();
            }
        }
    }
}

class ClientHandler implements Runnable {
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
        this.isloggedin = true;
    }

    @Override
    public void run() {
        try {
            dos.writeUTF("Connected...");

            String received;
            Date date = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy'@'hh:mm:ssa");

            while(true) {
                received = dis.readUTF();

                boolean flag = (received.split(" ")[4].toLowerCase().equals("quit"));
                if(flag) {
                    System.out.println("executing");
                    String clientRemove = received.split(" ")[2];
                    System.out.println("Client quit: " + clientRemove);
                    this.isloggedin = false;
                    //this.s.close();
                    int index = 0;
                    for(ClientHandler mc : Server.clientList){
                        if(mc.name.equals(clientRemove)){
                            index = Server.clientList.indexOf(mc);
                            break;
                        }
                    }
                    Server.clientList.remove(index);

                    for (ClientHandler mc : Server.clientList) {
                        mc.dos.writeUTF(ft.format(date) + " : ** Announcement ** : " + clientRemove + " has left the room...");
                    }
                    break;
                }

                for (ClientHandler mc : Server.clientList) {
                    mc.dos.writeUTF(received);
                }

                String clients = "";
                for (ClientHandler mc : Server.clientList) {
                    clients = clients + ", " + mc.name ;
                }
                System.out.println(clients);
                clients = "";
            }

            this.dis.close();
            this.dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}