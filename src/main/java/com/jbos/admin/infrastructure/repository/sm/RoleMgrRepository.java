package com.jbos.admin.infrastructure.repository.sm;

import com.jbos.admin.domain.entity.comm.TreeNode;
import com.jbos.admin.domain.entity.sm.Emp;
import com.jbos.admin.domain.entity.sm.Role;
import com.jbos.admin.domain.entity.sm.RoleFunc;
import com.jbos.admin.domain.entity.sm.UserRole;
import com.jbos.admin.common.page.PageParam;
import com.jbos.admin.common.page.Paging;
import com.jbos.admin.infrastructure.repository.sm.mapper.RoleMapper;
import com.jbos.admin.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RoleMgrRepository
 * @author youfu.wang
 * @date 2020-06-24
 */
@Component
public class RoleMgrRepository {
    @Autowired
    private RoleMapper roleMapper;
    /**
     * 查询下级角色
     * @param parentId
     * @return
     */
    public List<TreeNode> getRoleChildrenNode(String parentId){
        List<TreeNode> roleChildren=null;
        roleChildren=roleMapper.getRoleChildrenNode(parentId);
        return roleChildren;
    }
    /**
     * 查询角色数据列表
     * @param parameterObject
     * @return
     */
    @Paging
    public List<Role> getRoleList(PageParam pageParam, Map<String, Object> parameterObject) {
        List<Role> roles=null;
        roles=roleMapper.getRoleList(parameterObject);
        return roles;
    }

    /**
     * 查询角色功能数据
     * @param roleId
     * @return
     */
    public List<String> getRoleFuncs(String roleId){
        List<String> roleFuncs=new ArrayList<String>();
        roleFuncs=roleMapper.getRoleFuncs(roleId);
        return roleFuncs;
    }
    /**
     * 保存角色功能
     * @param parameterObject
     */
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
    public void saveRoleFuncs(Map<String, Object> parameterObject){
        //删除角色功能
        String roleId= StringUtils.replaceNull(parameterObject.get("roleId"));
        roleMapper.deleteRoleFunc(roleId);
        //新增角色功能
        ArrayList funcIds=(ArrayList)parameterObject.get("checkedIds");
        if(null!=funcIds){
            List<RoleFunc> roleFuncs=new ArrayList<RoleFunc>();
            for(Object funcId:funcIds){
                RoleFunc roleFunc=new RoleFunc();
                roleFunc.setRoleId(roleId);
                roleFunc.setFuncId(StringUtils.replaceNull(funcId));
                roleFuncs.add(roleFunc);
            }
            roleMapper.insertRoleFunc(roleFuncs);
        }
    }
    /**
     * 新增角色
     * @param parameterObject
     */
    public void insertRole(Map<String, Object> parameterObject) {
        roleMapper.insertRole(parameterObject);
    }

    /**
     * 修改角色
     * @param parameterObject
     */
    public void updateRole(Map<String, Object> parameterObject) {
        roleMapper.updateRole(parameterObject);
    }
    /**
     * 删除角色
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
    public void deleteRole(String id) {
        //删除角色
        roleMapper.deleteRole(id);
        //删除角色功能
        roleMapper.deleteRoleFunc(id);
    }
    /**
     * 查询角色用户列表
     * @param parameterObject
     * @return
     */
    @Paging
    public List<Emp> getRoleEmpList(PageParam pageParam, Map<String, Object> parameterObject){
        return roleMapper.getRoleEmpList(parameterObject);
    }
    /**
     * 查询选择角色用户列表
     * @param parameterObject
     * @return
     */
    @Paging
    public List<Emp> getSelectRoleEmpList(PageParam pageParam,Map<String, Object> parameterObject){
        return roleMapper.getSelectRoleEmpList(parameterObject);
    }
    /**
     * 新增角色用户
     * @param parameterObject
     */
    public void addRoleUser(Map<String, Object> parameterObject){
        String roleId= StringUtils.replaceNull(parameterObject.get("roleId"));
        //新增角色用户
        ArrayList emps=(ArrayList)parameterObject.get("emps");
        if(null!=emps){
            List<UserRole> userRoleList=new ArrayList<UserRole>();
            for(Object emp:emps){
                UserRole userRole=new UserRole();
                userRole.setRoleId(roleId);
                userRole.setUserId(StringUtils.replaceNull(((HashMap<String,String>)emp).get("id")));
                userRoleList.add(userRole);
            }
            roleMapper.insertRoleUser(userRoleList);
        }
    }
    /**
     * 删除角色用户
     * @param parameterObject
     */
    public void deleteRoleUser(Map<String, Object> parameterObject){
        roleMapper.deleteRoleUser(parameterObject);
    }
}
