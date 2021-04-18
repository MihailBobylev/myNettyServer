package models;

import javax.persistence.*;

@Entity
@Table(name = "students")
public class Students {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "institute")
    String institute;

    @Column(name = "direction")
    String direction;

    @Column(name = "groupp")
    String groupp;

    public Students() {
    }

    public Students(String institute, String direction, String groupp) {
        this.institute = institute;
        this.direction = direction;
        this.groupp = groupp;
    }

    public Integer getId() {
        return id;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getGroup() {
        return groupp;
    }

    public void setGroup(String group) {
        this.groupp = groupp;
    }
}
