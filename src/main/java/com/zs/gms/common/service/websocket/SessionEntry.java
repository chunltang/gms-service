package com.zs.gms.common.service.websocket;

import lombok.Data;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
public class SessionEntry {

    /**
     * session集合
     * */
    private  Set<Session> sessions=new CopyOnWriteArraySet<>();

    /**
     * 用户id
     * */
    private String key;

    public void addSession(Session session){
        sessions.add(session);
    }

    public boolean removeSession(Session session){
        if(sessions.contains(session)){
            sessions.remove(session);
            return true;
        }
        return false;
    }

}


