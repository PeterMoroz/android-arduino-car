package io.qbbr.arduinocar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AutomaticDriveActivity extends AppCompatActivity {

    private static final String ARDUINO_DATA = "$data: ";
    public static final char CMD_AUTOMATIC_DRIVE = 'n';

    TextView tvDistanceLeft;
    TextView tvDistanceMiddle;
    TextView tvDistanceRight;

    TextView tvSpeedLeft;
    TextView tvSpeedRight;
    TextView tvSpeedAdjusted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_drive);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvDistanceLeft = findViewById(R.id.tvDistanceLeft);
        tvDistanceMiddle = findViewById(R.id.tvDistanceMiddle);
        tvDistanceRight = findViewById(R.id.tvDistanceRight);

        tvSpeedLeft = findViewById(R.id.tvSpeedLeft);
        tvSpeedRight = findViewById(R.id.tvSpeedRight);
        tvSpeedAdjusted = findViewById(R.id.tvSpeedAdjusted);

        G.connectThread.addHandler(new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case ConnectThread.RECEIVE_MESSAGE:
                        String data = (String) msg.obj;
//                        Log.d(G.LOG_TAG, "data: '" + data + "'");
                        if (data.startsWith(ARDUINO_DATA)) {
                            data = data.substring(data.indexOf(':') + 2);
                            String[] params = data.split(", ");
                            if (params.length == 6) {
                                tvDistanceLeft.setText(params[0]);
                                tvDistanceMiddle.setText(params[1]);
                                tvDistanceRight.setText(params[2]);
                                tvSpeedLeft.setText(params[3]);
                                tvSpeedAdjusted.setText(params[4]);
                                tvSpeedRight.setText(params[5]);
                            }
                        }
                        break;
                }
            }
        });

        if (G.connectThread.getState() == Thread.State.NEW) {
            G.connectThread.start();
        }

        if (!G.connectThread.isAlive()) {
            G.connectThread.cancel();
            finishWithError();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        write(CMD_AUTOMATIC_DRIVE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void write(char data) {
        if (!G.connectThread.write(data)) {
            finishWithError();
        }
    }

    private void finishWithError() {
        Toast.makeText(this, "ConnectThread is'n alive", Toast.LENGTH_LONG).show();
        finish();
    }
}