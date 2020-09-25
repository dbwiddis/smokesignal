package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Flag implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int postId;
    private Timestamp createdAt;
    private boolean auto;

    public Flag() {
    }

    public Flag(int id, int postId, Timestamp createdAt, boolean auto) {
        this.id = id;
        this.postId = postId;
        this.createdAt = createdAt;
        this.auto = auto;
    }

    public int getId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }
}
