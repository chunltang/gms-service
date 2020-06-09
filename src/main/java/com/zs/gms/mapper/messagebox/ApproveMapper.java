package com.zs.gms.mapper.messagebox;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.gms.entity.messagebox.Approve;

public interface ApproveMapper extends BaseMapper<Approve> {

    void deleteOtherApprove();
}
