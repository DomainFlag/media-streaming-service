package com.example.cchiv.jiggles.player.protocol.builder;

public class Body {

    private byte[] input;
    private int size;

    public Body(int size) {
        this.size = size;
        this.input = new byte[size];
    }

    public void resolve(byte[] data, int offset, int length) {
        System.arraycopy(data, offset, input, 0, length);
    }

    public byte[] getInput() {
        return input;
    }

    public int getSize() {
        return size;
    }
}
