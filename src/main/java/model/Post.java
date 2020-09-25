package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private Timestamp createdAt;
    private boolean tp;
    private boolean fp;
    private boolean naa;

    public Post() {
    }

    public Post(int id, Timestamp createdAt, boolean tp, boolean fp, boolean naa) {
        this.id = id;
        this.createdAt = createdAt;
        this.tp = tp;
        this.fp = fp;
        this.naa = naa;
    }

    public int getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
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

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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
