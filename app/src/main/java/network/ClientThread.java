package network;

/**
 * Created by student on 19.05.2017.
 */


import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import general.Constants;
import general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private TextView httpTextView;

    private Socket socket;

    public ClientThread(String address, int port, TextView httpTextView) {
        this.address = address;
        this.port = port;
        this.httpTextView = httpTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(address);
            printWriter.flush();
            String httpResource;
            while ((httpResource = bufferedReader.readLine()) != null) {
                final String finalizedHttpResource = httpResource;
                httpTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        httpTextView.setText(finalizedHttpResource);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}