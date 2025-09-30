package simu.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name="configuration")
public class Configuration {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String type;
    private int value;

    public Configuration(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public Configuration() {

    }

    public String getType() {
        return this.type;
    }

    public int getValue() {
        return this.value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(int value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return type + ":" + value;
    }
}