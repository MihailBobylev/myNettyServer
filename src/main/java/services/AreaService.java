package services;

import dao.AreaDAO;
import models.Area;
import models.Hallway;

import java.util.List;

public class AreaService {
    private AreaDAO areaDAO = new AreaDAO();

    public AreaService() {
    }

    public Area findArea(int id) {
        return areaDAO.findAreaById(id);
    }

    public void saveArea(Area area) {
        areaDAO.save(area);
    }

    public void deleteArea(Area area) {
        areaDAO.delete(area);
    }

    public void updateArea(Area area) {
        areaDAO.update(area);
    }

    public List<Area> findAllAreas() {
        return areaDAO.findAll();
    }

    public Hallway findHallwayById(int id) {
        return areaDAO.findHallwayById(id);
    }
}
