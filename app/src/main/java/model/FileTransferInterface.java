package model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by horgun on 09/08/17.
 * Interface for transferring files
 */

public interface FileTransferInterface {

    public HashMap<String, Object> getFile(String hash, int offset) throws IOException;

}
