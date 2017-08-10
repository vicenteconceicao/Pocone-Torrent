package model;

/**
 * Created by horgun on 10/08/17.
 */

public class PoconeTorrentFile {
    private String trackerAddress;
    private int trackerPort;
    private String filename;
    private int size;
    private String hash;

    public PoconeTorrentFile() {
    }

    public PoconeTorrentFile(String trackerAddress, int trackerPort, String filename, int size, String hash) {
        this.trackerAddress = trackerAddress;
        this.trackerPort = trackerPort;
        this.filename = filename;
        this.size = size;
        this.hash = hash;
    }

    public String getTrackerAddress() {
        return trackerAddress;
    }

    public void setTrackerAddress(String trackerAddress) {
        this.trackerAddress = trackerAddress;
    }

    public int getTrackerPort() {
        return trackerPort;
    }

    public void setTrackerPort(int trackerPort) {
        this.trackerPort = trackerPort;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
