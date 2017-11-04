package es.tdmsl.obd;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Main extends Activity {
    TextView info ;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        info = (TextView) findViewById(R.id.info);
        Bluetooth bluetooth = new Bluetooth(this,getBaseContext(),info);

        //bluetooth.configurarAdaptadorBluetooth();
       // bluetooth.registrarEventosBluetooth();
       // bluetooth.buscarDispositivos();
    }
}
