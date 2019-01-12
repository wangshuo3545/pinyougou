package com.pinyougou.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果实体
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-01-12<p>
 */
public class PageResult implements Serializable{
    // {total : 100, rows : [{},{}]}
    // 定义总记录数
    private long total;
    // 定义分页数据
    private List<?> rows;

    /** 定义构造器 */
    public PageResult(){}
    public PageResult(long total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}