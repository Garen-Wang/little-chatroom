import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    private final ServerGUI gui;
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        gui = new ServerGUI();
        gui.setVisible(true);
    }

    public void start() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + ":" + socket.getPort());
                ClientHandler handler = new ClientHandler(socket, gui);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            close();
        }
    }

    public void close() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(11511);
        Server server = new Server(serverSocket);
        server.start();
    }
}
