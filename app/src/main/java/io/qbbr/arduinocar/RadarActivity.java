package io.qbbr.arduinocar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RadarActivity extends AppCompatActivity {

    private RadarView radarView = null;

    private static final String ARDUINO_VAR_DISTANCE = "$distance: ";
    public static final char CMD_RADAR = 'r';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        radarView = new RadarView(this);
        setContentView(radarView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        G.connectThread.addHandler(new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case ConnectThread.RECEIVE_MESSAGE:
                        String data = (String) msg.obj;
                        if (data.startsWith(ARDUINO_VAR_DISTANCE)) {
                            data = data.substring(data.indexOf(':') + 2);
                            String[] params = data.split(", ");
                            if (params.length == 3) {
                                try {
                                    final int left = Integer.parseInt(params[0].trim());
                                    final int centre = Integer.parseInt(params[1].trim());
                                    final int right = Integer.parseInt(params[2].trim());
                                    runOnUiThread(() -> radarView.plotDistances(left, centre, right));
                                } catch (NumberFormatException e) {
                                    // Ignore malformed lines
                                }
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
        write(CMD_RADAR);
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