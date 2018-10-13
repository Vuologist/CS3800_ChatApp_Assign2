import java.io.*;
import java.net.*;

public class Client
{
    public static void main(String[] args) throws IOException
    {
        Socket sock = new Socket("127.0.0.1", 3000);

        DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
        DataInputStream dis = new DataInputStream(sock.getInputStream());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter username: ");
        String name = br.readLine();
        System.out.println("Welcome " + name+ "!!!");
        System.out.println("Connecting...");
        dos.writeUTF(name);

        String connected = dis.readUTF();
        System.out.println(connected);

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try{
                        String msg = br.readLine();
                        dos.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        String msg = dis.readUTF();
                        System.out.print(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}