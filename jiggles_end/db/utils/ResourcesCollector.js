const fs = require("fs");
const CONSTANTS = require("./Constants");

class ResourcesCollector {
    constructor(resourcePath) {
        this.resourcePath = resourcePath;

        this.minLeaf = 2;
        this.maxLeaf = 6;
        this.treeCollector = [];

        this.init(resourcePath, this.treeCollector);
    }

    printCollection() {
        console.log(JSON.stringify(this.treeCollector));
    }

    static disruptBoundary(features, range) {
        if(range[0] >= range[1]) {
            features.splice(features.indexOf(range), 1);
        }
    }

    init(resourcePath, collection, depth = 0) {
        let records = fs.readdirSync(__dirname + `/../resources/${resourcePath}`);

        let folder = {
            name : resourcePath,
            files : [[this.minLeaf, this.maxLeaf]],
            folders  : { free : [[0, this.minLeaf]], reserved : []}
        };

        records.forEach((record) => {
            let index = Number(record.replace(/[^0-9]*/g, ""));

            let stats = fs.statSync(__dirname + `/../resources/${resourcePath}/${record}`);

            if(stats.isDirectory()) {
                folder.folders.free.forEach((range) => {
                    if(index > range[0] && index < range[1]) {
                        folder.folders.free.push([index+1, range[1]]);
                        range[1] = index;
                    } else if(index === range[0]) {
                        range[0]++;

                        ResourcesCollector.disruptBoundary(folder.folders.free, range);
                    } else if(index === range[1]) {
                        range[1]--;

                        ResourcesCollector.disruptBoundary(folder.folders.free, range);
                    }
                });

                this.init(`${resourcePath}/${record}`, folder.folders.reserved, depth+1);
            } else {
                folder.files.forEach((range) => {
                    if(index > range[0] && index < range[1]) {
                        folder.files.push([index+1, range[1]]);
                        range[1] = index;
                    } else if(index === range[0]) {
                        range[0]++;

                        ResourcesCollector.disruptBoundary(folder.files, range);
                    } else if(index === range[1]) {
                        range[1]--;

                        ResourcesCollector.disruptBoundary(folder.files, range);
                    }
                });
            }
        });

        collection.push(folder);
    };

    findPath(destination = this.treeCollector[0], parentReserved = true) {
        let reserved = (destination.folders.free.length === 0);

        if(destination.files.length === 0) {
            let it = 0;

            if(destination.folders.free.length !== 0 && parentReserved) {
                return {
                    type : CONSTANTS.FOLDER,
                    range : destination.folders.free,
                    name : destination.name
                }
            }

            while(it < destination.folders.reserved.length) {
                let folder = destination.folders.reserved[it];

                let extendedPath = this.findPath(folder, reserved);
                if(extendedPath)
                    return extendedPath;
                else if(parentReserved) {
                    return {
                        type : CONSTANTS.FOLDER,
                        name : destination.name
                    }
                }

                it++;
            }
        } else {
            return {
                type : CONSTANTS.FILE,
                name : destination.name,
                range : destination.files
            };
        }
    };

    reservePath() {
        let path = this.findPath();

        let range = path.range[0], index = range[0];

        range[0]++;
        if(range[0] === range[1])
            path.range.splice(0, 1);

        return 'resources/' + path.name + '/' + String(index).padStart(3, '0');
    };

    freeReservedPath(destination = this.treeCollector[0], paths, file, index = 0) {
        if(destination.name === paths[index]) {

            if(destination.files.length !== 0) {
                let leftOuterBound = {
                    value : this.minLeaf,
                    range : null,
                    index : null
                }, rightOuterBound = {
                    value : this.maxLeaf,
                    range : null,
                    index : null
                };

                destination.files.forEach((range, ind) => {
                    if(range[1] <= file) {
                        leftOuterBound.value = Math.max(leftOuterBound.value, range[0]);
                        leftOuterBound.range = range;
                        leftOuterBound.index = ind;
                    } else if(range[0] > file) {
                        rightOuterBound.value = Math.min(rightOuterBound.value, range[1]);
                        rightOuterBound.range = range;
                        rightOuterBound.index = ind;
                    }
                });

                /**
                 * Merging the cells together
                 */
                if(leftOuterBound.value + 1 === rightOuterBound.value) {
                    leftOuterBound.range[1] = rightOuterBound.range[1];
                    destination.files.splice(rightOuterBound.index, 1);
                } else if(leftOuterBound.range === null && rightOuterBound.range !== null && file + 1 === rightOuterBound.range[0]) {
                    rightOuterBound.range[0]--;
                } else if(rightOuterBound.range === null && leftOuterBound.range !== null && file === leftOuterBound.range[1]) {
                    leftOuterBound.range[1]++;
                } else {
                    destination.files.splice(rightOuterBound.index, 0, [file, file+1]);
                }
            } else {
                destination.files.push([file, file+1]);
            }
        } else {

        }
    }

    static purifyPath(path) {
        return path
            .replace(/^.*(forum\/threads)([^\.]*)(\.(jpg|jpeg|svg|png))/, '$1 $2')
            .split(' ');
    }

    freePath(path) {
        let purifiedPath = ResourcesCollector.purifyPath(path);
        let paths = [ purifiedPath[0] ].concat(purifiedPath[1].split('/'));

        this.freeReservedPath(this.treeCollector[0], paths.slice(0, paths.length-1), Number(paths[paths.length-1]), 0);
    }
}

module.exports = (() => new ResourcesCollector('forum/threads'))();