package io.qbbr.arduinocar;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class ConnectThread extends Thread {
    public final static int RECEIVE_MESSAGE = 1;

    private static final String ARDUINO_END_OF_LINE = "\r\n";

    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private ArrayList<Handler> handlerList;

    public ConnectThread(BluetoothDevice bluetoothDevice) {
        BluetoothSocket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(G.MY_UUID));
            Log.d(G.LOG_TAG, "Socket created");
        } catch (IOException e) {
            Log.d(G.LOG_TAG, "Could'n create an RFCOMM Socket: " + e.toString());
        }

        this.socket = socket;

        try {
            connect();
        } catch (IOException e) {
            Log.d(G.LOG_TAG, "Socket could'n connect: " + e.toString());
            Log.d(G.LOG_TAG, "Try again...");

            try {
                connect();
            } catch (IOException ex) {
                Log.d(G.LOG_TAG, "Socket could'n connect: " + ex.toString());
            }
        }

        if (socket.isConnected()) {
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                Log.d(G.LOG_TAG, "Socket IO Streams opened");
            } catch (IOException e) {
                Log.d(G.LOG_TAG, "Could'n open Socket IO streams: " + e.toString());
            }
        }

        this.inputStream = inputStream;
        this.outputStream = outputStream;

        handlerList = new ArrayList<>();
    }

    private void connect() throws IOException {
        Log.d(G.LOG_TAG, "Socket connecting...");
        socket.connect();
        Log.d(G.LOG_TAG, "Socket connected");
    }

    public boolean isConnected() {
        if (socket == null) {
            return false;
        }

        return socket.isConnected();
    }

    public void addHandler(Handler handler) {
        handlerList.add(handler);
    }

    @Override
    public void run() {
        super.run();

        byte[] packetBytes;
        int bytesAvailable;
        int bytesRead;
        String readMsg;
        StringBuilder sb = new StringBuilder();

        while (true) {
            try {
                bytesAvailable = inputStream.available();
                if (bytesAvailable > 0) {
                    packetBytes = new byte[bytesAvailable];
                    bytesRead = inputStream.read(packetBytes);
                    readMsg = new String(packetBytes, StandardCharsets.US_ASCII);
                    readMsg = readMsg.substring(0, bytesRead);

                    sb.append(readMsg);
                    int endOfLineIndex = sb.indexOf(ARDUINO_END_OF_LINE);
                    if (endOfLineIndex > 0) {
                        String data = sb.substring(0, endOfLineIndex);
                        for (Handler handler : handlerList) {
                            handler.obtainMessage(RECEIVE_MESSAGE, -1, -1, data).sendToTarget();
                        }
                        sb.delete(0, sb.length());
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    public boolean write(char data) {
        Log.d(G.LOG_TAG, "write data: " + data);

        try {
            outputStream.write(data);
            return true;
        } catch (IOException e) {
            if (!this.isAlive()) {
                this.cancel();
            }
            Log.d(G.LOG_TAG, "write error: " + e.getMessage());
        }
        return false;
    }

    public boolean write(String data) {
        try {
            byte[] bytes = data.getBytes();
            outputStream.write(data.getBytes());
            return true;
        } catch (IOException e) {
            if (!this.isAlive()) {
                this.cancel();
            }
            Log.d(G.LOG_TAG, "write error: " + e.getMessage());
        }
        return  false;
    }

    public void cancel() {
        try {
            Log.d(G.LOG_TAG, "Socket closing...");
            socket.close();
            Log.d(G.LOG_TAG, "Socket closed");
        } catch (IOException e) {
            Log.d(G.LOG_TAG, "Could'n close Socket: " + e.getMessage());
        }
    }
}
