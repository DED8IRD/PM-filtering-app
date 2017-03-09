package sqlitedb.models;

/**
 * Photo.java
 * Model for Photo table.
 * Created by DED8IRD on 11/7/2016.
 */

import java.io.ByteArrayOutputStream;

public class Photo {

    private int id;
    private String participant;
    private String timestamp;
    private String image;
    private String tag;
    private int rank;
    private String delete;

    // constructors
    public Photo() {
    }

    public Photo(String participant, String timestamp, String image) {
        this.participant = participant;
        this.timestamp = timestamp;
        this.image = image;
        this.tag = null;
        this.rank = -1;
        this.delete = null;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public void setImage(String image) { this.image = image; }

    public void setTag(String tag) { this.tag = tag; }

    public void setRank(int rank) { this.rank = rank; }

    public void setDelete(String timestamp) {this.delete = timestamp; }

    // getters
    public long getId() {
        return this.id;
    }

    public String getParticipant() {
        return this.participant;
    }

    public String getTimestamp() { return this.timestamp; }

    public String getImage() {
        return this.image;
    }

    public String getTag() {
        return this.tag;
    }

    public int getRank() {
        return this.rank;
    }

    public String getDelete() {return  this.delete; }
}