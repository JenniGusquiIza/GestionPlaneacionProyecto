package com.system.services;

import com.system.models.Persona;

import java.util.List;

public interface IPersonaService {
    void save(Persona persona);
    void delete(String personaId);
    Persona getPersona(String personaId);
    List<Persona> getAll();
}