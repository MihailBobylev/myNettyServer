package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Area")
public class Area {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "housing")
    private String housing;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "name")
    private String name;

    @Column(name = "isVisited")
    private Boolean isVisited;

    @OneToMany(mappedBy = "startArea", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hallway> startHallways;

    @OneToMany(mappedBy = "endArea", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hallway> endHallways;

    public Area() {
    }

    public Area(String housing, Integer floor, String name) {
        this.housing = housing;
        this.floor = floor;
        this.name = name;
        this.isVisited = false;
        startHallways = new ArrayList<Hallway>();
        endHallways = new ArrayList<Hallway>();
    }

    public void addStartHallway(Hallway hallway) {
        hallway.setStartArea(this);
        startHallways.add(hallway);
    }

    public void addEndHallway(Hallway hallway) {
        hallway.setEndArea(this);
        endHallways.add(hallway);
    }

    public void removeStartHallway(Hallway hallway) {
        startHallways.remove(hallway);
    }

    public List<Hallway> getStartHallway() {
        return startHallways;
    }

    public void setStartHallway(List<Hallway> hallways) {
        this.startHallways = hallways;
    }

    public void removeEndHallway(Hallway hallway) {
        endHallways.remove(hallway);
    }

    public List<Hallway> getEndHallway() {
        return endHallways;
    }

    public void setEndHallway(List<Hallway> hallways) {
        this.endHallways = hallways;
    }

    public Integer getId() {
        return id;
    }

    public String getHousing() {
        return housing;
    }

    public void setHousing(String housing) {
        this.housing = housing;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVisited() {
        return isVisited;
    }

    public void setVisited(Boolean visited) {
        isVisited = visited;
    }
}
