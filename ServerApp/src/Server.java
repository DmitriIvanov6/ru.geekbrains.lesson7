import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port + "...");
            while (true) {
                System.out.println("Ждём нового клиента...");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void broadcastMessage(String message) throws IOException {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public boolean isNickBusy(String nickname) {
        for (ClientHandler clientHandler : clients) {
            if(clientHandler.getUsername().equals(nickname)) {
                return true;
            }
        }
        return false;
    }
    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) throws IOException {
        for (ClientHandler client : clients) {
            if(client.getUsername().equals(receiverUsername)) {
                client.sendMessage("От: " + sender.getUsername() + " Сообщение: " + message);
                sender.sendMessage("Пользователю: " + receiverUsername + " Сообщение: " + message);
                return;
            }
        }

        sender.sendMessage("Невозможно отправить сообщение пользователю: " + receiverUsername + ". Такого пользователя нет.\n");
    }
}
