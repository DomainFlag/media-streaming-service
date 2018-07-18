class AudioPlayback {
    constructor() {
        this.audioCtx = new (window.AudioContext || window.webkitAudioContext)();

        this.offsetTime = undefined;
        this.gainNode = this.audioCtx.createGain();
        this.visualizer = new Visualizer(this.audioCtx, this.gainNode);
    }

    initialize = (callback, destination) => {
        this.currentTime = Date.now();

        this.audioCtx.decodeAudioData(destination)
            .then((audioBuffer) => {
                console.log("Successfully created AudioBuffer");

                this.audioBuffer = audioBuffer;
                this.playAudio(this.audioCtx, audioBuffer);

                callback();
            })
            .catch((console.error));
    };

    getTotalTime = () => {
        return this.audioBuffer.duration;
    };

    setVolumeLevel = (value) => {
        this.gainNode.gain.setValueAtTime(value, this.audioCtx.currentTime);
    };

    playAudio = (audioCtx, audioBuffer) => {
        this.timeOffset = (Date.now() - this.currentTime) / 1000;

        // Get an AudioBufferSourceNode.
        // This is the AudioNode to use when we want to play an AudioBuffer
        this.source = audioCtx.createBufferSource();
        // set the buffer in the AudioBufferSourceNode
        this.source.buffer = audioBuffer;
        // connect the AudioBufferSourceNode to the
        // destination so we can hear the sound
        this.source.connect(this.gainNode);

        let audioNode = this.visualizer.inputNode(this.gainNode);
        // let nodeNode = this.visualizer.inputNoteNode(audioNode);

        audioNode.connect(this.audioCtx.destination);
        // start the source playing
        this.source.start();
    };

    getCurrentTime = () => {
        if(this.offsetTime === undefined)
            return Math.round(this.audioCtx.currentTime - this.timeOffset);
        else {
            return Math.round(this.audioCtx.currentTime - this.timeOffset + this.offsetTime);
        }
    };

    jumpTrack = (offsetTime) => {
        this.offsetTime = offsetTime;
        this.timeOffset = this.audioCtx.currentTime;

        this.source.disconnect();
        this.source = this.audioCtx.createBufferSource();
        this.source.buffer = this.audioBuffer;

        this.source.connect(this.gainNode);
        let audioNode = this.visualizer.inputNode(this.gainNode);
        audioNode.connect(this.audioCtx.destination);

        this.source.start(0, offsetTime);
    };

    resumeTrack = () => {
        this.audioCtx.resume();
    };

    pauseTrack = () => {
        this.audioCtx.suspend();
    };

    getAudioTick = () => {
        return () => {
            return this.getCurrentTime();
        };
    };
}

class Visualizer {
    constructor(audioCtx) {
        this.audioCtx = audioCtx;
        this.analyser = audioCtx.createAnalyser();

        this.analyser.fftSize = 512;
        this.bufferLength = this.analyser.frequencyBinCount;
    }

    inputNode = (node) => {
        node.connect(this.analyser);

        return this.analyser;
    };

    inputNoteNode = (node) => {
        this.biquadFilter = this.audioCtx.createBiquadFilter();
        this.biquadFilter.type = "lowshelf";
        this.biquadFilter.frequency.value = 125;
        this.biquadFilter.gain.value = 5;

        node.connect(this.biquadFilter);

        return this.biquadFilter;
    };

    initialize = (canvas, ctx) => {
        this.canvas = canvas;
        this.ctx = ctx;

        this.ctx.lineWidth = "2px";

        this.height = canvas.height;
        this.width = canvas.width;
    };

    visualize = () => {
        let dataArray = new Float32Array(this.bufferLength);
        this.analyser.getFloatTimeDomainData(dataArray);

        this.partition = this.width / dataArray.length;

        this.ctx.clearRect(0, 0, this.width, this.height);

        this.ctx.strokeStyle = "#ffffff";

        this.ctx.beginPath();
        this.ctx.moveTo(0, this.height / 2.0);
        for(let it = 0; it < dataArray.length; it++) {
            this.ctx.lineTo(it * this.partition, ((dataArray[it] + 1.0) / 2.0) * this.height);
        }
        this.ctx.stroke();

        requestAnimationFrame(this.visualize);
    }
}

export default AudioPlayback;