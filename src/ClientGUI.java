import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ClientGUI extends JFrame {
    private final JSplitPane splitPane = new JSplitPane();
    private final JPanel leftPanel = new JPanel();
    private final JPanel rightPanel = new JPanel();
    private final JTextArea inputTextArea = new JTextArea();
    private final JButton sendButton = new JButton("发送");
    private final JPanel inputPanel = new JPanel();
    private final JPanel chatPanel = new JPanel();

    private final Client client;

    public ClientGUI(String username, Client client) {
        super(username);
        this.client = client;

        init(username);
    }

    private void init(String username) {
        setTitle(username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));

        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setMinimumSize(new Dimension(200, 0));
        splitPane.setLeftComponent(leftScrollPane);

        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        JScrollPane chatScrollPane = new JScrollPane(chatPanel);

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.add(inputTextArea);
        sendButton.setMaximumSize(new Dimension(0, 150));
        inputPanel.add(sendButton);
        inputPanel.setMinimumSize(new Dimension(600, 100));
        inputPanel.setPreferredSize(new Dimension(600, 150));
        inputPanel.setMaximumSize(new Dimension(600, 150));

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(chatScrollPane);
        rightPanel.add(inputPanel);
        splitPane.setRightComponent(rightPanel);

        sendButton.addActionListener(actionEvent -> {
            System.out.println("Send button pressed");
            String message = inputTextArea.getText().trim();
            client.sendAMessage(username + ": " + message);
            receiveMessage("me: " + message);
        });
        add(splitPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (client != null) client.close();
                System.exit(0);
            }
        });
    }

    public void receiveMessage(String message) {
        JTextArea textArea = new JTextArea(message);
        textArea.setLineWrap(true);
        chatPanel.add(textArea, BorderLayout.NORTH);
        chatPanel.revalidate();
    }

    public void setUsers(ArrayList<String> users) {
        leftPanel.removeAll();
        for (String username : users) {
            leftPanel.add(new JLabel(username));
        }
        leftPanel.repaint();
        leftPanel.revalidate();
    }

    public static void main(String[] args) {
        ClientGUI gui = new ClientGUI("username", null);
        gui.setVisible(true);
    }
}
