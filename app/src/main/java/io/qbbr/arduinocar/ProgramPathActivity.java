package io.qbbr.arduinocar;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProgramPathActivity extends AppCompatActivity  implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    ImageButton btnForwardLeft;
    ImageButton btnForward;
    ImageButton btnForwardRight;
    ImageButton btnRotateLeft;
    ImageButton btnStop;
    ImageButton btnRotateRight;
    ImageButton btnBackwardLeft;
    ImageButton btnBackward;
    ImageButton btnBackwardRight;

    ImageButton activeButton;
    Drawable activeButtonBackground;

    SeekBar seekBarSpeed;
    TextView tvSpeed;

    SeekBar seekBarTime;
    TextView tvTime;

    TextView tvCommand;
    Button btnAddCommand;
    Button btnExecuteProgram;

    int speedValue = 0;
    int timeValue = 0;

    Command command = null;
    List<Command> commandsList = null;
    List<String> itemsList = null;
    RecyclerView recyclerView = null;
    StringAdapter adapter = null;

    public static final char CMD_PROGRAMMED_DRIVE = 'p';

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                adapter.removeItem(position);
                commandsList.remove(position);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_path);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnForward = findViewById(R.id.btnForward);
        btnForward.setOnClickListener(v -> {
            deactivateOtherButton(btnForward);
            btnForward.setActivated(!btnForward.isActivated());
            if (btnForward.isActivated()) {
                activeButton = btnForward;
                outlineActiveButton();
                seekBarSpeed.setEnabled(true);
                command = new Command("forward", timeValue, speedValue);
            } else {
                deactivateButton(btnForward);
                command = null;
            }
            onCommandUpdate();
        });

        btnForwardLeft = findViewById(R.id.btnForwardLeft);
        btnForwardLeft.setOnClickListener(v -> {
            deactivateOtherButton(btnForwardLeft);
            btnForwardLeft.setActivated(!btnForwardLeft.isActivated());
            if (btnForwardLeft.isActivated()) {
                activeButton = btnForwardLeft;
                outlineActiveButton();
                seekBarSpeed.setEnabled(true);
                command = new Command("fleft", timeValue, speedValue);
            } else {
                deactivateButton(btnForwardLeft);
                command = null;
            }
            onCommandUpdate();
        });

        btnForwardRight = findViewById(R.id.btnForwardRight);
        btnForwardRight.setOnClickListener(v -> {
            deactivateOtherButton(btnForwardRight);
            btnForwardRight.setActivated(!btnForwardRight.isActivated());
            if (btnForwardRight.isActivated()) {
                activeButton = btnForwardRight;
                outlineActiveButton();
                seekBarSpeed.setEnabled(true);
                command = new Command("fright", timeValue, speedValue);
            } else {
                deactivateButton(btnForwardRight);
                command = null;
            }
            onCommandUpdate();
        });

        btnRotateLeft = findViewById(R.id.btnRotateLeft);
        btnRotateLeft.setOnClickListener(v -> {
            deactivateOtherButton(btnRotateLeft);
            btnRotateLeft.setActivated(!btnRotateLeft.isActivated());
            if (btnRotateLeft.isActivated()) {
                activeButton = btnRotateLeft;
                outlineActiveButton();
                seekBarSpeed.setEnabled(true);
                command = new Command("rleft", timeValue, speedValue);
            } else {
                deactivateButton(btnRotateLeft);
                command = null;
            }
            onCommandUpdate();
        });

        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(v -> {
            deactivateOtherButton(btnStop);
            btnStop.setActivated(!btnStop.isActivated());
            if (btnStop.isActivated()) {
                activeButton = btnStop;
                outlineActiveButton();
                seekBarSpeed.setEnabled(false);
                command = new Command("stop", timeValue);
            } else {
                deactivateButton(btnStop);
                command = null;
            }
            onCommandUpdate();
        });

        btnRotateRight = findViewById(R.id.btnRotateRight);
        btnRotateRight.setOnClickListener(v -> {
            deactivateOtherButton(btnRotateRight);
            btnRotateRight.setActivated(!btnRotateRight.isActivated());
            if (btnRotateRight.isActivated()) {
                activeButton = btnRotateRight;
                outlineActiveButton();
                seekBarSpeed.setEnabled(true);
                command = new Command("rright", timeValue, speedValue);
            } else {
                deactivateButton(btnRotateRight);
                command = null;
            }
            onCommandUpdate();
        });

        btnBackwardLeft = findViewById(R.id.btnBackwardLeft);
        btnBackwardLeft.setOnClickListener(v -> {
            deactivateOtherButton(btnBackwardLeft);
            btnBackwardLeft.setActivated(!btnBackwardLeft.isActivated());
            if (btnBackwardLeft.isActivated()) {
                activeButton = btnBackwardLeft;
                outlineActiveButton();
                seekBarSpeed.setEnabled(true);
                command = new Command("bleft", timeValue, speedValue);
            } else {
                deactivateButton(btnBackwardLeft);
                command = null;
            }
            onCommandUpdate();
        });

        btnBackward = findViewById(R.id.btnBackward);
        btnBackward.setOnClickListener(v -> {
            deactivateOtherButton(btnBackward);
            btnBackward.setActivated(!btnBackward.isActivated());
            if (btnBackward.isActivated()) {
                activeButton = btnBackward;
                outlineActiveButton();
                seekBarSpeed.setEnabled(true);
                command = new Command("backward", timeValue, speedValue);
            } else {
                deactivateButton(btnBackward);
                command = null;
            }
            onCommandUpdate();
        });

        btnBackwardRight = findViewById(R.id.btnBackwardRight);
        btnBackwardRight.setOnClickListener(v -> {
            deactivateOtherButton(btnBackwardRight);
            btnBackwardRight.setActivated(!btnBackwardRight.isActivated());
            if (btnBackwardRight.isActivated()) {
                activeButton = btnBackwardRight;
                outlineActiveButton();
                seekBarSpeed.setEnabled(true);
                command = new Command("bright", timeValue, speedValue);
            } else {
                deactivateButton(btnBackwardRight);
                command = null;
            }
            onCommandUpdate();
        });

        activeButton = null;
        activeButtonBackground = null;

        tvSpeed = findViewById(R.id.tvSpeed);
        seekBarSpeed = findViewById(R.id.seekbarSpeed);
        seekBarSpeed.setOnSeekBarChangeListener(this);

        tvTime = findViewById(R.id.tvTime);
        seekBarTime = findViewById(R.id.seekbarTime);
        seekBarTime.setOnSeekBarChangeListener(this);

        tvCommand = findViewById(R.id.tvCommand);
        btnAddCommand = findViewById(R.id.btnAddCommand);
        btnAddCommand.setOnClickListener(v -> {
            if (command != null) {
                commandsList.add(command);
                // String commandItem = tvCommand.getText().toString().trim();
                String commandItem = command.toString();
                if (!commandItem.isEmpty()) {
                    itemsList.add(commandItem);
                    adapter.notifyItemInserted(itemsList.size() - 1);
                }
                if (!btnExecuteProgram.isEnabled()) {
                    btnExecuteProgram.setEnabled(true);
                }
            }
        });

        btnExecuteProgram = findViewById(R.id.btnExecute);
        btnExecuteProgram.setOnClickListener(v -> {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < commandsList.size(); i++) {
                        Command command = commandsList.get(i);
//                        Log.d(G.LOG_TAG, "send command: " + command);
                        G.connectThread.write(command.serialize());
                        itemsList.remove(0);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemRemoved(0);
                                adapter.notifyItemRangeChanged(0, itemsList.size());
                            }
                        });

                        try {
                            Thread.sleep(command.getTime() * 100);
                        } catch (InterruptedException e) {
                            ;
                        }
                    }
                    commandsList.clear();
                    btnAddCommand.post(new Runnable() {
                        @Override
                        public void run() {
                            btnAddCommand.setEnabled(true);
                        }
                    });
                }
            };

            btnAddCommand.setEnabled(false);
            btnExecuteProgram.setEnabled(false);
            Thread thread = new Thread(runnable);
            thread.start();
        });

        speedValue = seekBarSpeed.getProgress();
        timeValue = seekBarTime.getProgress();
        command = null;
        commandsList = new ArrayList<>();
        itemsList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StringAdapter(itemsList);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        G.connectThread.addHandler(new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                // ignore message, just dummy handler
                switch (msg.what) {
                    case ConnectThread.RECEIVE_MESSAGE:
                        String data = (String) msg.obj;
//                        Log.d(G.LOG_TAG, "received: " + data);
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
        write(CMD_PROGRAMMED_DRIVE);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekbarSpeed:
                setSpeed(i);
                break;
            case R.id.seekbarTime:
                setTime(i);
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
        speedValue = i;
        tvSpeed.setText("Speed: " + String.valueOf(i));
        if (command != null) {
            command = new Command(command);
            command.setSpeed(speedValue);
            tvCommand.setText(command.toString());
        }
    }

    private void setTime(int i) {
        // time values: 1-100
        timeValue = i;
        tvTime.setText("Time: " + String.valueOf(i / 10.f));
        if (command != null) {
            command = new Command(command);
            command.setTime(timeValue);
            tvCommand.setText(command.toString());
        }
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

    private void outlineActiveButton() {
        activeButtonBackground = activeButton.getBackground();
        GradientDrawable outlineDrawable = new GradientDrawable();
        outlineDrawable.setColor(Color.TRANSPARENT);
        outlineDrawable.setStroke(8, Color.RED);
        activeButton.setBackground(outlineDrawable);
    }

    private void deactivateOtherButton(ImageButton button) {
        if (button != activeButton && activeButton != null && activeButtonBackground != null) {
            activeButton.setActivated(false);
            activeButton.setBackground(activeButtonBackground);
            activeButton = null;
            activeButtonBackground = null;
        }
    }

    private void deactivateButton(ImageButton button) {
        if (activeButton == button && activeButtonBackground != null) {
            activeButton.setActivated(false);
            activeButton.setBackground(activeButtonBackground);
            activeButton = null;
            activeButtonBackground = null;
        }
    }

    private void onCommandUpdate() {
        if (command != null) {
            tvCommand.setText(command.toString());
            btnAddCommand.setEnabled(true);
        } else {
            tvCommand.setText("");
            btnAddCommand.setEnabled(false);
        }
    }
}