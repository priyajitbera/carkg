package com.github.priyajitbera.carkg.service.api.service;

import java.util.List;

public interface CreateReadOps<ENTITY, ID, CREATE, RESP_MODEL> {

    RESP_MODEL save(CREATE create);

    List<RESP_MODEL> saveBatch(List<CREATE> creates);

    RESP_MODEL findById(ID id);
}
