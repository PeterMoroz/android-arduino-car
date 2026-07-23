package io.qbbr.arduinocar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class ManualDriveActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final char CMD_FORWARD_LEFT = 'q';
    public static final char CMD_FORWARD = 'w';
    public static final char CMD_FORWARD_RIGHT = 'e';
    public static final char CMD_BACKWARD = 'x';
    public static final char CMD_BACKWARD_LEFT = 'z';
    public static final char CMD_BACKWARD_RIGHT = 'c';
    public static final char CMD_ROTATE_LEFT = 'a';
    public static final char CMD_ROTATE_RIGHT = 'd';
    public static final char CMD_STOP = 's';
    public static final char CMD_MANUAL_DRIVE = 'm';
    private static final String ARDUINO_DATA = "$data: ";
    
    ImageButton btnForwardLeft;
    ImageButton btnForward;
    ImageButton btnForwardRight;
    ImageButton btnRotateLeft;
    ImageButton btnStop;
    ImageButton btnRotateRight;
    ImageButton btnBackwardLeft;
    ImageButton btnBackward;
    ImageButton btnBackwardRight;

    SeekBar seekBarSpeed;
    TextView tvSpeed;

    TextView tvDistanceLeft;
    TextView tvDistanceMiddle;
    TextView tvDistanceRight;

    CheckBox cbEnableChronometer;
    Chronometer chronometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_drive);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnForward = findViewById(R.id.btnForward);
        btnForward.setOnClickListener(this);
        btnForwardLeft = findViewById(R.id.btnForwardLeft);
        btnForwardLeft.setOnClickListener(this);
        btnForwardRight = findViewById(R.id.btnForwardRight);
        btnForwardRight.setOnClickListener(this);
        btnRotateLeft = findViewById(R.id.btnRotateLeft);
        btnRotateLeft.setOnClickListener(this);
        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);
        btnRotateRight = findViewById(R.id.btnRotateRight);
        btnRotateRight.setOnClickListener(this);
        btnBackwardLeft = findViewById(R.id.btnBackwardLeft);
        btnBackwardLeft.setOnClickListener(this);
        btnBackward = findViewById(R.id.btnBackward);
        btnBackward.setOnClickListener(this);
        btnBackwardRight = findViewById(R.id.btnBackwardRight);
        btnBackwardRight.setOnClickListener(this);

        tvSpeed = findViewById(R.id.tvSpeed);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        seekBarSpeed.setOnSeekBarChangeListener(this);

        cbEnableChronometer = findViewById(R.id.cbChronometerEnabled);
        cbEnableChronometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {
                if (!cbEnableChronometer.isChecked()) {
                    chronometer.stop();
                }
            }
        });

        chronometer = findViewById(R.id.cmChronometer);

        tvDistanceLeft = findViewById(R.id.tvDistanceLeft);
        tvDistanceMiddle = findViewById(R.id.tvDistanceMiddle);
        tvDistanceRight = findViewById(R.id.tvDistanceRight);

        G.connectThread.addHandler(new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case ConnectThread.RECEIVE_MESSAGE:
                        String data = (String) msg.obj;
                        if (data.startsWith(ARDUINO_DATA)) {
                            data = data.substring(data.indexOf(':') + 2);
                            String[] params = data.split(", ");
                            if (params.length == 6) {
                                tvDistanceLeft.setText(params[0]);
                                tvDistanceMiddle.setText(params[1]);
                                tvDistanceRight.setText(params[2]);
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
        write(CMD_MANUAL_DRIVE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnForwardLeft:
                write(CMD_FORWARD_LEFT);
                break;
            case R.id.btnForward:
                write(CMD_FORWARD);
                break;
            case R.id.btnForwardRight:
                write(CMD_FORWARD_RIGHT);
                break;
            case R.id.btnRotateLeft:
                write(CMD_ROTATE_LEFT);
                break;
            case R.id.btnStop:
                write(CMD_STOP);
                break;
            case R.id.btnRotateRight:
                write(CMD_ROTATE_RIGHT);
                break;
            case R.id.btnBackwardLeft:
                write(CMD_BACKWARD_LEFT);
                break;
            case R.id.btnBackward:
                write(CMD_BACKWARD);
                break;
            case R.id.btnBackwardRight:
                write(CMD_BACKWARD_RIGHT);
                break;
        }

        if (cbEnableChronometer.isChecked()) {
            int viewId = view.getId();
            if (viewId == R.id.btnStop) {
                chronometer.stop();
            } else if (viewId == R.id.btnForward || viewId == R.id.btnBackward ||
                viewId == R.id.btnForwardLeft || viewId == R.id.btnForwardRight ||
                viewId == R.id.btnRotateLeft || viewId == R.id.btnRotateRight ||
                viewId == R.id.btnBackwardLeft || viewId == R.id.btnBackwardRight) {
                chronometer.setText("");
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekBarSpeed:
                setSpeed(i);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setSpeed(int i) {
        // speed values: 0-9
        write(Character.forDigit(i - 1, 10));
        tvSpeed.setText("Speed: " + String.valueOf(i));
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
