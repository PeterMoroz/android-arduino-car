package io.qbbr.arduinocar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AreaMapActivity extends AppCompatActivity {
    private StringBuilder sb = new StringBuilder();

    private AreaMapView areaMapView = null;

    private static final String ARDUINO_END_OF_LINE = "\r\n";
    private static final String ARDUINO_MAP_DATA = "$mapdata: ";
    public static final char CMD_BUILD_AREA_MAP = 'b';



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_area_map);

        areaMapView = new AreaMapView(this);
        setContentView(areaMapView);


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                            if (data.startsWith(ARDUINO_MAP_DATA)) {
                                data = data.substring(data.indexOf(':') + 2);
                                String[] params = data.split(", ");
                                if (params.length == 6) {
                                    try {
                                        float rx = Float.parseFloat(params[0]);
                                        float ry = Float.parseFloat(params[1]);
                                        float heading = Float.parseFloat(params[2]);
                                        float distL = Float.parseFloat(params[3]);
                                        float distC = Float.parseFloat(params[4]);
                                        float distR = Float.parseFloat(params[5]);

                                        // Update UI on the main thread
                                        runOnUiThread(() -> {
                                            areaMapView.addRobotPoint(rx, ry);

                                            // Left sensor (-45 deg)
                                            if (distL < 150) {
                                                float[] obs = collectObstaclePoint(rx, ry, heading, distL, -45);
                                                areaMapView.addObstaclePoint(obs[0], obs[1]);
                                            }
                                            // Center sensor (0 deg)
                                            if (distC < 150) {
                                                float[] obs = collectObstaclePoint(rx, ry, heading, distC, 0);
                                                areaMapView.addObstaclePoint(obs[0], obs[1]);
                                            }
                                            // Right sensor (+45 deg)
                                            if (distR < 150) {
                                                float[] obs = collectObstaclePoint(rx, ry, heading, distR, 45);
                                                areaMapView.addObstaclePoint(obs[0], obs[1]);
                                            }
                                        });
                                    } catch (NumberFormatException e) {
                                        // Ignore malformed lines
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
        write(CMD_BUILD_AREA_MAP);
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

    private float[] collectObstaclePoint(float rx, float ry, float heading, float distance, float sensorAngle) {
        double totalAngle = Math.toRadians(heading + sensorAngle);
        float obsX = (float) (rx + (distance * Math.sin(totalAngle)));
        float obsY = (float) (ry + (distance * Math.cos(totalAngle)));
        return new float[]{obsX, obsY};
    }
}