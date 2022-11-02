package com.system.services;

import com.system.models.Fase;
import com.system.models.Grupo;

public interface IGrupoService {

    void save(Grupo grupo);
    void edit(Grupo grupo);
    void delete(String codigo);
    long max();
}
