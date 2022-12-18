import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private User user; // myself
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final ClientGUI gui;
    private final ArrayList<String> otherUsers = new ArrayList<>();
    private boolean closed = false;
    public Client(String username, int port) {
        try {
            socket = new Socket("localhost", port);
            String address = InetAddress.getLocalHost().getHostAddress();
            user = new User(username, address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            writer = new BufferedWriter(osw);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(isr);
        } catch (IOException e) {
            close();
        }
        gui = new ClientGUI(username, this);
        gui.setVisible(true);
    }

    public void sendInitMessage() {
        sendAMessage(user.name);
    }

    public void sendAMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            close();
        }
    }
    public void detectNewUser(String message) {
        int len = message.length();
        if (len >= 14 && message.startsWith("[服务器提示]")) {
            String username = message.substring(8, len - 6);
            if (!otherUsers.contains(username)) {
                if (message.endsWith("进入了聊天室")) {
                    otherUsers.add(username);
                    System.out.println("new username: " + username);
                    gui.setUsers(otherUsers);
                } else if (message.endsWith("离开了聊天室")) {
                    otherUsers.remove(username);
                    gui.setUsers(otherUsers);
                }
            }
        } else {
            String username = message.split(": ", 2)[0];
            System.out.println("new username: " + username);
            if (!otherUsers.contains(username)) {
                otherUsers.add(username);
                System.out.println("new username: " + username);
                gui.setUsers(otherUsers);
            }
        }
    }

    public void listenMessages() {
        new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    String message = reader.readLine();
                    gui.receiveMessage(message);
                    System.out.println(message);
                    detectNewUser(message);
                } catch (IOException e) {
                    close();
                }
            }
        }).start();
    }

    public void close() {
        if (!closed) closed = true;
        else return;
        System.out.println("bye~");
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("请输入你的用户名");
        final int port = 11511;
        Client client = new Client(username, port);
        client.listenMessages();
        client.sendInitMessage();
    }
}
