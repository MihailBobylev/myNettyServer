package models;

import javax.persistence.*;

@Entity
@Table(name = "auds")
public class Auds {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "corp")
    String corp;

    @Column(name = "number")
    String number;

    @Column(name = "schedule")
    String schedule;

    public Auds() {
    }

    public Auds(String corp, String number, String schedule) {
        this.corp = corp;
        this.number = number;
        this.schedule = schedule;
    }

    public Integer getId() {
        return id;
    }

    public String getCorp() {
        return corp;
    }

    public void setCorp(String corp) {
        this.corp = corp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
