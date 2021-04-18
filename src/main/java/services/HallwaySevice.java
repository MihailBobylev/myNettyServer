package services;

import dao.HallwayDAO;
import models.Classroom;
import models.Hallway;

import java.util.List;

public class HallwaySevice {
    private HallwayDAO hallwaysDao = new HallwayDAO();

    public HallwaySevice() {
    }

    public Hallway findHallway(int id) {
        return hallwaysDao.findHallwayById(id);
    }

    public void saveHallway(Hallway hallway) {
        hallwaysDao.save(hallway);
    }

    public void deleteHallway(Hallway hallway) {
        hallwaysDao.delete(hallway);
    }

    public void updateHallway(Hallway hallway) {
        hallwaysDao.update(hallway);
    }

    public List<Hallway> findAllHallways() {
        return hallwaysDao.findAll();
    }

    public Classroom findClassroomById(int id) {
        return hallwaysDao.findClassroomById(id);
    }
}
