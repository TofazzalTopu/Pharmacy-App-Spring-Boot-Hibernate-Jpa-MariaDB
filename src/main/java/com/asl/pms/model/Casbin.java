package com.asl.pms.model;

public class Casbin {
    private String url;
    private String methodName;

    public Casbin() {
    }

    public Casbin(String url, String methodName) {
        this.url = url;
        this.methodName = methodName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "Casbin{" +
                "url='" + url + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
