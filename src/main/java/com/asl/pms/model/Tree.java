package com.asl.pms.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree {
    private Node root;

    public Tree(Node root) {
        this.root = root;
    }

    public static Tree parse(List<Casbin> casbinList, List<String> selectedList){
        Map<String, Node> tree = parse(casbinList);

        for(String segment: selectedList){
            String[] segments = segment.split("/");
            for(String seg: segments){
                if(tree.containsKey(seg)){
                    Node node = tree.get(seg);
                    node.setSelected(true);
//                    node.setOpened(true);
                }
            }
        }
        Node root = tree.get("/");
        return new Tree(root);
    }

    public static Tree parseWithoutSelected(List<Casbin> casbinList){
        Map<String, Node> tree = parse(casbinList);
        Node root = tree.get("/");
        return new Tree(root);
    }

    public static Map<String, Node> parse(List<Casbin> casbinList){
        Map<String, Node> tree = new HashMap<>();

        for(Casbin casbin: casbinList){
            String url = casbin.getUrl();
            String[] segments = url.split("/");
            int index = 0;
            boolean flag = true;

            for(String seg: segments){
                if(seg.startsWith("{") && seg.endsWith("}")){
                    if (!flag) continue;
                    if(flag){
                        int firstIndex = seg.indexOf("{");
                        int lastIndex = seg.indexOf("}");
                        lastIndex++;

                        String subString = seg.substring(firstIndex, lastIndex);
                        seg = seg.replace(subString, "**");
                        flag = false;
                    }

                }
                Node current = new Node("", seg);
                if ("".equals(seg)) {
                    segments[index] = "/";
                    seg = "/";
                    current.setUrl(seg);

                }

                if(!tree.containsKey(seg)){
                    tree.put(seg, current);
                }

                if(index > 0){
                    String segment = segments[index - 1];
                    Node previous = tree.get(segment);
                    if(previous==null) {
                    	System.out.println("segment: "+segment);
                    }else {
                    	previous.setLeaf(false);
                    	 current.setParent(previous);
                         List<Node> children = previous.getChildren();

                         if (!children.contains(current)) {
                             children.add(current);
                         }
                    }
                    	
                    

                   

                }
                index++;
            }
            if(segments.length > 0){
                String last = segments[segments.length - 1];
                if(tree.get(last) != null)
                    tree.get(last).setLeaf(true);
            }

        }

       return tree;
    }

    public Node getRoot() {
        return this.root;
    }
}
