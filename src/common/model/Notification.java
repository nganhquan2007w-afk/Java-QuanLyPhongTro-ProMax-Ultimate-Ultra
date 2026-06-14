package common.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model biểu diễn một bản tin / thông báo.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String content;
    private Timestamp datePosted;

    public Notification(int id, String title, String content, Timestamp datePosted) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.datePosted = datePosted;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getDatePosted() { return datePosted; }
    public void setDatePosted(Timestamp datePosted) { this.datePosted = datePosted; }
}
