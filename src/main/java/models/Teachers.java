package models;

import javax.persistence.*;

@Entity
@Table(name = "teachers")
public class Teachers {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "schedule")
    String schedule;

    public Teachers() {
    }

    public Teachers(String name, String schedule) {
        this.name = name;
        this.schedule = schedule;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
