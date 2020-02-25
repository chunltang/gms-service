package com.zs.gms.service.client;

import com.zs.gms.entity.client.Excavator;

public interface ExcavatorService {

    void addExcavator(Excavator excavator);

    void delExcavator(Integer excavatorId);

    void updateExcavator(Excavator excavator);

    Excavator getExcavatorByNo(Integer excavatorNo);

    boolean isExistNo(Integer excavatorNo);

    boolean isExistId(Integer excavatorId);
}
