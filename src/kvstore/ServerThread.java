/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kvstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static kvstore.Protocol.Operation.*;
import static kvstore.Protocol.TYPE.*;

public class ServerThread extends Thread {

    private final Socket clientSocket;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try (
                PrintWriter out
                = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));) {
            String opt = in.readLine();
            String response = "";
            if (opt.equals(PUT.name())) {
                String key = in.readLine();
                String val = in.readLine();
                Server.ST.put(key, val);
                response = "put key=" + key + "\n";
            } else if (opt.equals(GET.name())) {
                String key = in.readLine();
                String val = Server.ST.get(key);
                if (val == null) {
                    response = "invalid_key\n";
                } else {
                    response = "get key=" + key + " get val=" + val + "\n";
                }
            } else if (opt.equals(DEL.name())) {
                String key = in.readLine();
                Server.ST.del(key);
                response = "delete key=" + key + "\n";
            } else if (opt.equals(STORE.name())) {
                response = Server.ST.list();
            } else if (opt.equals(EXIT.name())) {
                response = Server.exit();
            } else if (opt.equals(DPUTINIT.name())) {
                String key = in.readLine();
                String val = in.readLine();
                response = LeaderDel(key, val);
            } else if (opt.equals(DDEL1.name())) {
                String key = in.readLine();
                response = Server.ST.ddel1(key);
            } else if (opt.equals(DDEL2.name())) {
                String key = in.readLine();
                response = Server.ST.ddel2(key).name();
            } else if (opt.equals(DDELABORT.name())) {
                String key = in.readLine();
                Server.ST.ddelabort(key);
                //**TODO*//
                response = Protocol.Operation.DDELABORT.name();
            } else if (opt.equals(DPUTINIT.name())) {
                String key = in.readLine();
                String val = in.readLine();
                response = LeaderPut(key, val);
            } else if (opt.equals(DPUT1.name())) {
                String key = in.readLine();
                String val = in.readLine();
                response = Server.ST.dput1(key, val);
            } else if (opt.equals(DPUT2.name())) {
                String key = in.readLine();
                String val = in.readLine();
                response = Server.ST.dput2(key, val).name();
            } else if (opt.equals(DPUTABORT.name())) {
                String key = in.readLine();
                String val = in.readLine();
                Server.ST.dputabort(key, val);
                response = DPUTABORT.name();
            } else {
                System.out.print("Wrong command received!");
            }
            out.print(response);

        } catch (IOException e) {
            System.out.println("Exception caught when listening for a connection");
            System.out.println(e.getMessage());
        }
        Server.Tcounter--;
    }

    /*
    *==MESSAGE FLOW==
    *Client->Leader->GenericNode
    *GenericNode.return->Leader->return->Client.commandline  
     */
    private String LeaderPut(String key, String val) {
        String[] result;
        result = Server.broadcast(key, val, DPUT1);
        int i;
        for (i = 0; i < result.length; i++) {
            if (result[i] != null) {
                if (result[i].equals(Protocol.ACK_MESSAGE_ABORT)) {
                    Server.broadcast(key, val, DPUTABORT);
                    return Protocol.ACK_MESSAGE_ABORT;
                }
            }
        }
        result = Server.broadcast(key, val, DPUT2);
        for (i = 0; i < result.length; i++) {
            if (result[i].equals(PUT.name())) {
                Server.ST.put(key, val);
            }
        }
        return "put key=" + key + "\n";

    }

    private String LeaderDel(String key, String val) {
        String[] result;
        result = Server.broadcast(key, val, DDEL1);
        int i;
        for (i = 0; i < result.length; i++) {
            if (result[i] != null) {
                if (result[i].equals(Protocol.ACK_MESSAGE_ABORT)) {
                    Server.broadcast(key, val, DDELABORT);
                    return Protocol.ACK_MESSAGE_ABORT;
                }
            }
        }
        result = Server.broadcast(key, val, DDEL2);
        for (i = 0; i < result.length; i++) {
            if (result[i].equals(DEL.name())) {
                Server.ST.del(key);
            }
        }
        return "delete key=" + key + "\n";
    }

}