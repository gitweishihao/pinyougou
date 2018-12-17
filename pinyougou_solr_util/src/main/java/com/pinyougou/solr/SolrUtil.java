package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//使用注解@Component:创建solrUtil对象，并添加到springIOC容器中
@Component
public class SolrUtil {

    //注入商品skudao接口对象
    @Autowired
    private TbItemMapper tbItemMapper;
    //注入spring-data-solr模板对象
    @Autowired
    private SolrTemplate solrTemplate;

    //从数据库中查询满足条件的商品数据列表
    public void importItemData(){
        //创建数据库查询条件
        List<TbItem> itemList = tbItemMapper.findAllGrounding();
        //遍历集合
        for (TbItem tbItem : itemList) {
            //获取商品sku的规格选项
            String spec = tbItem.getSpec();
            //将json字符串转换为map对象
            Map map = JSON.parseObject(spec, Map.class);
            //将map集合数据设置给sku实体对象
            tbItem.setSpecMap(map);
        }
        //将查询到的所有商品数据添加保存到solr索引库中
        solrTemplate.saveBeans(itemList);
        //提交数据
        solrTemplate.commit();
    }

}
