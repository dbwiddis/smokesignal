package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private Timestamp updatedAt;
    private boolean tp;
    private boolean fp;
    private boolean naa;

    public Post() {
    }

    public Post(int id, Timestamp updatedAt, boolean tp, boolean fp, boolean naa) {
        this.id = id;
        this.updatedAt = updatedAt;
        this.tp = tp;
        this.fp = fp;
        this.naa = naa;
    }

    public int getId() {
        return id;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public boolean isTp() {
        return tp;
    }

    public boolean isFp() {
        return fp;
    }

    public boolean isNaa() {
        return naa;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTp(boolean tp) {
        this.tp = tp;
    }

    public void setFp(boolean fp) {
        this.fp = fp;
    }

    public void setNaa(boolean naa) {
        this.naa = naa;
    }
}
