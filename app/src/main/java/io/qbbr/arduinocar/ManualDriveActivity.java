package io.qbbr.arduinocar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ManualDriveActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private StringBuilder sb = new StringBuilder();

    public static final char CMD_FORWARD_LEFT = 'l';
    public static final char CMD_FORWARD = 'f';
    public static final char CMD_FORWARD_RIGHT = 'r';
    public static final char CMD_BACKWARD = 'b';
    public static final char CMD_BACKWARD_LEFT = 'h';
    public static final char CMD_BACKWARD_RIGHT = 'j';
    public static final char CMD_ROTATE_LEFT = 'n';
    public static final char CMD_ROTATE_RIGHT = 'm';
    public static final char CMD_STOP = 's';
    // CMD_SPEED  0 - 9
    public static final char CMD_GET_SPEED = 'e';
    public static final char CMD_SERVO_MID = 'w';
    public static final char CMD_SERVO_LEFT = 'a';
    public static final char CMD_SERVO_RIGHT = 'd';
    public static final char CMD_GET_DISTANCE = 'g';

    private static final String ARDUINO_VAR_DISTANCE = "$distance: ";
    private static final String ARDUINO_END_OF_LINE = "\r\n";

    ImageButton btnForwardLeft;
    ImageButton btnForward;
    ImageButton btnForwardRight;
    ImageButton btnRotateLeft;
    ImageButton btnStop;
    ImageButton btnRotateRight;
    ImageButton btnBackwardLeft;
    ImageButton btnBackward;
    ImageButton btnBackwardRight;

    TextView tvDistance;
    Button btnDistance;

    RadioButton radioBtnServoLeft;
    RadioButton radioBtnServoMid;
    RadioButton radioBtnServoRight;

    SeekBar seekBarSpeed;
    TextView tvSpeed;

    TextView tvArduino;

    Button btnAutomaticMode;

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

        tvDistance = findViewById(R.id.tvDistance);
        btnDistance = findViewById(R.id.btnDistance);
        btnDistance.setOnClickListener(this);

        radioBtnServoLeft = findViewById(R.id.radioBtnServoLeft);
        radioBtnServoLeft.setOnClickListener(this);
        radioBtnServoMid = findViewById(R.id.radioBtnServoMid);
        radioBtnServoMid.setOnClickListener(this);
        radioBtnServoMid.setChecked(true);
        radioBtnServoRight = findViewById(R.id.radioBtnServoRight);
        radioBtnServoRight.setOnClickListener(this);

        tvSpeed = findViewById(R.id.tvSpeed);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        seekBarSpeed.setOnSeekBarChangeListener(this);

        tvArduino = findViewById(R.id.tvArduino);

        btnAutomaticMode = findViewById(R.id.btnAutomaticMode);
        btnAutomaticMode.setOnClickListener(this);

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
                            if (!data.startsWith("[D]")) { // skjp debug msg
                                tvArduino.setText(Html.fromHtml("<u>Arduino answer</u>:\n" + "<b>" + data + "</b>", Html.FROM_HTML_MODE_LEGACY));

                                if (data.startsWith(ARDUINO_VAR_DISTANCE)) {
                                    String distance = data.substring(data.indexOf(':') + 2);
                                    tvDistance.setText(Html.fromHtml("<b>" + distance + "</b>", Html.FROM_HTML_MODE_LEGACY));
                                }
                            }
                            sb.delete(0, sb.length());
                        }
                        break;
                }
            }
        });

        if (G.connectThread.getState() == Thread.State.NEW) {
            Log.d(G.LOG_TAG, "ManualDriveActivity - start connect thread");
            G.connectThread.start();
        }

        if (!G.connectThread.isAlive()) {
            G.connectThread.cancel();
            finishWithError();
        }
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
            case R.id.btnDistance:
                write(CMD_GET_DISTANCE);
                break;
            case R.id.radioBtnServoLeft:
                write(CMD_SERVO_LEFT);
                break;
            case R.id.radioBtnServoMid:
                write(CMD_SERVO_MID);
                break;
            case R.id.radioBtnServoRight:
                write(CMD_SERVO_RIGHT);
                break;
            case R.id.btnAutomaticMode:
                Intent intent = new Intent(this, AutomaticDriveActivity.class);
                startActivity(intent);
                break;
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

    private void setSpeed(int i) {
        // speed values: 0-9
        write(Character.forDigit(i - 1, 10));
        tvSpeed.setText("Speed: " + String.valueOf(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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
