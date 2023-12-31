package com.jbos.admin.infrastructure.repository.sm.mapper;

import com.jbos.admin.domain.entity.sm.Dep;

import java.util.List;
import java.util.Map;

/**
 * DepMapper
 * @author youfu.wang
 * @date 2020-06-24
 */
public interface DepMapper {
    /**
     * 查询机构下级部门列表
     * @param parameterObject
     * @return
     */
    public List<Dep> getDepList(Map<String, Object> parameterObject);

    /**
     * 查询部门员工数量
     * @param depId
     * @return
     */
    public int getDepEmpCount(String depId);
    /**
     * 新增部门
     * @param parameterObject
     */
    public void addDep(Map<String, Object> parameterObject);
    /**
     * 修改部门
     * @param parameterObject
     */
    public void updateDep(Map<String, Object> parameterObject);
    /**
     * 删除部门
     * @param id
     */
    public void deleteDep(String id);
}
