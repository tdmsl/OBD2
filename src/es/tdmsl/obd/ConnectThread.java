package es.tdmsl.obd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Manu on 23/04/2016.
 */
public class ConnectThread extends Thread {
    private  BluetoothSocket mmSocket;
    private  BluetoothDevice mmDevice;
    private Activity activity;
    private TextView info;

    private Handler puente = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //STRinfo = STRinfo + "\nSocket:" + socket.getRemoteDevice().getName()+" conectado";

           // info.setText("\n"+info.getText()+msg.obj.toString());
            Toast.makeText(activity,"conectando "+ mmSocket.getRemoteDevice().getName(), Toast.LENGTH_SHORT).show();

        }
    };

    public ConnectThread(Activity activity,BluetoothDevice device, TextView info) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice   = device;
        this.activity = activity;
        this.info=info;
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        /*try {
            // MY_UUID is the app's UUID string, also used by the server code
            //tmp = device.createRfcommSocketToServiceRecord(uuid);
            tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) { }
        mmSocket = tmp;*/
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        BluetoothAdapter mBluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket =  mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            //Toast.makeText(activity,"conectando"+ mmSocket.toString(), Toast.LENGTH_SHORT).show();
            //info.setText("conectado");
            Message msg = new Message();
            msg.obj = "conectando";
            puente.sendMessage(msg);


        } catch (IOException connectException) {
            Message msg = new Message();
            msg.obj = "erro conexion";
            puente.sendMessage(msg);
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }


        // Do work to manage the connection (in a separate thread)
       
        manageConnectedSocket(mmSocket);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
