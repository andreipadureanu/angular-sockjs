package ro.home.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class InformationMessage {

    private final String message;
    private final String type;
    private final Date date;

    public InformationMessage() {
        this("");
    }

    public InformationMessage(String message) {
        this("INFO", message);
    }

    public InformationMessage(String type, String message) {
        this.type = type;
        this.message = message;
        this.date = new Date();
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
}