package sample;

public class Data {
    private int value;
    private long time;

    public Data(int value, long time) {
        this.value = value;
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }



    public void setValue(int value) {
        this.value = value;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
