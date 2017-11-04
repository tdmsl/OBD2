package es.tdmsl.obd;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.*;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
//import es.tdmsl.obd.*;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

/**
 * Created by Manu on 25/10/2015.
 */
public class Bluetooth extends BroadcastReceiver implements Runnable {
    private Activity activity;
    private String STRinfo;
    private TextView info;
    ProgressDialog progress;
    private BluetoothAdapter btAdapter;
    private  BluetoothSocket socket ;
    private BluetoothDevice myDispositivo;
    ArrayList<BluetoothDevice> arrayDevices = new ArrayList<BluetoothDevice>();



    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public Bluetooth(Activity activity, Context context,TextView info) {
        this.activity = activity;
        this.info = info;
        btAdapter = getDefaultAdapter();
        configurarAdaptadorBluetooth();
        //registrarEventosBluetooth();
       // buscarDispositivos();



    }

    public void configurarAdaptadorBluetooth() {
        // Obtenemos el adaptador Bluetooth. Si es NULL, significara que el
        // dispositivo no posee Bluetooth, por lo que deshabilitamos el boton
        // encargado de activar/desactivar esta caracteristica.
        BluetoothAdapter btAdapter = getDefaultAdapter();
        if (btAdapter == null) {
            new AlertDialog.Builder(activity)
                    .setTitle("")
                    .setMessage("")
                    .setPositiveButton("", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        // Comprobamos si el Bluetooth esta activo y cambiamos el texto del
        // boton dependiendo del estado.
        if (btAdapter.isEnabled()) {
            STRinfo = "Bluetooth ya estaba activado";
            //info.setText(STRinfo);
            Toast.makeText(activity, "Bluetooth ya estaba activado", Toast.LENGTH_SHORT).show();


        } else {
            //btnBluetooth.setText("Activar Bluetooth");
            //************************
            // Lanzamos el Intent que mostrara la interfaz de activacion del
            // Bluetooth. La respuesta de este Intent se manejara en el
            // metodo
            // onActivityResult
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);//REQUEST_ENABLE_BT
            //************************
           // STRinfo = "Bluetooth ha sido activado";
            //info.setText(STRinfo);
            Toast.makeText(activity, "Bluetooth ha sido activado", Toast.LENGTH_SHORT).show();

        }
        registrarEventosBluetooth();
    }

    @Override
    public void run() {
       Log.i("", "ejecutando el metodo run");

        //enviar comando
        //String comando = cmd ;
       /* String comando = "ttz" ;
        int data;

        try {
            OutputStream os = socket.getOutputStream();
            *//*STRinfo = STRinfo+"\nEscribiendo codigo de inicializaciom "+STRinicializacion;
            info.setText(STRinfo);*//*

            for(int i=0;i<comando.length();i++){
                data= comando.codePointAt(i);
           *//* Log.i("data ",""+data);
            STRinfo = STRinfo+"\n"+data;
            info.setText(STRinfo);*//*
                os.write(data);//Se escribe en el Puerto serie
                if((i+1) == comando.length()){
                    os.write(13);
                *//*Date temps =  new Date();
                while((timer()-temps.getTime())<3000){}
                STRinfo = STRinfo+"\n3000";
                info.setText(STRinfo);*//*
                   /////////// recibir(tv_respuesta);
                    //Solamente necessita el retorno de carro,
                    // sin el salto de linea.
                }
            }
        } catch (IOException e) {
            Log.i("error", "" + e);
        }*/


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            //Solicitamos la informacion extra del intent etiquetada como BluetoothAdapter.EXTRA_STATE
            //El segundo parametro indicara el valor por defecto que se obtendra si el dato extra no existe
            final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (estado) {
                // Apagado
                case BluetoothAdapter.STATE_OFF: {
                    configurarAdaptadorBluetooth();

                    break;
                }

                // Encendido
                case BluetoothAdapter.STATE_ON: {
                    // Lanzamos un Intent de solicitud de visibilidad Bluetooth, al que añadimos un par
                    // clave-valor que indicara la duracion de este estado, en este caso 120 segundos
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                    context.startActivity(discoverableIntent);

                    break;
                }
                default:
                    break;
            }

        }
        ///////////////////////////////////////////////////////////////////////
        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            //Extraemos el dispositivo del intent mediante la clave BluetoothDevice.EXTRA_DEVICE
            BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Acciones a realizar al descubrir un nuevo dispositivo
            // Si el array no ha sido aun inicializado, lo instanciamos
            //arrayDevices = new ArrayList<BluetoothDevice>();
            String str = dispositivo.getName();
            arrayDevices.add(dispositivo);

            if ("OBDII".equals(str)) {
                btAdapter.cancelDiscovery();
                progress.dismiss();
                Toast.makeText(activity.getBaseContext(), "Encontrado  " + str, Toast.LENGTH_SHORT).show();
                STRinfo = STRinfo + "\nEncontrado el dispositivo:\n" + dispositivo.getName();
                info.setText(STRinfo);
            }
        }

        //Log.i("******",arrayDevices.get(arrayDevices.size()-1).toString());
        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
            if (arrayDevices.size() != 0) {
                myDispositivo = arrayDevices.get(arrayDevices.size() - 1);
                Log.i("ACTIONFINISHED", myDispositivo.getName());
                //Log.i("ACTIONFINISHED", btAdapter.getRemoteDevice(arrayDevices.get(arrayDevices.size() - 1)).toString());
                if ("OBDII".equals(myDispositivo.getName())) {//OBDII
                    progress.dismiss();
                    //conectar();
                    ConnectThread connectThread=new ConnectThread(activity,myDispositivo,info);
                    connectThread.start();
                } else {
                    new AlertDialog.Builder(activity)
                            .setTitle("Error")
                            .setMessage("No se encuentra el interface ELM3272")
                            .setPositiveButton("Aceptar", null)
                            .show();
                    progress.dismiss();
                }
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle("Error")
                        .setMessage("No se encuentra ningun dispositivo")
                        .setPositiveButton("Aceptar", null)
                        .show();
                progress.dismiss();
            }
        }

    }

    public void conectar() {
        // conectar con el dispositivo seleccionado:
        //btAdapter = BluetoothAdapter.getDefaultAdapter();
        //dispositivo = btAdapter.getRemoteDevice(deviceAddress);
       // UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//OK
          UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService
        try {
            socket = myDispositivo.createInsecureRfcommSocketToServiceRecord(uuid);

           /* Toast.makeText(g,
                    "" + myDispositivo + "---" + socket,
                    Toast.LENGTH_LONG).show();*/
            STRinfo = STRinfo + "\nSocket:" + socket.getRemoteDevice().getName()+" conectado";
            info.setText(STRinfo);
            //socket.isConnected()
            socket.connect();
            STRinfo = STRinfo + "\nEn la Direccion " + socket.getRemoteDevice().getAddress()+"";
            info.setText(STRinfo);
            //enviar();
            //recibir();
            // start command execution
            // new Handler().post(mQueueCommands);
            // muestraProgreso();
        } catch (IOException e) {
                Log.i("Error",""+e);
        }
    }



    public BluetoothSocket getSocket() {

        return socket;
    }

    public void registrarEventosBluetooth() {
        Toast.makeText(activity, "registrar eventos Bluetooth", Toast.LENGTH_SHORT).show();
        // Registramos el BroadcastReceiver que instanciamos previamente para
        // detectar los distintos eventos que queremos recibir

        IntentFilter filtro = new IntentFilter();
        filtro.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filtro.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filtro.addAction(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(this, filtro);

        buscarDispositivos();
    }



    public void buscarDispositivos() {
        System.out.println("buscar dispositivos pulsado");
        // Comprobamos si existe un descubrimiento en curso. En caso afirmativo,
        // se cancela.
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        // Iniciamos la busqueda de dispositivos y mostramos el mensaje de que
        // el proceso ha comenzado
        if (btAdapter.startDiscovery()) {
            Toast.makeText(activity.getBaseContext(),
                    "Iniciando búsqueda de dispositivos bluetooth",
                    Toast.LENGTH_SHORT).show();
            progress = new ProgressDialog(activity);
            progress.setMessage("Buscando interface ELM327 ");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

        } else {

        Toast.makeText(activity.getBaseContext(),
                "Error al iniciar búsqueda de dispositivos bluetooth",
                Toast.LENGTH_SHORT).show();
    }
    }

    public void enviar(String cmd, TextView tv_respuesta){
        progress = new ProgressDialog(activity);
        progress.setMessage("ejecutando comando ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        Log.i("", " progress.show();");
        Toast.makeText(activity.getBaseContext(),
                "progress.show()",
                Toast.LENGTH_SHORT).show();
        //enviar comando
        String comando = cmd ;

        int data;

        try {
            OutputStream os = socket.getOutputStream();
            /*STRinfo = STRinfo+"\nEscribiendo codigo de inicializaciom "+STRinicializacion;
            info.setText(STRinfo);*/

            for(int i=0;i<comando.length();i++){
                data= comando.codePointAt(i);
           /* Log.i("data ",""+data);
            STRinfo = STRinfo+"\n"+data;
            info.setText(STRinfo);*/
                os.write(data);//Se escribe en el Puerto serie
                if((i+1) == comando.length()){
                    os.write(13);
                /*Date temps =  new Date();
                while((timer()-temps.getTime())<3000){}
                STRinfo = STRinfo+"\n3000";
                info.setText(STRinfo);*/
                     recibir(tv_respuesta);
                    //Solamente necessita el retorno de carro,
                    // sin el salto de linea.
                }
            }
        } catch (IOException e) {
            Log.i("error", "" + e);
        }
         new Thread (this).start();


    }

    private void recibir(TextView tv_respuesta) {
        StringBuffer respuesta=new StringBuffer();
        Date temps =  new Date();
        while((timer()-temps.getTime())<3000){}
        char z;
        int reb=0;
        /*STRinfo = STRinfo+"\nRecibiendo";
        info.setText(STRinfo);*/

        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while((char) reb != (char)'>' ) {
                //Log.i("","char "+(char)reb);

            try {
                reb=is.read();//Se lee el Puerto serie
            } catch (IOException e) {
                e.printStackTrace();
            }

            z=(char) reb;
            respuesta.append(z);
            //Log.i("", "char " + (char) reb);
            //Log.i("", "respuesta " + respuesta.toString());
            }
           /* Log.i("TAG", "mensaje" + respuesta.toString()+"");
            STRinfo = STRinfo+"\n"+respuesta.toString();
            info.setText(STRinfo);*/
            tv_respuesta.setText(respuesta.toString());
            progress.dismiss();
            //socket.close();

    }

    public long timer(){
        Date  temps2 = new Date();
        return temps2.getTime();
    }



}


