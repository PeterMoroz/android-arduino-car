package io.qbbr.arduinocar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;

    TextView tvCurrentDevice;
    Button btnChooseDevice;
    Button btnManualDrive;
    Button btnAutomaticDrive;
    Button btnOpenRadar;
    Button btnBuildAreaMap;
    Button btnProgramPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        G.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (G.bluetoothAdapter == null) {
            Toast.makeText(this, "You device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!G.bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        btnChooseDevice = findViewById(R.id.btnChooseDevice);
        btnChooseDevice.setOnClickListener(this);

        tvCurrentDevice = findViewById(R.id.tvCurrentDevice);

        btnManualDrive = findViewById(R.id.btnManualDrive);
        btnManualDrive.setOnClickListener(this);

        btnAutomaticDrive = findViewById(R.id.btnAutomaticDrive);
        btnAutomaticDrive.setOnClickListener(this);

        btnOpenRadar = findViewById(R.id.btnRadar);
        btnOpenRadar.setOnClickListener(this);

        btnBuildAreaMap = findViewById(R.id.btnBuildMap);
        btnBuildAreaMap.setOnClickListener(this);

        btnProgramPath = findViewById(R.id.btnProgramPath);
        btnProgramPath.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(G.LOG_TAG, "BT enabled");
                    Toast.makeText(this, "Bluetooth has turned ON", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(G.LOG_TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                Log.e(G.LOG_TAG, "wrong request code");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
//            case R.id.action_github:
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/qbbr/android-arduino-car"));
//                startActivity(browserIntent);
//                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChooseDevice:
                if (btnChooseDevice.getText() == getResources().getString(R.string.btn_choose_device)) {
                    Intent intent = new Intent(this, DeviceListActivity.class);
                    startActivity(intent);
                } else {
                    G.connectThread.cancel();
                    finish();
                    startActivity(getIntent());
                }
                break;
            case R.id.btnManualDrive:
                Intent intentManualDrive = new Intent(this, ManualDriveActivity.class);
                startActivity(intentManualDrive);
                break;
            case R.id.btnAutomaticDrive:
                Intent intentAutomaticDrive = new Intent(this, AutomaticDriveActivity.class);
                startActivity(intentAutomaticDrive);
                break;
            case R.id.btnRadar:
                Intent intentRadar = new Intent(this, RadarActivity.class);
                startActivity(intentRadar);
                break;
            case R.id.btnBuildMap:
                Intent intentBuildMap = new Intent(this, AreaMapActivity.class);
                startActivity(intentBuildMap);
                break;
            case R.id.btnProgramPath:
                startActivity(new Intent(this, ProgramPathActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (G.connectThread != null && G.connectThread.isConnected()) {
            tvCurrentDevice.setText("connected to device " + G.bluetoothDevice.getName());
            btnChooseDevice.setText(R.string.btn_disconnect);
            btnManualDrive.setEnabled(true);
            btnAutomaticDrive.setEnabled(true);
            btnOpenRadar.setEnabled(true);
            btnBuildAreaMap.setEnabled(true);
            btnProgramPath.setEnabled(true);
        } else {
            tvCurrentDevice.setText(R.string.device_not_chosen);
            btnChooseDevice.setText(R.string.btn_choose_device);
            btnManualDrive.setEnabled(false);
            btnAutomaticDrive.setEnabled(false);
            btnOpenRadar.setEnabled(false);
            btnBuildAreaMap.setEnabled(false);
            btnProgramPath.setEnabled(false);
        }
    }
}
