package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "subgroup")
    String subgroup;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonBySubgroup> lessonBySubgroups;

    public Students() {
    }

    public Students(String institute, String direction, String groupp, String subgroup) {
        this.institute = institute;
        this.direction = direction;
        this.groupp = groupp;
        this.subgroup = subgroup;
        this.lessonBySubgroups = new ArrayList<LessonBySubgroup>();
    }

    public void addLessonBySubgroup(LessonBySubgroup lessonBySubgroup) {
        lessonBySubgroup.setStudent(this);
        lessonBySubgroups.add(lessonBySubgroup);
    }

    public void removeLessonBySubgroup(LessonBySubgroup lessonBySubgroup) {
        lessonBySubgroups.remove(lessonBySubgroup);
    }

    public List<LessonBySubgroup> getLessonBySubgroups() {
        return lessonBySubgroups;
    }

    public void setLessonBySubgroups(List<LessonBySubgroup> lessonBySubgroups) {
        this.lessonBySubgroups = lessonBySubgroups;
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

    public String getGroupp() {
        return groupp;
    }

    public void setGroupp(String groupp) {
        this.groupp = groupp;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
    }
}
