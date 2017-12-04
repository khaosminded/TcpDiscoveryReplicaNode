/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kvstore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static kvstore.Protocol.Operation.*;

public class store {

    private static volatile Map<String, String> map = new ConcurrentHashMap<>();
    private static volatile Map<String, Boolean> lockMap = new ConcurrentHashMap<>();
    private Protocol.TYPE type;

    public store() {

    }

    public store(Protocol.TYPE type) {
        this.type = type;
    }

    public void put(String key, String val) {
        map.put(key, val);
    }

    public String get(String key) {
        return map.get(key);
    }

    public void del(String key) {
        if(map.get(key)!=null)
            map.remove(key);
    }

    public String dput1(String key, String val) {
        if (lockMap.get(key)!=null) {
            return Protocol.ACK_MESSAGE_ABORT;
        }
        lockMap.put(key, Boolean.TRUE);
        return Protocol.ACK_MESSAGE_SUCCESS;
    }

    public Protocol.Operation dput2(String key, String val) {
        map.put(key, val);
        lockMap.remove(key);
        return PUT;
    }

    public void dputabort(String key, String val) {
        lockMap.remove(key);
    }

    public String ddel1(String key) {
        if (lockMap.get(key)!=null) {
            return Protocol.ACK_MESSAGE_ABORT;
        }
        lockMap.put(key, Boolean.TRUE);
        return Protocol.ACK_MESSAGE_SUCCESS;
    }

    public Protocol.Operation ddel2(String key) {
        del(key);
        lockMap.remove(key);
        return DEL;
    }

    public void ddelabort(String key) {
        lockMap.remove(key);
    }

    public String list() {
        String list = "";
        Iterator<String> that = map.keySet().iterator();
        while (that.hasNext()) {
            String key = that.next();
            String val = map.get(key);
            list += "\nkey:" + key + ":value:" + val + ":";
        }
        return list;
    }

}
