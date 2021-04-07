package client.network;


import java.io.*;
import java.net.Socket;

public class BasicChatNetwork implements ClientNetwork {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public BasicChatNetwork(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("SWW during establishing", e);
        }
    }
    private void SaveHistory(String massage) throws IOException {
        try {
            File history = new File("history.txt");
            if (!history.exists()) {
                System.out.println("Файла истории нет,создадим его");
                history.createNewFile();
            }
            PrintWriter fileWriter = new PrintWriter(new FileWriter(history, true));

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.newLine();
            bufferedWriter.write(massage);
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void send(String message) {
        try {
            out.writeUTF(message);
            SaveHistory(message);
        } catch (IOException e) {
            throw new RuntimeException("SWW during send", e);
        }
    }

    @Override
    public String receive() {
        try {
            return in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException("SWW during receive", e);
        }
    }
}
