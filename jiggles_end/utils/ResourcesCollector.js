const fs = require("fs");
const CONSTANTS = require("./Constants");

class ResourcesCollector {
    constructor(resourcePath) {
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
        let records = fs.readdirSync(`./../resources/${resourcePath}`);

        let folder = {
            name : resourcePath,
            files : [[this.minLeaf, this.maxLeaf]],
            folders  : { free : [[0, this.minLeaf]], reserved : []}
        };

        records.forEach((record) => {
            let index = Number(record.replace(/[^0-9]*/g, ""));

            let stats = fs.statSync(`./../resources/${resourcePath}/${record}`);

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
                    range : destination.folders.free[0],
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
                range : destination.files[0]
            };
        }
    };
}

let resourcesCollector = new ResourcesCollector('forum/threads');
resourcesCollector.printCollection();
console.log(resourcesCollector.findPath());

// modules.exports = (() => new ResourcesCollector())();