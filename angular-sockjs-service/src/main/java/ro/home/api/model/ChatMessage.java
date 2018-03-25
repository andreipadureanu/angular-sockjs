package ro.home.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class ChatMessage {

    private String message;
    private String author;
    private Date date;

    public ChatMessage() {
        this("", "");
    }

    public ChatMessage(String author, String message) {
        this.author = author;
        this.message = message;
        this.date = new Date();
    }

    public ChatMessage(ChatMessage message) {
        this.message = message.getMessage();
        this.author = message.getAuthor();
        this.date = message.date;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "{\"author\":\"" + author + "\",\"message\":\"" + message + "\",\"date\":\"" + date + "\"}";
    }
}
