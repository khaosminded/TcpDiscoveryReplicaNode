package kvstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static kvstore.Protocol.Operation.*;
import static kvstore.Protocol.TYPE.*;

/**
 * TCP socket based client which is used to send requests to server
 *
 */
public class Client {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Protocol.Operation opt;
    private String serverResp;
    private Protocol.TYPE type;

    public Client(String addr, int port, Protocol.Operation opt, Protocol.TYPE type) {
        super();
        try {
            this.type = type;
            this.clientSocket = new Socket(addr, port);
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            this.opt = opt;
        } catch (IOException e) {
            System.out.println("Client exception caught:" + e.toString());
            System.out.println(e.getMessage());

        }
    }

    public String runClient(String key, String value) {
        try {

            switch (opt) {
                case EXIT:
                    out.print(EXIT.name());
                    this.serverResp = in.readLine();
                    break;
                case DEL:
                    if (type.equals(CLIENT)) {
                        out.print(DDELINIT.name());
                        out.print(key);
                        this.serverResp = in.readLine();
                    } else {
                        out.print(DEL.name());
                        out.print(key);
                        this.serverResp = in.readLine();
                    }
                    break;
                case GET:
                    out.print(GET.name());
                    out.print(key);
                    this.serverResp = in.readLine();
                    break;
                case STORE:
                    out.print(STORE.name());
                    char[] cbuf = new char[1024];
                    in.read(cbuf, 0, 1024);
                    this.serverResp = String.valueOf(cbuf);
                    break;
                case PUT:
                    if (type.equals(CLIENT)) {
                        out.print(DPUTINIT.name());
                    } else {
                        out.print(PUT.name());

                    }
                    out.print(key);
                    out.print(value);
                    this.serverResp = in.readLine();
                    break;
                case DDEL1:
                    out.print(DDEL1.name());
                    out.print(key);
                    this.serverResp = in.readLine();
                    break;
                case DDEL2:
                    out.print(DDEL2.name());
                    out.print(key);
                    this.serverResp = in.readLine();
                    break;
                case DDELABORT:
                    out.print(DDELABORT.name());
                    out.print(key);
                    this.serverResp = in.readLine();
                    break;
                case DPUT1:
                    out.print(DPUT1.name());
                    out.print(key);
                    out.print(value);
                    this.serverResp = in.readLine();
                    break;
                case DPUT2:
                    out.print(DPUT2.name());
                    out.print(key);
                    out.print(value);
                    this.serverResp = in.readLine();
                    break;
                case DPUTABORT:
                    out.print(DPUTABORT.name());
                    out.print(key);
                    out.print(value);
                    this.serverResp = in.readLine();
                    break;
                default:
                    break;
            }
            System.out.print(serverResp);
            return serverResp;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return "";
    }

    public void closeConnections() {
        try {
            if (out != null) {
                out.flush();
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
