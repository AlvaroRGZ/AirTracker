package es.upm.miw.airtracker.model;

import javax.annotation.Generated;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
@IgnoreExtraProperties
public class Condition {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("code")
    @Expose
    private Integer code;

    public Condition() {
    }

    public Condition(String text, String icon, Integer code) {
        this.text = text;
        this.icon = icon;
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", code=" + code +
                '}';
    }
}
