/*
 * Decompiled with CFR 0.139.
 */
package eu.blackfire62.MySkin.Shared;

public class SkinProperty {
    public String name;
    public String value;
    public String signature;

    public SkinProperty(String value, String signature) {
        this.name = "textures";
        this.value = value;
        this.signature = signature;
    }

    public SkinProperty(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }
}

