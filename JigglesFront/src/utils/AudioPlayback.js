class Setup {
    constructor(audioCtx) {
        this.audioCtx = audioCtx;
        this.equalizer = {};
        this.filters = {};
        this.init();
    }

    init = () => {
        for(let g = 5; g < 15; g++)
            this.equalizer[Math.pow(2, g)] = 0;
    };

    onChangeValues = (set) => {
        Object.keys(set).forEach((key) => {
           if(this.equalizer.hasOwnProperty(key)) {
               this.equalizer[key] = set[key];


               if(this.filters.hasOwnProperty(key)) {
                   this.filters[key].gain.value = set[key] * 2;
               }
           }
        });
    };

    onChangeValue = (key) => (value) => {
        if(this.equalizer[key] !== value) {
            this.equalizer[key] = value;

            if(this.filters.hasOwnProperty(key))
                this.filters[key].gain.value = Number(value) * 2;
        }
    };

    inputEqualizerNode = (node) => {
        let prov = node;

        Object.keys(this.filters).forEach((key) => {
            this.filters[key].disconnect();

            delete this.filters[key];
        });

        Object.keys(this.equalizer).forEach((type) => {
            let nodeBiquadFilter = this.audioCtx.createBiquadFilter();
            this.filters[type] = nodeBiquadFilter;

            nodeBiquadFilter.type = "peaking";
            // More punch
            nodeBiquadFilter.frequency.value = Number(type) * 2;
            nodeBiquadFilter.gain.value = this.equalizer[type];
            // Fidelity level, the higher the better distribution on frequency domain
            nodeBiquadFilter.Q.value = 12.5;

            prov.connect(nodeBiquadFilter);

            prov = nodeBiquadFilter;
        });

        return prov;
    };
}

class AudioPlayback {
    constructor() {
        this.audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        this.currentTime = Date.now();

        this.offsetTime = 0;
        this.gainNode = this.audioCtx.createGain();
        this.volume = 0;

        this.setup = new Setup(this.audioCtx);
        this.visualizer = new Visualizer(this.audioCtx);
    }

    initialize = (callback, destination, resolution) => {
        this.audioCtx.decodeAudioData(destination)
            .then((audioBuffer) => {
                console.log("Successfully created AudioBuffer");

                this.audioBuffer = audioBuffer;
                this.playAudio(this.audioCtx, audioBuffer);

                resolution(true);
                callback();
            })
            .catch((data) => {
                console.error(data);
                resolution(false);
            });
    };

    getTotalTime = () => {
        return this.audioBuffer.duration;
    };

    setVolumeLevel = (value) => {
        this.volume = value;
        this.gainNode.gain.setValueAtTime(this.volume, this.audioCtx.currentTime);
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
        let nodeNode = this.setup.inputEqualizerNode(audioNode);

        nodeNode.connect(this.audioCtx.destination);
        // start the source playing
        this.source.start();
    };

    getCurrentTime = () => {
        if(this.offsetTime === 0)
            return Math.round(this.audioCtx.currentTime - this.timeOffset);
        else {
            return Math.round(this.audioCtx.currentTime - this.offsetTime);
        }
    };

    jumpTrack = (offsetTime) => {
        this.offsetTime = this.audioCtx.currentTime - offsetTime;

        this.source.disconnect();
        this.source = this.audioCtx.createBufferSource();
        this.source.buffer = this.audioBuffer;

        this.gainNode.disconnect();
        this.gainNode = this.audioCtx.createGain();
        this.gainNode.gain.setValueAtTime(this.volume, this.audioCtx.currentTime);

        this.source.connect(this.gainNode);
        let audioNode = this.visualizer.inputNode(this.gainNode);
        let nodeNode = this.setup.inputEqualizerNode(audioNode);

        nodeNode.connect(this.audioCtx.destination);

        this.source.start(0, offsetTime);
    };

    resumeTrack = () => {
        return this.audioCtx.resume();
    };

    pauseTrack = () => {
        return this.audioCtx.suspend();
    };

    getAudioTick = () => this.getCurrentTime();
}

class Visualizer {
    constructor(audioCtx) {
        this.audioCtx = audioCtx;
        this.analyser = this.audioCtx.createAnalyser();

        this.gathering = false;
        this.data = [];

        this.analyser.fftSize = 512;
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
        this.analyser.disconnect();
        this.analyser = this.audioCtx.createAnalyser();
        this.analyser.fftSize = 512;
        this.bufferLength = this.analyser.frequencyBinCount;

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
        this.analyser.getFloatTimeDomainData(dataArray);

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