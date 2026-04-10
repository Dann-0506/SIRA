package com.academico.service.individuals;

import com.academico.dao.MateriaDAO;
import com.academico.model.Materia;
import com.academico.dao.UnidadDAO;
import com.academico.model.Unidad;

import java.sql.SQLException;
import java.util.List;

public class MateriaService {
    private final MateriaDAO materiaDAO = new MateriaDAO();
    private final UnidadDAO unidadDAO = new UnidadDAO();

    public List<Materia> listarTodas() throws Exception {
        try { 
            return materiaDAO.findAll(); 
        } catch (SQLException e) { 
            throw new Exception("Error al cargar materias."); 
        }
    }

    public void guardar(Materia materia, boolean esEdicion) throws Exception {
        if (materia.getClave() == null || materia.getClave().isBlank() || materia.getNombre() == null || materia.getNombre().isBlank()) {
            throw new IllegalArgumentException("La clave y el nombre de la materia son obligatorios.");
        }
        if (materia.getTotalUnidades() <= 0) throw new Exception("Mínimo 1 unidad por materia.");
        
        try {
            if (esEdicion) {
                materiaDAO.actualizar(materia);
            } else {
                // 1. Guardamos la materia y obtenemos el objeto con su ID real
                Materia mGuardada = materiaDAO.insertar(materia);
                
                // 2. Generamos las unidades individualmente (Ya es seguro porque la BD está arreglada)
                for (int i = 1; i <= mGuardada.getTotalUnidades(); i++) {
                    Unidad u = new Unidad();
                    u.setMateriaId(mGuardada.getId());
                    u.setNumero(i);
                    u.setNombre("Unidad " + i);
                    unidadDAO.insertar(u);
                }
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) throw new Exception("La clave de materia ya existe.");
            throw new Exception("Error al guardar la materia.");
        }
    }
    
    public void eliminar(int id) throws Exception {
        try {
            materiaDAO.eliminar(id);
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new Exception("No se puede eliminar: Esta materia ya está asignada a uno o más grupos.");
            }
            throw new Exception("Error al intentar eliminar la materia.");
        }
    }
}