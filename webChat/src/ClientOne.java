import client.ClientChatAdapter;

public class ClientOne {
    public static void main(String[] args) {
        new ClientChatAdapter("localhost", 8989);
    }
}
