package com.pinyougou.manager.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import entity.Result;
import groupEntity.Specification;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    //分页查询所有
    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNum, Integer pageSize){
        return specificationService.findPage(pageNum,pageSize);
    }
    //条件分页查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSpecification tbSpecification,
                             Integer pageNum ,Integer pageSize){
       return specificationService.search(tbSpecification,pageNum,pageSize);
    }
    //添加保存
    @RequestMapping("/add")
    public Result add(@RequestBody Specification specification){
        try {
            //添加成功
            specificationService.add(specification);
            return new Result(true,"添加成功！");
        }catch (Exception e){
            //添加失败
            e.printStackTrace();
            return new Result(false,"添加失败！");
        }
    }

    //根据id查询，用于数据回显
    @RequestMapping("/findOne")
    public Specification findOne(Long id){
        return specificationService.findOne(id);
    }

    //修改保存
    @RequestMapping("/update")
    public Result update(@RequestBody Specification specification){
        try {
            //添加成功
            specificationService.uqdate(specification);
            return new Result(true,"修改成功！");
        }catch (Exception e){
            //添加失败
            e.printStackTrace();
            return new Result(false,"修改失败！");
        }
    }

    //根据复选框选中的id数组删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){

        try {
            //添加成功
            specificationService.delete(ids);
            return new Result(true,"删除成功！");
        }catch (Exception e){
            //添加失败
            e.printStackTrace();
            return new Result(false,"删除失败！");
        }
    }

    //查询模板关联规格数据列表
    @RequestMapping("/selectSpecificationList")
    public List<Map> selectSpecificationList(){
        return specificationService.selectSpecification();
    }

}
