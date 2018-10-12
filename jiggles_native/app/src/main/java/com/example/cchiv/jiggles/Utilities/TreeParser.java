package com.example.cchiv.jiggles.utilities;

import com.example.cchiv.jiggles.model.Comm;

import java.util.ArrayList;
import java.util.List;

public class TreeParser {

    private static final String TAG = "TreeParser";

    private void depthQuery(List<Node> subspace, List<Comm> comments, int depth, int count) {
        int len = comments.size();

        for(int it = 0; it < len; it++) {
            if(comments.get(it-count).getDepth() == depth) {
                Comm comment = comments.remove(it-count);

                Node node = new Node(comment);
                subspace.add(node);

                count++;
            }
        }
    }

    public class Node {

        private Comm comment;
        private List<Node> children = new ArrayList<>();

        public Node(Comm comment) {
            this.comment = comment;
        }
    }

    public class Branch {

        private List<Node> parent = new ArrayList<>();
        private List<Node> child = new ArrayList<>();

        public Branch() {}

        public Branch(List<Node> parent, List<Node> child) {
            this.parent = parent;
            this.child = child;
        }

        public List<Node> getParent() {
            return parent;
        }

        public List<Node> getChild() {
            return child;
        }

        public void setParent(List<Node> parent) {
            this.parent = parent;
        }

        public void setChild(List<Node> child) {
            this.child = child;
        }
    }

    public class Stack {

        private List<Node> tree;
        private int depth = 0;
        private List<Comm> comments;
        private Branch stack;

        public Stack(List<Comm> comments) {
            this.depth = 0;
            this.comments = comments;
            this.stack = new Branch();

            depthQuery(this.stack.getParent(), comments, depth++, 0);
            depthQuery(this.stack.getChild(), comments, depth++, 0);

            // Saving the reference for the root of tree
            this.tree = this.stack.getParent();
        };

        public List<Node> getTree() {
            return tree;
        }

        public void queryNext() {
            stack.parent = stack.child;
            stack.child = new ArrayList<>();

            depthQuery(stack.getChild(), comments, depth++, 0);
        };
    }

    public void queryTree(Node node, List<Comm> comments) {
        comments.add(node.comment);

        for(Node child : node.children) {
            queryTree(child, comments);
        }
    }

    public List<Comm> parseTree(Stack stack) {
        List<Comm> comments = new ArrayList<>();

        for(Node node : stack.getTree())
            queryTree(node, comments);

        return comments;
    }

    public List<Comm> queryTree(List<Comm> comments, int depth) {
        Stack stack = new Stack(comments);

        while(stack.stack.child.size() != 0 && (stack.depth < depth || depth == -1) ) {

            for(Node parentNode : stack.stack.parent) {
                for(Node childNode : stack.stack.child) {
                    if(parentNode.comment.getId().equals(childNode.comment.getParent()))
                        parentNode.children.add(childNode);
                }
            }

            stack.queryNext();
        }

        return parseTree(stack);
    }

    public List<Comm> queryTree(List<Comm> comments) {
        return queryTree(comments, -1);
    };
};