import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;

public class MinimalChatApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ChatWindow("User 1", 5555, 5556).setVisible(true);
            new ChatWindow("User 2", 5556, 5555).setVisible(true);
        });
    }
}

    class Message {
        private String content;
        private LocalDateTime timestamp;
        private boolean isSent;
        private String sender;

        public Message(String content, boolean isSent, String sender) {
            this.content = content;
            this.isSent = isSent;
            this.sender = sender;
            this.timestamp = LocalDateTime.now();
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public boolean isSent() {
            return isSent;
        }

        public String getSender() {
            return sender;
        }
    }

class MessageBubble extends JPanel {
    private static final Color SENT_COLOR = new Color(0, 128, 215);
    private static final Color RECEIVED_COLOR = new Color(245, 245, 245);
    private static final int CORNER_RADIUS = 12;
    private static final Font MESSAGE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TIME_FONT = new Font("Segoe UI", Font.PLAIN, 10);
    private static final Font SENDER_FONT = new Font("Segoe UI", Font.BOLD, 11);

    public MessageBubble(Message message) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setBackground(message.isSent() ? SENT_COLOR : RECEIVED_COLOR);
        bubble.setBorder(new RoundedBorder(CORNER_RADIUS, bubble.getBackground()));
        bubble.setBorder(BorderFactory.createCompoundBorder(
                bubble.getBorder(),
                BorderFactory.createEmptyBorder(12, 12, 8, 12)));

        JTextArea text = new JTextArea(message.getContent());
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBackground(message.isSent() ? SENT_COLOR : RECEIVED_COLOR);
        text.setForeground(message.isSent() ? Color.WHITE : Color.DARK_GRAY);
        text.setFont(MESSAGE_FONT);
        text.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JLabel time = new JLabel(
                message.getTimestamp().format(DateTimeFormatter.ofPattern("h:mm a")),
                SwingConstants.RIGHT);
        time.setFont(TIME_FONT);
        time.setForeground(message.isSent() ? new Color(200, 225, 255) : new Color(120, 120, 120));

        bubble.add(text, BorderLayout.CENTER);
        bubble.add(time, BorderLayout.SOUTH);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        if (!message.isSent()) {
            JLabel senderLabel = new JLabel(message.getSender());
            senderLabel.setFont(SENDER_FONT);
            senderLabel.setForeground(new Color(100, 100, 100));
            senderLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            container.add(senderLabel, BorderLayout.NORTH);
        }

        container.add(bubble, BorderLayout.CENTER);

        if (message.isSent()) {
            add(container, BorderLayout.LINE_END);
        } else {
            add(container, BorderLayout.LINE_START);
        }
    }

    private static class RoundedBorder implements Border {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 2, radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}

    class ChatWindow extends JFrame {
        private int receivePort;
        private int sendPort;
        private String username;
        private DefaultListModel<Message> messageListModel = new DefaultListModel<>();
        private JList<Message> messageList;
        private JTextField inputField;

    public ChatWindow(String title, int receivePort, int sendPort) {
        this.receivePort = receivePort;
        this.sendPort = sendPort;
        this.username = title;

        setTitle(title + " - Minimal Chat");
        setSize(380, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(receivePort == 5555 ? 200 : 650, 200);
        setMinimumSize(new Dimension(300, 400));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainPanel.setBackground(new Color(250, 250, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(60, 60, 60));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Message list
        messageList = new JList<>(messageListModel);
        messageList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                return new MessageBubble((Message) value);
            }
        });
        messageList.setBackground(new Color(250, 250, 250));
        messageList.setSelectionBackground(new Color(240, 240, 240));
        messageList.setSelectionForeground(Color.BLACK);
        messageList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(250, 250, 250));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        inputField = new JTextField();
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 220)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(110, 120, 215));
        sendButton.setForeground(Color.BLACK);
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Hover effect for button
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(new Color(0, 100, 190));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(new Color(0, 120, 215));
            }
        });

        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        inputWrapper.add(inputField, BorderLayout.CENTER);

        inputPanel.add(inputWrapper, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Send action
        ActionListener sendAction = e -> sendMessage();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        add(mainPanel);

        // Start message listener
        new Thread(this::startMessageListener).start();
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            // Add message to local window immediately
            Message msg = new Message(text, true, username);
            messageListModel.addElement(msg);
            inputField.setText("");
            messageList.ensureIndexIsVisible(messageListModel.getSize() - 1);

            // Send to other window
            try (Socket socket = new Socket("localhost", sendPort);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(text);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error sending message: " + e.getMessage());
            }
        }
    }

    private void startMessageListener() {
        try (ServerSocket serverSocket = new ServerSocket(receivePort)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()))) {

                    String message;
                    while ((message = in.readLine()) != null) {
                        final String msg = message;
                        SwingUtilities.invokeLater(() -> {
                            messageListModel.addElement(
                                    new Message(msg, false, username.equals("User 1") ? "User 2" : "User 1"));
                            messageList.ensureIndexIsVisible(messageListModel.getSize() - 1);
                        });
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Connection error: " + e.getMessage());
        }
    }
}