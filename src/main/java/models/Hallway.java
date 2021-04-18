package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Hallway")
public class Hallway {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startAreaId")
    private Area startArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endAreaId")
    private Area endArea;

    @Column(name = "orientationStart")
    String orientationStart;

    @Column(name = "orientationEnd")
    String orientationEnd;

    @OneToMany(mappedBy = "hallway", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Classroom> classrooms;

    public Hallway() {
    }

    public Hallway(String orientationStart, String orientationEnd) { //Integer id, String startArea, String endArea,
        this.orientationStart = orientationStart;
        this.orientationEnd = orientationEnd;
        classrooms = new ArrayList<Classroom>();
    }

    public void addClassroom(Classroom classroom) {
        classroom.setHallway(this);
        classrooms.add(classroom);
    }

    public void removeClassroom(Classroom classroom) {
        classrooms.remove(classroom);
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public Integer getId() {
        return id;
    }

    public Area getStartArea() {
        return startArea;
    }

    public void setStartArea(Area startArea) {
        this.startArea = startArea;
    }

    public Area getEndArea() {
        return endArea;
    }

    public void setEndArea(Area endArea) {
        this.endArea = endArea;
    }


    public String getOrientationStart() {
        return orientationStart;
    }

    public void setOrientationStart(String orientationStart) {
        this.orientationStart = orientationStart;
    }

    public String getOrientationEnd() {
        return orientationEnd;
    }

    public void setOrientationEnd(String orientationEnd) {
        this.orientationEnd = orientationEnd;
    }

}
