package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface TbBrandService {

    List<TbBrand> findAll();

    PageResult findPage(Integer pageNum, Integer pageSize);

    void add(TbBrand tbBrand);

    TbBrand findOne(Long id);

    void update(TbBrand tbBrand);

    void delete(Long[] ids);

    List<Map> selectBrandList();
}
