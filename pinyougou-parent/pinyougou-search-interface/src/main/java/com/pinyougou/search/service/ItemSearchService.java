package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/30 10:34
 */
public interface ItemSearchService {
    /**
     * 搜索
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 导入数据
     * @param list
     */
    public void importList(List list);

    public void deleteGoodsId(List goodsIds);
}
