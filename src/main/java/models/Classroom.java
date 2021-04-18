package models;

import javax.persistence.*;

@Entity
@Table(name = "Classes")
public class Classroom {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "number")
    Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hallwayId")
    private Hallway hallway;

    @Column(name = "side")
    String side;

    @Column(name = "position")
    String position;

    public Classroom() {
    }

    public Classroom(Integer number, String side, String position) { //Integer hallwayId,
        this.number = number;
        this.side = side;
        this.position = position;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Hallway getHallway() {
        return hallway;
    }

    public void setHallway(Hallway hallway) {
        this.hallway = hallway;
    }
}
