import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadServer {

    public static void main(String[] args) throws IOException {

        ServerConfig config = ServerConfig.getInstance();

        ServerSocket serverSocket = null;

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        try {
            serverSocket = new ServerSocket(config.getPort());
        } catch (IOException e) {
            System.err.println(
                    "Could not listen on port: " + config.getPort() + ".");
            System.exit(-1);
        }

        while (true) {

            Socket socket = serverSocket.accept();

            threadPool.execute(
                    new UploadServerThread(socket));
        }
    }
}