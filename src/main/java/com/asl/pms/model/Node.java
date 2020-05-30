package com.asl.pms.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Node parent;
    @Expose
    private String name;
    @SerializedName("text")
    @Expose
    private String url;
    @Expose
    private List<Node> children;
    @Expose
    private String path;
    @Expose
    private boolean selected;
    @Expose
    private boolean opened;
    @Expose
    private boolean leaf;

    public Node(String name, String url, List<Node> children) {
        this.name = name;
        this.url = url;
        this.children = children;
        this.children = new ArrayList<>();
    }

    public Node(String name, String url) {
        this.name = name;
        this.url = url;
        this.children = new ArrayList<>();
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
        this.setPath(this);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private void setPath(Node parent) {
        Node node = parent;
        List<String> stringList = new ArrayList<>();
        while (node != null){
            if(node.isLeaf()){
                stringList.add("**");
            }
            stringList.add(node.url);
            node = node.parent;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(int i=stringList.size()-1; i >= 0; i--){
            if((i==0) || (i==stringList.size() - 1)){
                stringBuilder.append(stringList.get(i));
            }else{
                stringBuilder.append(stringList.get(i)+"/");
            }
        }
        this.path = stringBuilder.toString();
    }

    public Node() {
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (!name.equals(node.name)) return false;
        return url.equals(node.url);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }
}
