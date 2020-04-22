/*
 * Decompiled with CFR 0.139.
 */
package eu.blackfire62.MySkin.Shared.Util;

public class MojangAPIException
extends Exception {
    private static final long serialVersionUID = 7588175957523148088L;
    private Reason reason;

    public MojangAPIException(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }

    public static enum Reason {
        UNKNOWN,
        NOT_PREMIUM,
        RATE_LIMITED,
        SERVER_UNAVAILABLE;
        
    }

}

