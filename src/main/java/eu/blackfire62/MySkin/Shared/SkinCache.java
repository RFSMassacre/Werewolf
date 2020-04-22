/*
 * Decompiled with CFR 0.139.
 */
package eu.blackfire62.MySkin.Shared;

import eu.blackfire62.MySkin.Shared.SkinProperty;
import java.util.UUID;

public interface SkinCache {
    public UUID loadSkinOfPlayer(UUID var1);

    public void resetSkinOfPlayer(UUID var1);

    public void saveSkinOfPlayer(UUID var1, UUID var2);

    public UUID loadUUID(String var1);

    public void saveUUID(String var1, UUID var2);

    public SkinProperty loadSkinProperty(UUID var1);

    public void saveSkinProperty(UUID var1, SkinProperty var2);

    public void clearCache();
}

