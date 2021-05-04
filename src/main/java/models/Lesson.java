package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Lesson")
public class Lesson {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "lessontype")
    String lessontype;

    @Column(name = "numberOfWeek")
    String numberOfWeek;

    @Column(name = "lessonByGroup")
    String lessonByGroup;

    @Column(name = "groupp")
    String groupp;

    @Column(name = "subgroup")
    String subgroup;

    @Column(name = "numberOfClass")
    String numberOfClass;

    @Column(name = "numberOfDay")
    String numberOfDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_teacher")
    private Teachers teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_auds")
    private Auds aud;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonBySubgroup> lessonBySubgroups;

    public Lesson() {
    }

    public Lesson(String lessontype, String numberOfWeek, String lessonByGroup, String groupp, String subgroup, String numberOfClass, String numberOfDay) {
        this.lessontype = lessontype;
        this.numberOfWeek = numberOfWeek;
        this.lessonByGroup = lessonByGroup;
        this.groupp = groupp;
        this.subgroup = subgroup;
        this.numberOfClass = numberOfClass;
        this.numberOfDay = numberOfDay;
        this.lessonBySubgroups = new ArrayList<LessonBySubgroup>();
    }

    public void addLessonBySubgroup(LessonBySubgroup lessonBySubgroup) {
        lessonBySubgroup.setLesson(this);
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

    public String getLessontype() {
        return lessontype;
    }

    public void setLessontype(String lessontype) {
        this.lessontype = lessontype;
    }

    public String getNumberOfWeek() {
        return numberOfWeek;
    }

    public void setNumberOfWeek(String numberOfWeek) {
        this.numberOfWeek = numberOfWeek;
    }

    public String getLessonByGroup() {
        return lessonByGroup;
    }

    public void setLessonByGroup(String lessonByGroup) {
        this.lessonByGroup = lessonByGroup;
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

    public String getNumberOfClass() {
        return numberOfClass;
    }

    public void setNumberOfClass(String numberOfClass) {
        this.numberOfClass = numberOfClass;
    }

    public String getNumberOfDay() {
        return numberOfDay;
    }

    public void setNumberOfDay(String numberOfDay) {
        this.numberOfDay = numberOfDay;
    }

    public Teachers getTeacher() {
        return teacher;
    }

    public void setTeacher(Teachers teacher) {
        this.teacher = teacher;
    }

    public Auds getAud() {
        return aud;
    }

    public void setAud(Auds aud) {
        this.aud = aud;
    }
}
