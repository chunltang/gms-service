package com.zs.gms.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

@Slf4j
public class SortUtil {

    /**
     * 分页排序
     *
     * @param defaultSortField 默认排序字段
     * @param defaultSortType  默认排序类型
     */
    public static void handlePageSort(QueryRequest request, Page page, String defaultSortType, String defaultSortField) {
        if (null == request) {
            request = new QueryRequest();
        }
        page.setCurrent(request.getPageNo());
        page.setSize(request.getPageSize());
        String sortField = request.getField();
        if (StringUtils.isNotBlank(request.getField()) && StringUtils.isNotBlank(request.getSortType())
                && !StringUtils.equalsIgnoreCase(request.getField(), "null")
                && !StringUtils.equalsIgnoreCase(request.getSortType(), "null")) {
            if (StringUtils.equals(defaultSortType, GmsConstant.SORT_ASC)) {
                page.setAsc(sortField);
            } else {
                page.setDesc(sortField);
            }
        } else {
            if (StringUtils.isNotBlank(defaultSortField)) {
                if (StringUtils.equals(defaultSortType, GmsConstant.SORT_ASC)) {
                    page.setAsc(defaultSortField);
                } else {
                    page.setDesc(defaultSortField);
                }
            }
        }
    }

    /**
     * 内存分页排序
     * @param sortFiledMap 排序字段，排序类型
     * @param data 排序集合
     * @param tClass 排序对象
     */
    public static <T> List<T> memoryPageSort(int pageNo, int pageSize, Map<String, String> sortFiledMap, List<T> data, Class<T> tClass) {
        Set<String> sortFieldDesc = sortFiledMap.keySet();
        List<Comparator<T>> comparators = new ArrayList<>();
        try {
            for (String fieldDesc : sortFieldDesc) {
                Field field = tClass.getDeclaredField(fieldDesc);
                field.setAccessible(true);
                Function<T, String> func = u -> {
                    try {
                        Object obj = field.get(u);
                        return obj.toString();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return null;
                };
                Comparator<T> comparing = Comparator.comparing(func,(o1,o2)->{
                    if(sortFiledMap.get(fieldDesc).equals(GmsConstant.SORT_ASC)){
                        return o1.compareTo(o2);
                    }else{
                        return o2.compareTo(o1);
                    }
                });
                comparators.add(comparing);
            }
        } catch (Exception e) {
            log.error("属性反射异常!", e);
        }
        if (comparators.size() > 0) {
            Optional<Comparator<T>> reduce = comparators.stream().reduce(Comparator::thenComparing);
            Collections.sort(data, reduce.get());
        }
        int beginIndex = (pageNo - 1) * pageSize;
        int endIndex = pageNo * pageSize;
        return data.subList(beginIndex, endIndex > data.size() ? data.size() : endIndex);
    }

    public static <T> Page<T> getPage(QueryRequest request,LinkedHashMap<String, Boolean> sortFiledMap,Class<T> clazz){
        Page<T> page = new Page<>();
        List<OrderItem> orders=new ArrayList<>();
        for (String field : sortFiledMap.keySet()) {
            orders.add(new OrderItem().setColumn(field).setAsc(sortFiledMap.get(field)));
        }
        page.setCurrent(request.getPageNo());
        page.setSize(request.getPageSize());
        page.setOrders(orders);
        return page;
    }
}
