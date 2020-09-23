package model;

import java.io.Serializable;

public class PostReason implements Serializable {

    private static final long serialVersionUID = 1L;

    private int reasonId;
    private int postId;

    public PostReason() {
    }

    public PostReason(int reasonId, int postId) {
        this.reasonId = reasonId;
        this.postId = postId;
    }

    public int getReasonId() {
        return reasonId;
    }

    public int getPostId() {
        return postId;
    }

    public void setReasonId(int reasonId) {
        this.reasonId = reasonId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
