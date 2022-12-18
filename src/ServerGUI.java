import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ServerGUI extends JFrame {
    private final JPanel panel;
    private final JLabel label2;
    public ServerGUI() {
        setTitle("聊天室服务器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        JLabel label1 = new JLabel("服务器端连接客户", JLabel.CENTER);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        label2 = new JLabel("目前有0个客户在线", JLabel.CENTER);
        add(label1, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(label2, BorderLayout.SOUTH);
    }

    public void setOnlineUsers(ArrayList<ClientHandler> handlers) {
        panel.removeAll();
        for (ClientHandler handler :handlers) {
            panel.add(new JLabel(handler.getUsername() + " - "  + handler.getAddressAndPort()));
        }
        panel.repaint();
        panel.revalidate();
        label2.setText("目前有" + handlers.size() + "个客户在线");
    }

    public static void main(String[] args) {
        ServerGUI gui = new ServerGUI();
        gui.setVisible(true);
    }
}
