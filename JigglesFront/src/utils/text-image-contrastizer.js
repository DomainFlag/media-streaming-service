function rgbToHsl(r, g, b) {
    r /= 255, g /= 255, b /= 255;

    let max = Math.max(r, g, b), min = Math.min(r, g, b);
    let h, s, l = (max + min) / 2;

    if (max === min) {
        h = s = 0; // achromatic
    } else {
        let d = max - min;
        s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

        switch (max) {
            case r: h = (g - b) / d + (g < b ? 6 : 0); break;
            case g: h = (b - r) / d + 2; break;
            case b: h = (r - g) / d + 4; break;
        }

        h /= 6;
    }

    return l;
}

/* Do on the server only once, instead of doing on client */
/* Recognize where to put text? */
const constrastizer = (image, child, precision = 10) => {
    let posChild = image.getBoundingClientRect();
    let posParent = child.getBoundingClientRect();

    let posRelativeTop = posParent.top - posChild.top;
    let posRelativeLeft = posParent.left - posChild.left;

    let widthChild = posChild.width;
    let heightChild = posChild.height;

    let canvas = document.createElement("canvas");
    let ctx = canvas.getContext("2d");
    ctx.width = widthChild;
    ctx.height = heightChild;

    ctx.drawImage(image, posRelativeLeft, posRelativeTop, widthChild, heightChild, 0, 0, widthChild, heightChild);

    let data = ctx.getImageData(5, 5, 1, 1).data;

    let luminosity = rgbToHsl(data[0], data[1], data[2]);
    if(luminosity < 0.5)
        return "#FFFFFF";
    else return "#000000";
};

export default constrastizer;