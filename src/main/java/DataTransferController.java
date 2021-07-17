import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DataTransferController {

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(7755);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("client connected");
                makeThread(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeThread(Socket socket) {
        new Thread(() -> {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                processInputOutput(dataInputStream, dataOutputStream);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void processInputOutput(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        while (true) {
            String input;
            try {
                input = dataInputStream.readUTF();
                String result = InputOutput.process(input);
                if (result.equals("break"))
                    break;
                dataOutputStream.writeUTF(result);
                dataOutputStream.flush();
            } catch (IOException e) {
                System.out.println("client disconnected");
                break;
            }
        }
    }

}
