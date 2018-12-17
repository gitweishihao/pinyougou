package com.pinyougou.content.service.impl;
import java.util.List;

import com.pinyougou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
    private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//缓存数据同步,添加保存后，清空redis缓存
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
	    //修改有可能修改广告分类id，所以先获取修改前,广告分类id
        Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
        //缓存数据同步，修改前，清空redis缓存数据
        redisTemplate.boundHashOps("content").delete(categoryId);
		contentMapper.updateByPrimaryKey(content);
		//判断广告分类id是否发生变化，如果发生变化，需要清空修改后的广告分类对应的缓存数据
        if(content.getCategoryId().longValue()!= content.getCategoryId().longValue()){
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
		    //根据广告id查询广告分类id
            TbContent tbContent = contentMapper.selectByPrimaryKey(id);
            //缓存数据同步
            redisTemplate.boundHashOps("content").delete(tbContent.getCategoryId());
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	//根据广告分类id查询广告列表
	@Override
	public List<TbContent> findCategoryId(Long categoryId) {

	    //先根据广告分类id从redis数据库读取缓存广告列表数据
        List<TbContent> contentRedisList = (List<TbContent>)redisTemplate.boundHashOps("content").get(categoryId);
        //判断缓存中的广告列表数据是否为空
        if(contentRedisList == null){
            //缓存中没有数据，就去数据库中查询
            //创建查询条件对象
            TbContentExample example = new TbContentExample();
            //根据广告分类id查询广告列表
            Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);
            //只获取广告状态开启的广告列表
            criteria.andStatusEqualTo("1");
            //按照广告排序查询
            example.setOrderByClause("sort_order");
            List<TbContent> contentList = contentMapper.selectByExample(example);
            redisTemplate.boundHashOps("content").put(categoryId,contentList);
            System.out.println("来自mysql数据库！");
        }else{
            System.out.println("来自redis缓存！");
        }
        return contentRedisList;
	}

}
