package com.example.cchiv.jiggles.player.protocol;

public class Header {
    private String header;
    private String value;

    public Header(String header, String value) {
        this.header = header;
        this.value = value;
    }

    public void encode(StringBuilder stringBuilder, String value) {
        stringBuilder.append(header);
        stringBuilder.append(": ");
        stringBuilder.append(value);
        stringBuilder.append("\n");
    }

    public void encode(StringBuilder stringBuilder) {
        encode(stringBuilder, value);
    }

    public Header decode(String value) {
        return new Header(null, null);
    }

    public String getHeader() {
        return header;
    }

    public String getValue() {
        return this.value;
    }
}