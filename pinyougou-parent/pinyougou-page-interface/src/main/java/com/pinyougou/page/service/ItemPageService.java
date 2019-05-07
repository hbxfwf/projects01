package com.pinyougou.page.service;

import java.io.IOException;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/7 09:50
 */
public interface ItemPageService {
    /**
     *  生成商品详细页
     *  @param goodsId
     *  */
    public boolean genItemHtml(Long goodsId);
}
