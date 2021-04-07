package server.auth;

import client.ClientChatAdapter;
import server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String name;
    private boolean isSubscribe = false;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    listen();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    private void listen() {
        try {
            doAuth();
            readMessage();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void doAuth() throws IOException {
        killAboutTime();
        while (true) {
            String input = in.readUTF();
            if (input.startsWith("-auth")) {
                String[] credentials = input.split("\\s");
                AuthEntry maybeAuthEntry = server.getAuthenticationService()
                        .findUserByCredentials(credentials[1], credentials[2]);
                if (maybeAuthEntry != null) {
                    if (server.isNicknameFree(maybeAuthEntry.getNickname())) {
                        sendMessage("CMD: auth is ok");
                        name = maybeAuthEntry.getNickname();
                        server.broadcast(name + " logged in.");
                        server.subscribe(this);
                        isSubscribe = true;
                        return;
                    } else {
                        sendMessage("Current user is already logged-in.");
                    }
                } else {
                    sendMessage("Unknown user. Incorrect login/password");
                }
            } else {
                sendMessage("Invalid authentication request.");
            }
        }
    }

    public String getName() {
        return name;
    }

    public void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            killMe(message);
            server.broadcast(name + ": " + message);
        }
    }
    private void killMe(String message){
        //Додумался только до этого
        if (message.equals("kill")){
            System.out.println("chek");
            server.broadcast(name + " logged out.");
            server.unsubscribe(this);
        }
    }
    private void killAboutTime() throws IOException{
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(120000);
                    if (!isSubscribe){
                        sendMessage("You are not active!!!");
                        sendMessage("We delete you ! Sorry...");
                        try {
                            in.close();
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException("SWW", e);
                }
            }
        }).start();
    }
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}