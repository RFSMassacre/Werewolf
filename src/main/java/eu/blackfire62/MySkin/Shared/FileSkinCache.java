/*
 * Decompiled with CFR 0.139.
 */
package eu.blackfire62.MySkin.Shared;

import eu.blackfire62.MySkin.Shared.SkinCache;
import eu.blackfire62.MySkin.Shared.SkinProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.UUID;

public class FileSkinCache
implements SkinCache {
    private String skinpath;
    private String uuidpath;
    private String playerpath;
    private String newline;

    public FileSkinCache(File directory) {
        this.skinpath = directory.getAbsolutePath() + File.separatorChar + "skin" + File.separatorChar + "properties" + File.separatorChar;
        this.uuidpath = directory.getAbsolutePath() + File.separatorChar + "skin" + File.separatorChar + "uuids" + File.separatorChar;
        this.playerpath = directory.getAbsolutePath() + File.separatorChar + "skin" + File.separatorChar + "players" + File.separatorChar;
        this.newline = System.getProperty("line.separator");
        new File(this.skinpath).mkdirs();
        new File(this.uuidpath).mkdirs();
        new File(this.playerpath).mkdirs();
    }

    @Override
    public UUID loadSkinOfPlayer(UUID player) {
        File file = new File(this.playerpath + player.toString());
        if (!file.exists()) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            UUID skin = UUID.fromString(reader.readLine());
            reader.close();
            return skin;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveSkinOfPlayer(UUID player, UUID skin) {
        File file = new File(this.playerpath + player.toString());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(skin.toString());
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public UUID loadUUID(String skinname) {
        File file = new File(this.uuidpath + skinname);
        if (!file.exists()) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            UUID uuid = UUID.fromString(reader.readLine());
            reader.close();
            return uuid;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveUUID(String skinname, UUID uuid) {
        File file = new File(this.uuidpath + skinname);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(uuid.toString());
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkinProperty loadSkinProperty(UUID skin) {
        File file = new File(this.skinpath + skin.toString());
        if (!file.exists()) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            SkinProperty prop = new SkinProperty(reader.readLine(), reader.readLine());
            reader.close();
            return prop;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveSkinProperty(UUID skin, SkinProperty property) {
        File file = new File(this.skinpath + skin.toString());
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(property.value + this.newline + property.signature);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetSkinOfPlayer(UUID player) {
        File file = new File(this.playerpath + player.toString());
        if (file.exists()) {
            try {
                file.delete();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clearCache() {
        File dir = new File(this.skinpath);
        for (File f : dir.listFiles()) {
            f.delete();
        }
        dir = new File(this.uuidpath);
        for (File f : dir.listFiles()) {
            f.delete();
        }
    }
}

