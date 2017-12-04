package kvstore;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    public static final store ST = new store();

    private ServerSocket serverSocket;
    private String Addr;
    private int portNumber;
    private String MBPaddr;
    private int MBPport;
    private static Map<String, String> mbpList;

    private Protocol.TYPE type;
    private static boolean then_exit = false;
    public static int Tcounter = 0;

    public Server(int portNumber, String MBPaddr, int MBPport) throws UnknownHostException {
        this.Addr = InetAddress.getLocalHost().getHostAddress();
        this.type = Protocol.TYPE.RKVSTORE;
        this.portNumber = portNumber;
        this.MBPaddr = MBPaddr;
        this.MBPport = MBPport;
        this.mbpList = new HashMap();

    }

    public Server(int portNumber) {
        this.portNumber = portNumber;
        this.type = Protocol.TYPE.MBPSTORE;
    }

    public static String exit() {
        then_exit = true;
        return "<the server then exits>";
    }

    private void publish() {
        Client client = new Client(MBPaddr, MBPport,
                Protocol.Operation.PUT, Protocol.TYPE.MBPSTORE);
        client.runClient(Addr, String.valueOf(portNumber));
    }

    private void unpublish() {
        Client client = new Client(MBPaddr, MBPport,
                Protocol.Operation.DEL, Protocol.TYPE.MBPSTORE);
        client.runClient(Addr, String.valueOf(portNumber));
    }

    private void refresh() {
        Client client = new Client(MBPaddr, MBPport,
                Protocol.Operation.STORE, Protocol.TYPE.MBPSTORE);
        String list = client.runClient(Addr, String.valueOf(portNumber));
        String L[] = list.split(":");
        mbpList.clear();
        for (int i = 0; i < L.length - 1; i += 4) {
            mbpList.put(L[i + 1], L[i + 3]);
        }
    }

    public static String[] broadcast(String key, String val, Protocol.Operation opt) {
        Iterator<String> that = mbpList.keySet().iterator();
        StringBuffer result = new StringBuffer();
        while (that.hasNext()) {

            String addr = that.next();
            String port = mbpList.get(addr);
            Client client = new Client(addr, Integer.parseInt(port),
                    opt, Protocol.TYPE.RKVSTORE);
            result.append(client.runClient(key, val) + "#");
        }
        return result.toString().split("#");
    }

    public void runTCP() throws IOException {

        if (type == Protocol.TYPE.RKVSTORE) {
            System.out.println("TCP kvstore server... trying to listen port: " + portNumber);
            class T extends Thread {

                public void run() {
                    refresh();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            T t = new T();
            t.run();
        } else {
            System.out.println("TCP membership server...");
        }
        publish();
        try {
            serverSocket = new ServerSocket(portNumber);
            while (!then_exit) {
                System.out.println("New request received[" + Tcounter + " running]");
                new ServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);

        } finally {
            unpublish();
            serverSocket.close();
        }
    }

}
