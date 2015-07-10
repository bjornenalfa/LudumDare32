package server;

public class GameServer {
    
    static int SERVER_PORT = 9010;

    private static Server server;

    public GameServer() {
        server = new Server(SERVER_PORT);
        server.run();
    }

    private void update() {

    }

    public static void main() {
        new GameServer();
    }
}
