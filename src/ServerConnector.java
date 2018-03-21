import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnector {

    private int port;
    private int cash = 3000;
    static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    public ServerConnector(int port) {
        this.port = port;
    }

    public void startServer(){

        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Waiting for a client...");

            while (true) {

                Socket socket = ss.accept();
                executeIt.execute(new ClientWorkThread(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientWorkThread extends Thread {

        private Socket socket;
        private boolean checker = false;

        public ClientWorkThread(Socket mainSocket) {
            socket = mainSocket;
        }

        public void run() {

            try {

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.println("\nGot a client");

                String line = in.readUTF();
                if (line.equals("Dad 123456")) {

                    checker = true;
                    System.out.println("\nI know him");
                    out.writeUTF("Welcome");
                    out.flush();
                    while (checker) {
                        line = in.readUTF();
                        System.out.println("\nThe dumb client just sent me this line : " + line);
                        switch (line) {
                            case "1": {

                                out.writeUTF("Goodbye");
                                out.flush();
                                System.out.println("Already he disconnected");
                                checker = false;
                                break;
                            }
                            case "2": {

                                out.writeUTF("Your cash: " + String.valueOf(cash));
                                out.flush();
                                System.out.println("Send cash: " + cash);
                                break;
                            }
                            case "3": {

                                out.writeUTF("");
                                out.flush();
                                line = in.readUTF();

                                try {
                                    int money = Integer.parseInt(line);
                                    System.out.println("He wont: " + money + " money");

                                    if (money <= cash) {

                                        cash -= money;
                                        System.out.println("Send it");
                                        out.writeUTF("You can take it. Now on your cash: " + String.valueOf(cash));
                                        out.flush();
                                    } else {

                                        System.out.println("No such amount");
                                        out.writeUTF("You haven't such amount");
                                        out.flush();
                                    }
                                } catch (NumberFormatException e) {

                                    System.out.println("Incorrect value");
                                    out.writeUTF("Please try again");
                                }
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("Connection error");
                    out.writeUTF("Wrong username or password");
                    out.flush();
                }

                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main (String [] args) {
        int portNumber = 6666;
        ServerConnector server = new ServerConnector(portNumber);
        server.startServer();
    }
}



