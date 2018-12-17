package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import groupEntity.Specification;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(TbSpecification tbSpecification, Integer pageNum, Integer pageSize) {
        //执行mybatis分页插件pageheper的静态方法，用于设置条件
        PageHelper.startPage(pageNum,pageSize);
        //调用dao，根据规格名称执行条件查询
        //创建查询条件对象
        TbSpecificationExample example = new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria = example.createCriteria();

        //判断规格实体类是否为空
        if(tbSpecification != null){
            //获取规格名称
            String specName = tbSpecification.getSpecName();
            //判断规格名称是否为空
            if(specName != null && !"".equals(specName)){
                //指定根据规格名称(前台输入的关键字)执行模糊查询
                criteria.andSpecNameLike("%"+specName+"%");
            }
        }
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);

        return new PageResult(page.getTotal(),page.getResult());
    }

    //分页查询所有
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //执行分页插件的静态方法
        PageHelper.startPage(pageNum,pageSize);
        //执行dao查询所有
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //添加保存
    @Override
    public void add(Specification specification) {
        //保存规格
        //获取规格实体类对象
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.insert(tbSpecification);

        //保存规格选项
        //获取规格选项集合
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        //遍历集合
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            //关联规格，将规格实体id赋给规格选项的外键id
            tbSpecificationOption.setSpecId(tbSpecification.getId());
            //调用dao执行添加
            specificationOptionMapper.insert(tbSpecificationOption);
        }
    }

    //修改保存
    @Override
    public void uqdate(Specification specification) {
        //修改规格数据
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

        //更新规格选项列表， 思路：先删除原有的规格列表，再重新保存修改后的规格数据
        //创建条件对象
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        //创建条件选项对象
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //根据规格id删除规格选项
        criteria.andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(example);

        //保存规格选项
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        //遍历
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            //关联规格
            tbSpecificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(tbSpecificationOption);
        }

    }

    //根据id查询规格中间表实体
    @Override
    public Specification findOne(Long id) {

        Specification specification = new Specification();
        //查询规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //将规格实体封装给规格中间实体
        specification.setTbSpecification(tbSpecification);

        //查询规格选项
        //创建查询条件对象
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //根据规格id查询规格选项
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
        //将规格选项集合封装给规格中间体
        specification.setSpecificationOptionList(tbSpecificationOptions);

        return specification;
    }

    @Override
    public void delete(Long[] ids) {
        //遍历数据
        for (Long id : ids) {
            //删除规格
            specificationMapper.deleteByPrimaryKey(id);

            //删除规格选项
            //创建删除条件对象
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            //根据规格id删除规格选项
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }

    @Override
    public List<Map> selectSpecification() {
        return specificationMapper.selectSpecificationList();
    }
}
