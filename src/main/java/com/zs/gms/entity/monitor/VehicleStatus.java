package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VehicleStatus implements Serializable {

    public Integer vehicleId;

    public Object status;

    public Date createTime;
}
