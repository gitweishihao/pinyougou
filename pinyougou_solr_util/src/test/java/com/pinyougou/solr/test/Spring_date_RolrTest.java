package com.pinyougou.solr.test;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.solr.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class Spring_date_RolrTest {

    //注入spring-data-solr提供的全文检索模板
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrUtil solrUtil;

    //添加商品到索引库
    @Test
    public void addTest(){

        //创建商品sku实体对象
        TbItem tbItem = new TbItem();
        //模拟添加商品信息数据
        tbItem.setId(1L);
        tbItem.setTitle("华为maxs 移动4G,64G");
        tbItem.setSeller("华为旗舰店");
        tbItem.setBrand("华为");

        //向solr索引库添加商品信息
        solrTemplate.saveBean(tbItem);

        //提交数据(必须提交)
        solrTemplate.commit();
    }

    //修改商品索引库
    @Test
    public void setTest(){
        //创建商品sku实体对象
        TbItem tbItem = new TbItem();
        //模拟修改商品数据
        tbItem.setId(1L);
        tbItem.setTitle("华为mate 全网通4G,64G");
        tbItem.setSeller("华为连锁店");
        tbItem.setBrand("华为mate");
        tbItem.setPrice(new BigDecimal(2999));

        //修改索引库商品数据
        solrTemplate.saveBean(tbItem);

        //提交数据
        solrTemplate.commit();
    }

    //根据主键删除商品数据
    @Test
    public void deleteTest(){
        //删除索引库商品数据
        //根据主键删除商品数据
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    //根据主键id查询
    @Test
    public void findById(){
        TbItem tbItem = solrTemplate.getById(1L, TbItem.class);
        System.out.println("商品标题:"+tbItem.getTitle()+"商品价格:"+tbItem.getPrice()+"商家信息:"+tbItem.getSeller());
    }

    //循环插入100条测试数据
    @Test
    public void testAddList(){
        //创建集合对象
        List<TbItem> tbItemList = new ArrayList<>();

        for (int i = 0; i <100; i++) {
            //创建商品sku实体对象
            TbItem tbItem = new TbItem();
            //模拟修改商品数据
            tbItem.setId(i+1L);
            tbItem.setTitle("华为mate"+(i+1)+" 全网通4G,64G");
            tbItem.setSeller("华为第"+(i+1)+"连锁店");
            tbItem.setBrand("华为mate"+i);
            tbItem.setPrice(new BigDecimal(2999));
            //将一条商品数据插入到list集合中
            tbItemList.add(tbItem);
        }
        solrTemplate.saveBeans(tbItemList);
        //提交数据
        solrTemplate.commit();
    }

    //分页查询
    @Test
    public void testPageQuery() {
        //创建查询所有对象(*:*：*表示查询所有域字段,*：表示查询内容)
        Query query = new SimpleQuery("*:*");

        //设置分页条件(起始索引，每页显示几条)
        query.setOffset(2);     //从第二条开始查询
        query.setRows(5);       //每页显示5条数据
        //执行分页查询(分页查询是在查询所有的条件进行的查询)
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

        System.out.println("总记录数："+page.getTotalElements()+"总页数："+page.getTotalPages());
        //遍历分页查询展示内容数据
        List<TbItem> contentList = page.getContent();
        showList(contentList);

    }


    //分页查询内容展示
    private void showList(List<TbItem> list){
        for (TbItem tbItem : list) {
            System.out.println("商品标题："+tbItem.getTitle()+"商家："+tbItem.getSeller());
        }
    }

    //条件查询
    @Test
    public void queryMultiTest(){
        //创建查询对象
        Query query = new SimpleQuery();
        //创建查询条件,spring-data-solr提供的查询条件对象,支持链式编程 fieldname:域字段名称
        Criteria criteria = new Criteria("item_title").contains("5").and("item_seller").contains("9");
        //将查询条件设置给查询对象
        query.addCriteria(criteria);
        //执行条件查询
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数："+page.getTotalElements()+"总页数："+page.getTotalPages());
        //获取查询到的内容
        List<TbItem> content = page.getContent();
        //调用方法展示
        showList(content);
    }

    //删除所有
    @Test
    public void deleteQueryTest(){
        //创建查询所有对象
        Query query = new SimpleQuery("*:*");
        //执行删除所有
        solrTemplate.delete(query);
        //提交数据
        solrTemplate.commit();
    }

    //添加保存
    @Test
    public void dataImportTest(){
        solrUtil.importItemData();
    }
}
