import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket sock = new Socket("127.0.0.1", 3000);

        DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
        DataInputStream dis = new DataInputStream(sock.getInputStream());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter username: ");
        String name = br.readLine();
        System.out.println("Welcome " + name + "!!!");
        System.out.println("Connecting...");
        dos.writeUTF(name);

        String connected = dis.readUTF();
        System.out.println(connected);
        System.out.println("Enter quit/QUIT to exit...");
        System.out.println("You may begin messaging...");

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy'@'hh:mm:ssa");

                while (true) {
                    try {
                        String msg = br.readLine();
                        String msgOut = ft.format(date) + " : " + name + " : " + msg;
                        dos.writeUTF(msgOut);
                        if (msg.toLowerCase().equals("quit")) {
                            sock.close();
                            System.out.println("Logged Out");
                            System.exit(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String msg = dis.readUTF();
                        System.out.println(msg);
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