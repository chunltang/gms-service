package com.zs.gms.enums.mapmanager;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.EnumUtils;

public enum AreaTypeEnum implements IEnum {
    LOAD_AREA("9","LoadArea","装载区"),
    UNLOAD_MINERAL_AREA("2","UnloadMineralArea","卸矿区"),
    UNLOAD_WASTE_AREA("3","UnloadWasteArea","卸土区"),
    PARKING_LOT("4","ParkingLot","停车场"),
    PETORL_STATION("5","PetrolStation","加油区"),
    WATER_STATION("6","WaterStation","加水区"),
    ROAD("0","Road","路"),
    JUNCTION("1","Junction","连接点"),
    PASSABLE_AREA("7","PassableArea","可通行区域"),
    IMPASSABLE_AREA("8","ImpassableArea","不可通行区域");

    private String value;//地图需要的值

    private String numValue;//服务层对应的值

    private String desc;

    private AreaTypeEnum( String numValue,String value, String desc){
        this.numValue=numValue;
        this.value=value;
        this.desc=desc;
    };

    @Override
    @JsonValue
    public String getValue(){
        return value;
    }

    public String getNumValue(){
        return numValue;
    }


    public String getDesc(){
        return desc;
    }
}
