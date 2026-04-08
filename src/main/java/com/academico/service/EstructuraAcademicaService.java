package com.academico.service;

import com.academico.dao.ActividadGrupoDAO;
import com.academico.model.ActividadGrupo;

import java.math.BigDecimal;
import java.sql.SQLException;

public class EstructuraAcademicaService {

    private final ActividadGrupoDAO actividadDAO;

    public EstructuraAcademicaService() {
        this.actividadDAO = new ActividadGrupoDAO();
    }

    public boolean puedeAgregarActividad(int grupoId, int unidadId, BigDecimal nuevaPonderacion) throws SQLException {
        BigDecimal sumaActual = actividadDAO.sumaPonderaciones(grupoId, unidadId);
        
        BigDecimal sumaSimulada = sumaActual.add(nuevaPonderacion);
        
        return sumaSimulada.compareTo(new BigDecimal("100.00")) <= 0;
    }

    public ActividadGrupo guardarActividad(ActividadGrupo actividad) throws SQLException {
        boolean esValido = puedeAgregarActividad(
                actividad.getGrupoId(), 
                actividad.getUnidadId(), 
                actividad.getPonderacion()
        );

        if (!esValido) {
            throw new IllegalArgumentException("Error: La suma de ponderaciones de la unidad excedería el 100%.");
        }

        return actividadDAO.insertar(actividad);
    }
}