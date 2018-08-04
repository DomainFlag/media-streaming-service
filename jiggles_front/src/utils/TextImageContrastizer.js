function rgbaToRgb(r, g, b, a, r2, g2, b2) {
    a /= 255;

    let r3 = Math.round(((1 - a) * r2) + (a * r));
    let g3 = Math.round(((1 - a) * g2) + (a * g));
    let b3 = Math.round(((1 - a) * b2) + (a * b));

    return [r3, g3, b3];
}


function rgbToHsl(r, g, b) {
    r /= 255;
    g /= 255;
    b /= 255;

    let max = Math.max(r, g, b), min = Math.min(r, g, b);

    return (max + min) / 2;
}

/* Do on the SERVER only once, instead of doing on client */
const constrastizer = (image, content, precision = 10, canvas = null) => {
    let dimenCanvas = image.getBoundingClientRect();
    let dimenContent = content.getBoundingClientRect();

    let ratioX = dimenCanvas.width / image.naturalWidth;
    let ratioY = dimenCanvas.height / image.naturalHeight;

    let relativeXContent = dimenCanvas.width - dimenContent.width - (dimenCanvas.right - dimenContent.right);
    let relativeYContent = dimenCanvas.height - dimenContent.height - (dimenCanvas.bottom - dimenContent.bottom);

    let relativeWidth = dimenContent.width;
    let relativeHeight = dimenContent.height;

    if(canvas === null)
        canvas = document.createElement("canvas");
    let ctx = canvas.getContext("2d");
    canvas.width = relativeWidth;
    canvas.height = relativeHeight;

    ctx.drawImage(image, relativeXContent / ratioX,
        relativeYContent / ratioY,
        relativeWidth / ratioX,
        relativeHeight / ratioY,
        0, 0, relativeWidth, relativeHeight);

    let sum = 0;
    for(let g = 0; g < precision; g++) {
        let colorInput = ctx.getImageData(
            Math.floor(Math.random()*relativeWidth),
            Math.floor(Math.random()*relativeHeight),
            1, 1);

        sum += rgbToHsl(...rgbaToRgb(...colorInput.data, 255, 255, 255));
    }

    if(sum / precision < 0.5)
        return "#FFFFFF";
    else return "#000000";
};

export default constrastizer;