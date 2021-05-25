package services;

import dao.HallwayDAO;
import models.Area;
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
    public List<Hallway> getConnectedHallways(int areaId){
        List<Hallway> hallways = hallwaysDao.findAll();
        hallways.removeIf(h -> (
                h.getStartArea().getId() != areaId) && (h.getEndArea().getId() != areaId)
        );
        return hallways;
    }
    public Area getOtherEnd(Hallway hallway, Integer areaId){
        if(hallway.getStartArea().getId().equals(areaId)){
            return hallway.getEndArea();
        }else {
            return hallway.getStartArea();
        }
    }

    public Classroom getClassroom(String number){ //String corp,
        List<Classroom> classes = hallwaysDao.findClassroomByNumber(number); //corp,
        return classes.get(0);
    }
}
