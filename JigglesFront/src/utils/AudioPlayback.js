class Setup {
    constructor(audioCtx, callback) {
        this.audioCtx = audioCtx;
        this.equalizer = {};
        this.callback = callback;

        this.init();
    }

    init = () => {
        for(let g = 5; g < 15; g++)
            this.equalizer[Math.pow(2, g)] = 0;
    };

    onChangeValue = (key) => (value) => {
        if(this.equalizer[key] !== value) {
            this.equalizer[key] = value;

            this.callback();
        }
    };

    inputEqualizerNode = (node) => {
        let prov = node;

        Object.keys(this.equalizer).forEach((type) => {
            let nodeBiquadFilter = this.audioCtx.createBiquadFilter();

            nodeBiquadFilter.type = "peaking";
            nodeBiquadFilter.frequency.value = Number(type);
            nodeBiquadFilter.gain.value = this.equalizer[type];

            prov.connect(nodeBiquadFilter);

            prov = nodeBiquadFilter;
        });

        return prov;
    };
}

class AudioPlayback {
    constructor() {
        this.audioCtx = new (window.AudioContext || window.webkitAudioContext)();

        this.offsetTime = undefined;
        this.gainNode = this.audioCtx.createGain();

        this.setup = new Setup(this.audioCtx, this.jumpTrack);
        this.visualizer = new Visualizer(this.audioCtx);
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
        console.log("fsafsafa");
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
        let nodeNode = this.setup.inputEqualizerNode(audioNode);

        nodeNode.connect(this.audioCtx.destination);
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
        console.log(typeof offsetTime);
        if(offsetTime === undefined)
            offsetTime = this.getCurrentTime();
        else this.offsetTime = offsetTime;
        this.timeOffset = this.audioCtx.currentTime;

        this.source.disconnect();
        this.source = this.audioCtx.createBufferSource();
        this.source.buffer = this.audioBuffer;

        this.source.connect(this.gainNode);
        let audioNode = this.visualizer.inputNode(this.gainNode);
        let nodeNode = this.setup.inputEqualizerNode(audioNode);

        nodeNode.connect(this.audioCtx.destination);

        this.source.start(offsetTime);
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
        this.analyser = this.audioCtx.createAnalyser();

        this.gathering = false;
        this.data = [];

        this.analyser.fftSize = 64;
        this.bufferLength = this.analyser.frequencyBinCount;
    }

    initialize = (canvas, ctx) => {
        this.canvas = canvas;
        this.ctx = ctx;

        this.ctx.lineWidth = "2px";

        this.height = canvas.height;
        this.width = canvas.width;
    };

    inputNode = (node) => {
        node.connect(this.analyser);

        return this.analyser;
    };


    gatherData = (timeSeconds) => {
        this.gathering = true;

        return new Promise((resolve, reject) => {
            this.data.length = 0;

            setTimeout(() => {
                this.gathering = false;

                if(this.data.length === 0) {
                    reject("no data");
                } else resolve(this.data);

            }, timeSeconds);
        });
    };

    visualize = () => {
        let dataArray = new Float32Array(this.bufferLength);
        // this.analyser.getFloatTimeDomainData(dataArray);
        this.analyser.getFloatFrequencyData(dataArray);

        if(this.gathering) {
            let arr = Array.from(dataArray);
            arr.forEach((el) => this.data.push(el));
        }

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