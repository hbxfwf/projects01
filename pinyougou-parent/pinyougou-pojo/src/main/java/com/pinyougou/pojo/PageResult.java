package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/2/25 09:10
 */
public class PageResult implements Serializable {
    private long total;     //总记录数
    private List rows;      //每一页记录集合

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
    public PageResult(){}
    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
