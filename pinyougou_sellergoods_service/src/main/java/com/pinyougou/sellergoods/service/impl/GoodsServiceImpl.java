package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加保存
	 * 后台组装
	 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品id，同时也是商品编号',
	 `title` varchar(100) NOT NULL COMMENT '商品标题',   // 商品名称（SPU名称）+ 商品规格选项名称 中间以空格隔开
	 `image` varchar(2000) DEFAULT NULL COMMENT '商品图片',  // 从 tb_goods_desc item_images中获取第一张
	 `categoryId` bigint(10) NOT NULL COMMENT '所属类目，叶子类目',  //三级分类id
	 `create_time` datetime NOT NULL COMMENT '创建时间',
	 `update_time` datetime NOT NULL COMMENT '更新时间',
	 `goods_id` bigint(20) DEFAULT NULL,
	 `seller_id` varchar(30) DEFAULT NULL,
	  //以下字段作用：
	 `category` varchar(200) DEFAULT NULL, //三级分类名称
	 `brand` varchar(100) DEFAULT NULL,//品牌名称
	 `seller` varchar(200) DEFAULT NULL,//商家店铺名称
	 */
	//注入商品扩展dao接口对象
	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;
	//注入商品skudao接口对象
    @Autowired
	private TbItemMapper tbItemMapper;
	//注入分类实体接口对象
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    //注入商家实体接口对象
    @Autowired
    private TbSellerMapper tbSellerMapper;
    //注入品牌实体接口对象
    @Autowired
    private TbBrandMapper tbBrandMapper;



	@Override
	public void add(Goods goods) {
		//设置商品初始状态
		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0");
		goodsMapper.insert(tbGoods);

		//设置商品扩展的商品id
		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		tbGoodsDesc.setGoodsId(tbGoods.getId());
		tbGoodsDescMapper.insert(tbGoodsDesc);

		//判断是否启用规格
        if("1".equals(tbGoods.getIsEnableSpec())) {
            //启用规格

            //获取商品sku集合对象
            List<TbItem> tbItemList = goods.getTbItemList();
            //遍历sku集合对象
            for (TbItem tbItem : tbItemList) {
                //设置商品标题 :  商品名称（SPU名称）+ 商品规格选项名称 中间以空格隔开
                //获取商品spu名称
                String title = tbGoods.getGoodsName();
                //获取商品规格选项名称spec:是json字符串需要转换为json对象
                Map<String, Object> specMap = JSON.parseObject(tbItem.getSpec());
                //遍历map集合key值
                for (String specName : specMap.keySet()) {
                    //根据规格名称获取商品规格选项名称
                    title += " " + specMap.get(specName);
                }
                //向商品sku实体封装title商品标题名称
                tbItem.setTitle(title);

                //调用公共方法
                setItemValues(goods,tbItem);

                //将商品sku查询数据库
                tbItemMapper.insert(tbItem);
            }
        }else{
            //不启用规格，后台封装商品sku实体对象
            TbItem tbItem = new TbItem();
            //封装标题，即spu商品名称
            tbItem.setTitle(goods.getTbGoods().getGoodsName());
            //设置默认价格
            tbItem.setPrice(goods.getTbGoods().getPrice());
            //设置上下架状态
            tbItem.setStatus("1");
            //设置默认状态
            tbItem.setIsDefault("0");
            //设置库存数量
            tbItem.setNum(500);
            //设置规格选项
            tbItem.setSpec("{}");
            //调用公共方法
            setItemValues(goods,tbItem);

            //向数据库插入数据
            tbItemMapper.insert(tbItem);
        }
	}

	//商品录入公共代码
    private void setItemValues(Goods goods,TbItem tbItem){
	    //获取商品spu实体对象
        TbGoods tbGoods = goods.getTbGoods();

        //获取商品扩展实体对象
        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();

        //从商品扩展实体对象中获取图片信息 从 tb_goods_desc item_images中获取第一张
        // [{"color":"红色","url":"http://192.168.25.133/group1/M00/00/01/wKgZhVmHINKADo__AAjlKdWCzvg874.jpg"},
        // {"color":"黑色","url":"http://192.168.25.133/group1/M00/00/01/wKgZhVmHINyAQAXHAAgawLS1G5Y136.jpg"}]
        //将json字符串转换为json集合对象
        List<Map> imagerList = JSON.parseArray(tbGoodsDesc.getItemImages(),Map.class);
        //判断集合是否为空
        if(imagerList.size() > 0){
            //获取第一张图片的完整路径
            String imageUrl = (String) imagerList.get(0).get("url");
            //向商品sku实体封装图片路径
            tbItem.setImage(imageUrl);
        }

        //向商品sku实体封装三级分类id值
        tbItem.setCategoryid(tbGoods.getCategory3Id());

        //根据三级分类的id，获取三级分类的实体对象
        TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
        //向商品sku实体封装三级分类的名称
        tbItem.setCategory(tbItemCat.getName());

        //获取商家id
        String sellerId = tbGoods.getSellerId();
        //向商品sku实体封装商家id
        tbItem.setSellerId(sellerId);

        //根据商家id查询商家实体对象
        TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(sellerId);
        //向商品sku实体封装商家店铺名称
        tbItem.setSeller(tbSeller.getNickName());

        //根据品牌id获取品牌实体对象
        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
        //向商品sku实体封装品牌名称
        tbItem.setBrand(tbBrand.getName());

        //向商品sku实体封装商品spu的id
        tbItem.setGoodsId(tbGoods.getId());

        //向商品sku实体封装创建时间
        tbItem.setCreateTime(new Date());

        //向商品sku实体封装修改时间
        tbItem.setUpdateTime(new Date());

    }

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//不是真的将数据库的商品记录，而是逻辑删除
            //根据id查询spu商品信息
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //设置删除字段为1
            tbGoods.setIsDelete("1");
            //修改数据库商品信息
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		//创建查询条件
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		//只查询非删除状态
		criteria.andIsDeleteIsNull();
		
		if(goods!=null){
			//判断商家id是否为空
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				//修改根据商家id精确查询
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	//商品审核
	@Override
	public void updateStarts(Long[] ids, String starts) {
        //遍历商品id集合
        for (Long id : ids) {
            //根据商品id查询商品信息
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //修改商品审核状态
            tbGoods.setAuditStatus(starts);
            //执行修改
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    @Override
    public void isMarketable(Long[] ids,String isMarketable) {
        //遍历商品id集合
        for (Long id : ids) {
            //根据商品id查询商品spu信息
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //判断商品审核状态
            if("1".equals(tbGoods.getAuditStatus())){
                //已审核状态，商品可以上架
                tbGoods.setIsMarketable(isMarketable);
                //修改数据库
                goodsMapper.updateByPrimaryKey(tbGoods);
            }else{
                //只有审核状态，可以上架，抛出异常
                throw new RuntimeException("只有审核状态的商品，可以上架！");
            }
        }
    }

}
