class AudioPlayback {
    constructor() {
        this.audioCtx = new (window.AudioContext || window.webkitAudioContext)();

        this.offsetTime = undefined;
        this.gainNode = this.audioCtx.createGain();
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
        this.gainNode.connect(audioCtx.destination);
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
        this.gainNode.connect(this.audioCtx.destination);
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
    }
}

export default AudioPlayback;