package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Flag implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private Post post;
    private Timestamp createdAt;
    private boolean auto;

    public Flag() {
    }

    public Flag(int id, Post post, Timestamp createdAt, boolean auto) {
        this.id = id;
        this.post = post;
        this.createdAt = createdAt;
        this.auto = auto;
    }

    public int getId() {
        return id;
    }

    public Post getPost() {
        return post;
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

    public void setPost(Post post) {
        this.post = post;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }
}
