package com.asl.pms.model;

import java.util.ArrayList;
import java.util.List;

public class CasbinRole {
    private String roleName;
    private List<String> urlName = new ArrayList<>();
    private Long organizationId;
    private Long branchId;

    public CasbinRole() {
    }

    public CasbinRole(String roleName, List<String> urlName) {
        this.roleName = roleName;
        this.urlName = urlName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getUrlName() {
        return urlName;
    }

    public void setUrlName(List<String> urlName) {
        this.urlName = urlName;
    }

    @Override
    public String toString() {
        return "CasbinRole{" +
                "roleName='" + roleName + '\'' +
                ", urlName=" + urlName +
                '}';
    }
}
