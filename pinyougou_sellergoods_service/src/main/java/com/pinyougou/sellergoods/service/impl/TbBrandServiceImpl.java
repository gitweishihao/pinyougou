package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.TbBrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TbBrandServiceImpl implements TbBrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    //分页查询品牌
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //执行分页插件pegeheper的静态方法，用于设置分页查询条件
        PageHelper.startPage(pageNum,pageSize);
        //查询所有品牌
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        //封装数据到自定义结果集
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    //添加保存
    @Override
    public void add(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    //查询模板关联的品牌数据列表
    @Override
    public List<Map> selectBrandList() {
        return tbBrandMapper.selectBrandList();
    }
}
