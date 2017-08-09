package model;

import java.util.Date;

/**
 * Created by horgun on 09/08/17.
 * Representation of shared file
 */

public class SharedFile {
    private String hash;
    private Date data;
    private String filename;
    private int size;
    private int status;

    public SharedFile(String hash, Date data, String filename, int size, int status) {
        this.hash = hash;
        this.data = data;
        this.filename = filename;
        this.size = size;
        this.status = status;
    }

    public SharedFile() {

    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
