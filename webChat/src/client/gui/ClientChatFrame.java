package client.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientChatFrame implements ChatFrameInteraction {
    private final ChatFrame chatFrame;

    public ClientChatFrame(Consumer<String> messageConsumer) {
        this.chatFrame = new ChatFrame("Client Chat v1.0", messageConsumer);
        try {
            loadHistory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void append(String message) {
        chatFrame.getChatArea().append(message);
        chatFrame.getChatArea().append("\n");
    }
    private void loadHistory() throws IOException {
        int posHistory = 100;
        File history = new File("history.txt");
        List<String> historyList = new ArrayList<>();
        FileInputStream in = new FileInputStream(history);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        String temp;
        while ((temp = bufferedReader.readLine()) != null) {
            historyList.add(temp);
        }

        if (historyList.size() > posHistory) {
            for (int i = historyList.size() - posHistory; i <= (historyList.size() - 1); i++) {
                chatFrame.getChatArea().append(historyList.get(i) + "\n");
            }
        } else {
            for (int i = 0; i < historyList.size(); i++) {
                chatFrame.getChatArea().append(historyList.get(i) + "\n");
            }
        }
    }
}
