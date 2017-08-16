package rmi;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.IServerListener;
import net.sf.lipermi.net.Server;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.Socket;

import br.ic.ufmt.quick.PoconeTorrent;
import model.FileTransfer;
import model.FileTransferInterface;
import model.HashManagerInterface;

/**
 * Created by horgun on 09/08/17.
 */

public class ServerRMI extends Service {
    public static int serverPort = 5000;
    public static String serverIP = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return super.onStartCommand(intent, flags, startId);
    }

    public void startServer(){
        CallHandler callHandler = new CallHandler();
        FileTransfer ft = new FileTransfer();
        try {
            callHandler.registerGlobal(FileTransferInterface.class, ft);
            Server server = new Server();
            server.bind(5000, callHandler);

            server.addServerListener(new IServerListener() {
                @Override
                public void clientConnected(Socket socket) {
                    Log.d("Conexao", "Client Connected: " + socket.getInetAddress());
                }

                @Override
                public void clientDisconnected(Socket socket) {
                    Log.d("Conexao", "Client Disconnected: " + socket.getInetAddress());
                }
            });
            Log.d("Conexao", "ServerRMI on!");

//            serverIP = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress();
            WifiManager wifiMgr = (WifiManager) PoconeTorrent.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();

            serverIP = Formatter.formatIpAddress(ip);

            Log.d("Conexao", "O ip do servidor: " + serverIP);

//            ft.getFile("2jmj7l5rSw0yVb/vlWAYkK/YBwk=");

        } catch (LipeRMIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
