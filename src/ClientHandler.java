import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public String getAddressAndPort() {
        return socket.getInetAddress() + ":" + socket.getPort();
    }

    public String getUsername() {
        return username;
    }

    private String username;
    private ServerGUI gui;
    public ClientHandler(Socket socket, ServerGUI gui) {
        try {
            this.gui = gui;
            this.socket = socket;
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            writer = new BufferedWriter(osw);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(isr);
            username = reader.readLine();
            clientHandlers.add(this);
            gui.setOnlineUsers(clientHandlers);

            sendMessage("[服务器提示] " + username + "进入了聊天室");
            System.out.println(username + "进入了聊天室");
        } catch (IOException e) {
            close(this);
        }
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                String message = reader.readLine();
                if (message == null) break;
                sendMessage(message);
            }
        } catch (IOException ignored) {
        } finally {
            close(this);
        }
    }

    private void sendMessage(String message) {
        for (ClientHandler handler : clientHandlers) {
            try {
                if (!handler.username.equals(username)) {
                    handler.writer.write(message);
                    handler.writer.newLine();
                    handler.writer.flush();
                }
            } catch (IOException e) {
                close(handler);
            }
        }
    }

    private void close(ClientHandler handler) {
        sendMessage("[服务器提示] " + handler.username + "退出了聊天室");
        clientHandlers.remove(handler);
        gui.setOnlineUsers(clientHandlers);
        System.out.println(username + "离开了聊天室");
        try {
            if (handler.reader != null) handler.reader.close();
            if (handler.writer != null) handler.writer.close();
            if (handler.socket != null) handler.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
