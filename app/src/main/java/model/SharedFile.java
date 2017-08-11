package model;


import java.sql.Date;

/**
 * Created by horgun on 09/08/17.
 * Representation of shared file
 */

public class SharedFile {
    private String hash;
    private Date date;
    private String filename;
    private int size;
    private int status;

    public SharedFile(String hash, Date date, String filename, int size, int status) {
        this.hash = hash;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
