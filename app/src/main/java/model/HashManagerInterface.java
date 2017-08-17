/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;

/**
 *
 * @author horgun
 */
public interface HashManagerInterface {
    
    public List<Peer> getPeers(String hash);
    
    public boolean shareFile(String hash);
    
    public boolean unshareFile(String hash);
    
}
