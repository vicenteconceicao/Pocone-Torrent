/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author horgun
 */
public class Peer implements Serializable{
    private String ip;
    private int port;

    public Peer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Peer){
            Peer p = (Peer) obj;
            return this.ip.equals(p.getIp()) && this.port == p.getPort();
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.ip);
        hash = 79 * hash + this.port;
        return hash;
    }
    
    
}
