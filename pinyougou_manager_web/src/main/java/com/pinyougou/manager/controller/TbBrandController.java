package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.TbBrandService;

import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
//@RestController:@ResponceBody,@Controller

@RestController
@RequestMapping("/tbBrand")
public class TbBrandController {

    @Reference
    private TbBrandService tbBrandService;

    //查询所有品牌
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        List<TbBrand> tbBrandList = tbBrandService.findAll();
        return tbBrandList;
    }

    //品牌分页数据查询
    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNum, Integer pageSize){
        return tbBrandService.findPage(pageNum,pageSize);
    }

    //添加保存品牌
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            tbBrandService.add(tbBrand);
            return new Result(true,"添加成功！");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败！");
        }
    }

    //根据id查询品牌
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return tbBrandService.findOne(id);
    }

    //修改品牌
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            tbBrandService.update(tbBrand);
            return new Result(true,"添加成功！");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败！");
        }
    }

    //删除品牌
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            tbBrandService.delete(ids);
            return new Result(true,"删除成功！");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败！");
        }
    }

    //查询商品模板关联的品牌列表
    @RequestMapping("/selectBrandList")
    public List<Map> selectBrandList(){
        return tbBrandService.selectBrandList();
    }
}
