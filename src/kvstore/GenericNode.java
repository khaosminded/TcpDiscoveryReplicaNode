package kvstore;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static kvstore.Protocol.Operation.*;
import static kvstore.Protocol.TYPE.*;

public class GenericNode {

    /**
     * command processor
     *
     * @param args
     *
     */
    public static void main(String[] args) {
        int argLength = args.length;
        String role = null;
        String addr = null;
        String port = null;
        String mbpAddr = null;
        String mbpPort = null;
        int portNumber;
        Protocol.Operation opt = null;
        String key = null;
        String value = null;
        boolean wc = false;
        if (args.length < 1) {
            System.out.println("Wrong command!");
        }
        if (args[0].equals(Protocol.SERVER_ROLE)) {
            try {
                Server server;
                role = Protocol.SERVER_ROLE;
                switch (args.length) {
                    case 2:
                        portNumber = Integer.parseInt(args[1]);
                        server = new Server(portNumber);
                        break;
                    case 3:
                        portNumber = Integer.parseInt(args[1]);
                        mbpAddr = args[2];
                        mbpPort = "4410";
                        server = new Server(portNumber, mbpAddr, Integer.parseInt(mbpPort));
                        break;
                    case 4:
                        portNumber = Integer.parseInt(args[1]);
                        mbpAddr = args[2];
                        mbpPort = args[3];
                        server = new Server(portNumber, mbpAddr, Integer.parseInt(mbpPort));
                        break;
                    default:
                        System.out.println("Server Wrong command!");
                        return;
                }
                server.runTCP();
            } catch (IOException ex) {
                Logger.getLogger(GenericNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (args[0].equals("tc")) {
            switch (args.length) {
                case 4:
                    addr = args[1];
                    port = args[2];
                    if (EXIT.name().equalsIgnoreCase(args[3])) {
                        opt = EXIT;
                    } else if (STORE.name().equalsIgnoreCase(args[3])) {
                        opt = STORE;
                    } else {
                        wc = true;
                    }
                    break;
                case 5:
                    addr = args[1];
                    port = args[2];
                    key = args[4];
                    if (GET.name().equalsIgnoreCase(args[3])) {
                        opt = GET;
                    } else if (DEL.name().equalsIgnoreCase(args[3])) {
                        opt = DEL;
                    } else {
                        wc = true;
                    }
                    break;
                case 6:
                    addr = args[1];
                    port = args[2];
                    key = args[4];
                    value = args[5];
                    if (PUT.name().equalsIgnoreCase(args[3])) {
                        opt = PUT;
                    } else if (DEL.name().equalsIgnoreCase(args[3])) {
                        opt = DEL;
                    }else {
                        wc = true;
                    }
                    break;
                default:
                    wc = true;
                    break;

            }
            if (wc) {
                System.out.println("Client Wrong command!");
                return;
            }
            Client client = new Client(addr, Integer.parseInt(port), opt, CLIENT);
            client.runClient(key, value);
        }

    }

}
