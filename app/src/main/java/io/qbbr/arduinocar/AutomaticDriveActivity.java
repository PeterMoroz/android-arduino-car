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

    private static final String ARDUINO_VAR_DISTANCE = "$distance: ";
    private static final String ARDUINO_VAR_SPEED = "$speed: ";
    private static final String ARDUINO_END_OF_LINE = "\r\n";
    public static final char CMD_AUTOMATIC_DRIVE = 'n';

    private StringBuilder sb = new StringBuilder();

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
                        String readMsg = (String) msg.obj;
                        sb.append(readMsg);
                        int endOfLineIndex = sb.indexOf(ARDUINO_END_OF_LINE);
                        if (endOfLineIndex > 0) {
                            String data = sb.substring(0, endOfLineIndex);
                            Log.d(G.LOG_TAG, "data: '" + data + "'");

                            if (data.startsWith(ARDUINO_VAR_DISTANCE)) {
                                String distance = data.substring(data.indexOf(':') + 2);

                                int fromPos = 0, toPos = distance.indexOf(':');
                                if (toPos > 0) {
                                    String part0 = distance.substring(fromPos, toPos - 1);
                                    fromPos = toPos + 2;
                                    toPos = distance.indexOf(':', fromPos);
                                    if (toPos > 0) {
                                        String part1 = distance.substring(fromPos, toPos - 1);
                                        fromPos = toPos + 2;
                                        String part2 = distance.substring(fromPos);

                                        tvDistanceLeft.setText(part0);
                                        tvDistanceMiddle.setText(part1);
                                        tvDistanceRight.setText(part2);
                                    }
                                }
                            } else if (data.startsWith(ARDUINO_VAR_SPEED)) {
                                String speed = data.substring(data.indexOf(':') + 2);

                                int fromPos = 0, toPos = speed.indexOf(':');
                                if (toPos > 0) {
                                    String part0 = speed.substring(fromPos, toPos - 1);
                                    fromPos = toPos + 2;
                                    toPos = speed.indexOf(':', fromPos);
                                    if (toPos > 0) {
                                        String part1 = speed.substring(fromPos, toPos - 1);
                                        fromPos = toPos + 2;
                                        String part2 = speed.substring(fromPos);

                                        tvSpeedLeft.setText(part0);
                                        tvSpeedAdjusted.setText(part1);
                                        tvSpeedRight.setText(part2);
                                    }
                                }
                            }
                            sb.delete(0, sb.length());
                        }
                        break;
                }
            }
        });

        if (G.connectThread.getState() == Thread.State.NEW) {
            Log.d(G.LOG_TAG, "AutomaticDriveActivity - start connect thread");
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