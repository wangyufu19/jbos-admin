package com.jbos.admin.common.office.excel;

import com.jbos.admin.common.response.ResponseResult;

import java.util.Map;

/**
 * IPageExcel
 * @author youfu.wang
 * @date 2023/4/12
 **/
public interface IPageExcel {
    /**
     * 每页写入行数
     */
    public int length=5000;

    /**
     * 得到工作表行数据列表
     * @param params
     * @return
     */
    public ResponseResult getSheetRowDataList(Map<String, Object> params);
}
