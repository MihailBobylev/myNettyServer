package services;

import dao.AreaDAO;
import dao.AudsDAO;
import models.Area;
import models.Auds;
import models.Hallway;
import models.Students;

import java.util.List;

public class AudsService {
    private AudsDAO audsDAO = new AudsDAO();

    public AudsService() {
    }

    public Auds findAuds(int id) {
        return audsDAO.findAudsById(id);
    }

    public void saveAud(Auds aud) {
        audsDAO.save(aud);
    }

    public void deleteAud(Auds aud) {
        audsDAO.delete(aud);
    }

    public void updateAud(Auds aud) {
        audsDAO.update(aud);
    }

    public List<Auds> findAllAuds() {
        return audsDAO.findAll();
    }

    public List<Auds> findByAud(String corp, String aud) {
        return audsDAO.findByAud(corp, aud);
    }
}
