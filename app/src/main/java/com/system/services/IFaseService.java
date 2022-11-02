package com.system.services;

import com.system.models.Fase;

public interface IFaseService {

    void save(Fase fase);
    void edit(Fase fase);
    void delete(String codigo);
    long max();
}
