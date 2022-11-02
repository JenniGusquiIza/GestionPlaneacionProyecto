package com.system.services;

import com.system.models.Fase;
import com.system.models.Producto;

public interface IProductoService {
    void save(Producto producto);
    void edit(Producto producto);
    void delete(String codigo);

}
