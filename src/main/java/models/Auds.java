package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "aud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons;

    public Auds() {
    }

    public Auds(String corp, String number) {
        this.corp = corp;
        this.number = number;
        lessons = new ArrayList<Lesson>();
    }

    public void addLesson(Lesson lesson) {
        lesson.setAud(this);
        lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
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

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
