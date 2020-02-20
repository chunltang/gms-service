package com.zs.gms.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import org.apache.commons.lang3.StringUtils;

public class SortUtil {

    /**
     * 分页排序
     * @param defaultSortField 默认排序字段
     * @param defaultSortType  默认排序类型
     * */
    public static void handlePageSort(QueryRequest request, Page page, String defaultSortType, String defaultSortField){
        page.setCurrent(request.getPageNo());
        page.setSize(request.getPageSize());
        String sortField = request.getField();
        if(StringUtils.isNotBlank(request.getField()) &&StringUtils.isNotBlank(request.getSortType())
           &&!StringUtils.equalsIgnoreCase(request.getField(),"null")
            &&!StringUtils.equalsIgnoreCase(request.getSortType(),"null")){
              if(StringUtils.equals(defaultSortType, GmsConstant.SORT_ASC)){
                   page.setAsc(sortField);
              }else{
                  page.setDesc(sortField);
              }
        }else{
            if(StringUtils.isNotBlank(defaultSortField)){
                if(StringUtils.equals(defaultSortType, GmsConstant.SORT_ASC)){
                    page.setAsc(defaultSortField);
                }else{
                    page.setDesc(defaultSortField);
                }
            }
        }
    }
}
