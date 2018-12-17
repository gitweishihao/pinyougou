package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;

import entity.PageResult;
import groupEntity.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    PageResult search(TbSpecification tbSpecification, Integer pageNum, Integer pageSize);

    PageResult findPage(Integer pageNum, Integer pageSize);

    void add(Specification specification);

    void uqdate(Specification specification);

    Specification findOne(Long id);

    void delete(Long[] ids);

    List<Map> selectSpecification();
}
