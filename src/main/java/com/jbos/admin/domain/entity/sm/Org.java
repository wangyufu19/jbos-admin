package com.jbos.admin.domain.entity.sm;

import com.jbos.admin.domain.entity.comm.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Org
 * @author youfu.wang
 * @date 2019-01-31
 */

@Setter
@Getter
public class Org extends BaseEntity {
    /**
     * 根机构id
     */
    public static String ROOTORG_ID="0";

    private String  parentId;

    private String orgCode;

    private String orgName;

}
