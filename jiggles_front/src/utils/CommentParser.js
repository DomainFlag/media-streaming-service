let depthQuery = (stack, comments, depth, count = 0) => {
    let len = comments.length;

    for(let it = 0; it < len; it++) {
        if(comments[it-count].depth === depth) {
            stack.push({
                ...comments[it-count],
                children : []
            });

            comments.splice(it-count, 1);
            count++;
        }
    }
};

class Stack {
    constructor(comments) {
        this.depth = 0;
        this.comments = comments;

        this.stack = {
            "parent" : [],
            "child" : []
        };

        depthQuery(this.stack.parent, this.comments, this.depth++);
        depthQuery(this.stack.child, this.comments, this.depth++);

        this.tree = this.stack.parent;
    }

    getTree() {
        return this.tree;
    }

    queryNext() {
        this.stack.parent = this.stack.child;
        this.stack.child = [];

        depthQuery(this.stack.child, this.comments, this.depth++);
    };
}

const queryTree = (comments, depth = -1) => {
    let stack = new Stack(comments);

    while(stack.stack.child.length !== 0 && (stack.depth < depth || depth === -1) ) {

        stack.stack.parent.forEach((parentNode) => {
            stack.stack.child.forEach((childNode) => {

                if(parentNode._id === childNode.parent) {
                    parentNode.children.push(childNode);
                }
            });
        });

        stack.queryNext();
    }

    return stack.getTree();
};

export default queryTree;