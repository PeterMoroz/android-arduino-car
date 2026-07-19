package io.qbbr.arduinocar;

public class Command {
    private String command;
    private int time;
    private int speed;

    Command(String command, int time) {
        this.command = command;
        this.time = time;
    }

    Command(String command, int time, int speed) {
        this.command = command;
        this.time = time;
        this.speed = speed;
    }

    Command(Command other) {
        this.command = other.command;
        this.time = other.time;
        this.speed = other.speed;
    }

//    void setCommand(String command) {
//        this.command = command;
//    }

    void setTime(int time) {
        this.time = time;
    }

    int getTime() {
        return  time;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    public String toString() {
        float t = ((float)(time)) / 10.f;
        if (command.compareTo("stop") != 0) {
            return command + ", t: " + t + ", s: " + speed;
        }
        return command + ", t: " + t;
    }

    public String serialize() {
        if (command.compareTo("stop") != 0) {
            return "#" + command + ", " + time + ", " + speed + "#";
        }
        return "#" + command + ", " + time + "#";
    }
}
