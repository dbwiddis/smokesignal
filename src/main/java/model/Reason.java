package model;

import java.io.Serializable;

public class Reason implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private boolean inactive;
    private String reasonName;
    private int weight;

    public Reason() {
    }

    public Reason(int id, boolean inactive, String reasonName, int weight) {
        super();
        this.id = id;
        this.inactive = inactive;
        this.reasonName = reasonName;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getReasonName() {
        return reasonName;
    }

    public boolean isInactive() {
        return inactive;
    }

    public int getWeight() {
        return weight;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
