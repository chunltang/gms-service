package com.zs.gms.service.client;

import com.zs.gms.entity.client.Excavator;

import java.util.List;

public interface ExcavatorService {

    void addExcavator(Excavator excavator);

    List<Excavator> getExcavators();

    void delExcavator(Integer excavatorId);

    void updateExcavator(Excavator excavator);

    Excavator getExcavatorByNo(Integer excavatorNo);

    boolean isExistNo(Integer excavatorNo);

    boolean isExistId(Integer excavatorId);
}
