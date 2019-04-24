package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.group.Specification;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//1.添加规格内容
		specificationMapper.insert(specification.getSpec());
		//2.添加规格选项，
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			//2.1)为规格选项设置其specId值
			option.setSpecId(specification.getSpec().getId());
			//2.2)在tb_specificationOption表中添加记录
			specificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//1.修改规格表
		specificationMapper.updateByPrimaryKey(specification.getSpec());
		//2.修改规格选项表的思路，就是根据规格id删除原来的规格选项，再重新添加
		//2.1)根据规格id删除原来的规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		//2.1.1)添加实例条件
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpec().getId());
		//2.1.2)执行删除规格列表
		specificationOptionMapper.deleteByExample(example);

		//2.2)再根据规格选项id在规格选项表中添加数据
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			option.setSpecId(specification.getSpec().getId());
			specificationOptionMapper.insert(option);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//1.查询得到规格对象
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		//2.根据上面的规格对象查询出规格选项列表
		//2.1)定义规格选项查询的实例
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		//2.2)定义查询实例的查询条件
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		//2.3)添加查询条件
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
		//3.构造出Specification对象
		Specification specification = new Specification();
		specification.setSpec(tbSpecification);
		specification.setSpecificationOptionList(specificationOptionList);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//1.删除规格表
			specificationMapper.deleteByPrimaryKey(id);
			//2.删除规格选项表
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> findSpecList() {
		return specificationMapper.findSpecList();
	}

}
