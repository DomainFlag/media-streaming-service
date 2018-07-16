let currentTime, timeOffset;

let audioPlayback = (destination) => {
    let audioCtx = new (window.AudioContext || window.webkitAudioContext)();

    currentTime = Date.now();

    audioCtx.decodeAudioData(destination)
        .then((audioBuffer) => {
            console.log("Successfully created AudioBuffer");
            playAudio(audioCtx, audioBuffer);
        })
        .catch((console.error));
};


let playAudio = (audioCtx, audioBuffer) => {
    timeOffset = (Date.now() - currentTime) / 1000;

    // Get an AudioBufferSourceNode.
    // This is the AudioNode to use when we want to play an AudioBuffer
    let source = audioCtx.createBufferSource();
    // set the buffer in the AudioBufferSourceNode
    source.buffer = audioBuffer;
    // connect the AudioBufferSourceNode to the
    // destination so we can hear the sound
    source.connect(audioCtx.destination);
    // start the source playing
    source.start();


    // setInterval(() => {
    //     console.log(Math.round(audioCtx.currentTime - timeOffset));
    // }, 1000);

    setTimeout(() => {
        source.disconnect();
        source = audioCtx.createBufferSource();
        source.buffer = audioBuffer;
        source.connect(audioCtx.destination);
        source.start(0, 40);
    }, 4 * 1000);
};

export default audioPlayback;