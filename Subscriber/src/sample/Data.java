package sample;

public class Data {
    private int value;
    private long time;
    private String topic ="";

    public Data(int value, long time, String topic) {
        this.value = value;
        this.time = time;
        this.topic = topic;
    }

    public int getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }

    public String getTopic() {
        return topic;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

}
