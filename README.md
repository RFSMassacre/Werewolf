# Werewolves Is Now Free And Open Source!
I have decided to drop Spigot support due to the hard fork, so here we are!
![Banner](https://cdn.modrinth.com/data/cached_images/7ae67d1710fd895ea4dba40b21739fa017857fd0.png)


# Installation
## Requirements
Ensure you have Java 21 or higher to run this.

## Steps
1. Download the resource.
2. Download [SkinsRestorer](https://modrinth.com/plugin/skinsrestorer).
3. (Optional) Download [VampireRevamp](https://www.spigotmc.org/resources/vampirerevamp.75479/). ;)
4. Drop in all the jars in your plugins folder and start your server up.
5. For Waterfall/Velocity users, you cannot place SkinsRestorer on BungeeCord. 
It must be placed in the Spigot plugin folder. 
Once done, you create a blank file and name it "disableProxyMode.txt" and make sure it's a text file in the SkinsRestorer folder. 
If you have multiple servers in which you need SkinsRestorer, place them in servers individually and link them through a database.
6. Modify the configuration files to your liking, then either reboot your server or run the `/wwa reload` command.

# Configuration

<details>
    <summary>config.yml</summary>

```YAML
################################################################
###            Werewolf (Recoded) by RFSMassacre             ###
###                                                          ###
###   Discord Support: https://discord.gg/GzrSfKf            ###
###                                                          ###
### Feel free to contact me for support, but be courteous    ###
### and don't freak out and scream vaguely like I'm some     ###
### Trump supporter. :[                                      ###
###                                                          ###
################################################################

#Whether to use the permission group feature.
#This feature makes a new permission group for werewolves so owners
#can add custom perms for werewolves.
group-permissions:
    enabled: false
    group: "werewolf"

#Whether alpha stats should take in effect
alphas: true

#Werewolf Ability Cooldowns
cooldowns:
    #In minutes
    transform: 20
    howl: 3
    growl: 3
    
#Wolf Sound
sound:
    growl: "ENTITY_WOLF_GROWL"
    howl: "ENTITY_WOLF_HOWL"
    pant: "ENTITY_WOLF_PANT"
    volume: 10.0

#Werewolf Level Perks
maturity:
    intent: 2
    no-drop: 10
    scent-track: 20
    free-transform: 30
    gold-immunity: 35
    
#Transformation Time Period
transformation:
    #Whether to limit transformation time or not
    limit: true
    #Whether levels should increase time by LINEAR, EXPONENTIAL, or FLAT.
    #Not choosing the right name will default to FLAT, meaning it will not increase time limit per level.
    equation: LINEAR
    #Amount of time given at the start.
    base: 0
    #How fast seconds are earned, does not affect FLAT equation.
    modifier: 15
    
#Damage Stats
#Defense numbers lower than 1.0 increases defense.
#Alpha werewolves are intended to have more defense and damage than normal werewolves.
werewolf-stats:
    witherfang:
        speed: 0.6
        werewolf:
            defense: 0.3
            fist-damage: 6.0
            item-damage: 1.0
        alpha:
            defense: 0.15
            fist-damage: 12.0
            item-damage: 2.0
    silvermane:
        speed: 0.4
        werewolf:
            defense: 0.4
            fist-damage: 5.0
            item-damage: 1.0
        alpha:
            defense: 0.2
            fist-damage: 10.0
            item-damage: 2.0
    bloodmoon:
        speed: 0.3
        werewolf:
            defense: 0.333
            fist-damage: 5.0
            item-damage: 1.0
        alpha:
            defense: 0.166
            fist-damage: 10.0
            item-damage: 2.0

#Potion effects per Clan
#It MUST be in <name>:<power>:<duration> format otherwise it will not read.
werewolf-effects:
    witherfang:
        - "HUNGER:0:72000"
        - "NIGHT_VISION:0:72000"
        - "JUMP:3:72000"
        - "SPEED:3:72000"
    silvermane:
        - "HUNGER:2:72000"
        - "NIGHT_VISION:0:72000"
        - "JUMP:2:72000"
        - "SPEED:1:72000"
        - "REGENERATION:2:72000"
    bloodmoon:
        - "HUNGER:1:72000"
        - "NIGHT_VISION:0:72000"
        - "JUMP:1:72000"
        - "SPEED:0:72000"
        - "INCREASE_DAMAGE:2:72000"
    
#Silver Penalty
#Damage per second when Werewolf holds a silver sword
silver-penalty: 3    
    
#Werewolf Infection Rate
#By chance out of 100
infection:
    wolf:
        chance: 5
    werewolf:
        chance: 1
        intent: 5
 
#Auto-Cure Setting
auto-cure:
    enabled: false
    days: 31
    alpha-only: true

#Scent Tracking
track:
    chance: 10
    range: 8
    y-offset: 0
    particle: "SPELL_MOB"
    particle-amount: 20
    #Distances must be in descending values.
    distances:
        far: 500
        close: 200
        very-close: 50
    safe-zones: false
    
#Recipes
recipes:
    cure-potion: true
    infection-potion: true
    wolfsbane-potion: true
    silver-sword: true
    vampire-tracker: true
    werewolf-tracker: true
    washed-helmet: true
    washed-chestplate: true
    washed-leggings: true
    washed-boots: true
    purified-helmet: true
    purified-chestplate: true
    purified-leggings: true
    purified-boots: true
    salt: true
    
#What ingedient defines the tracker's recipe
#This is so 1.8 and below can craft these items optionally    
tracker-ingredient:
    vampire: GHAST_TEAR
    werewolf: RABBIT_FOOT
    
#Werewolf Clan Skins
enable-skins: true

#You can use URL or names below.
#Remember to specify whether you want CLASSIC or SLIM in each option.
#Changing skin options will take effect on startup.
skins:
    Alpha: "WerewolfAlpha"
    Witherfang: "WF_Werewolf"
    Silvermane: "SM_Werewolf"
    Bloodmoon: "BM_Werewolf"

use-urls: false
skin-urls:
    Alpha:
        url: "https://i.imgur.com/T5N6m75.png"
        skin-type: "SLIM"
    Witherfang:
        url: "https://i.imgur.com/kUF6qIH.png"
        skin-type: "SLIM"
    Silvermane:
        url: "https://i.imgur.com/8xQWrjG.png"
        skin-type: "SLIM"
    Bloodmoon:
        url: "https://i.imgur.com/M0qnxXh.png"
        skin-type: "SLIM"
 
#Check Intervals
#Lower numbers may cause lag on low-end servers, but will respond in realtime on high-end servers.
#If you lower silver check below 20, they will lose more than 1 HP per second.
#If you lower the alpha check it might cause some lag if you have 100+ werewolves on the server.
intervals:
    moon-cycle: 10
    cure-check: 20
    werewolf-buffs: 20
    werewolf-skins: 10
    werewolf-drops: 10
    werewolf-silver: 20
    werewolf-scent: 100
    alpha-update: 1200
    trackers: 20
    item-update: 40
    hunting-armor-checker: 20
    hybrid-check: 20
 
#List of worlds were Werewolf powers will not work whatsoever.
no-werewolf-worlds:
    - world_nether
    - world_the_end
 
#List of worlds where the moon cycle will not happen
#Moon cycles will load moons to every world with a NORMAL enviornment.
#If the name of that world is here, it will skip this.
blocked-worlds:
    - world_nether
    - world_the_end
    
#List of commands that are blocked while in werewolf form.
blocked-commands:
    alpha: #When being alpha.
        - /pvp
    werewolf: #When in wolf form.
        - /skin
        - /myskin
    all: #When being a werewolf in general.
        - /v o
        - /v offer
        - /vampire o
        - /vampire offer
        - /v a
        - /v accept
        - /vampire a
        - /vampire accept
        - /v flask
        - /v f
        - /vampire flask
        - /vampire f
        
#List of potion effect that are cleared in werewolf form.
blocked-potions:
    - ABSORPTION
    - DAMAGE_RESISTANCE
    - FIRE_RESISTANCE
    - HEALTH_BOOST
    - INVISIBILITY
    - LUCK
    - WATER_BREATHING
    
#Display names of each moon phase
moon-phases:
    FULL_MOON: "Full Moon"
    WANING_GIBBOUS: "Waning Gibbous"
    LAST_QUARTER: "Last Quarter"
    WANING_CRESCENT: "Waning Crescent"
    NEW_MOON: "New Moon"
    WAXING_CRESCENT: "Waxing Crescent"
    FIRST_QUARTER: "First Quarter"
    WAXING_GIBBOUS: "Waxing Gibbous"
    
#What is replaces the placeholders in the menus
menu:
    not-applied: "&7N/A"
    race:
        werewolf: "&6Lvl {level} {clan} Werewolf"
        vampire: "&cVampire"
        human: "&fHuman"
    days:
        now: "&3&lNow"
        tonight: "&bTonight"
        tomorrow: "&bTomorrow Night"
        later: "{time} Days"
    clan:
        WITHERFANG: "&2&lWitherfang"
        SILVERMANE: "&d&lSilvermane"
        BLOODMOON: "&4&lBloodmoon"
        
    
#List of items werewolves are not allowed to eat at all times
#The idea is to only give them meat
diet:
    enabled: true
    prevent-consumption: false
    blocked-foods:
        - APPLE
        - BREAD
        - MUSHROOM_SOUP
        - GOLDEN_APPLE
        - CARROT_ITEM
        - POTATO_ITEM
        - BAKED_POTATO
        - POISONOUS_POTATO
        - PUMPKIN_PIE
        - COOKIE
        - CAKE
        - MELON
        - BEETROOT
        - BEETROOT_SOUP
        - CHORUS_FRUIT
        - ROTTEN_FLESH

#Options pertaining to the hunting of other races
hunting:
    enabled: true
    trackers:
        use-either-hand: true
    purification:
        #Chance of failure out of 100
        chance: 75
    force-pvp:
        #Force PVP in claims you do not have permission to.
        alphas: true
        claims: true
        admin-claims: false
    target:
        vampire:
            daytime: false
            nighttime: true
        werewolf:
            wolf-form: true
            human-form: true
            daytime: false
            nighttime: true
        safe-zones: false

purity:
    washed:
        diamond-helmet: 5
        diamond-chestplate: 20
        diamond-leggings: 15
        diamond-boots: 5 
    purified:
        diamond-helmet: 10
        diamond-chestplate: 45
        diamond-leggings: 35
        diamond-boots: 10
        
bonus:
    washed:
        diamond-helmet: 4
        diamond-chestplate: 10
        diamond-leggings: 7
        diamond-boots: 4
    purified:
        diamond-helmet: 8
        diamond-chestplate: 20
        diamond-leggings: 14
        diamond-boots: 8
        
#Plugin support
support:
    PvPManager: false
    WorldGuard: false
```

</details>

<details>
    <summary>locale.yml</summary>

```YAML
################################################################
###              Werewolf (Recoded) Locale                   ###
################################################################

prefix:
    werewolf: "&6&lWerewolf &7&l> &e"
    hunter: "&3&lHunting &7&l> &b"

infection:
    werewolf-potion: "&cThe taste of this drink reeks! But you feel... Nimble."
    wolf-bite: "&cThe wolf left a mark on your arm. But... It's healing?"
    werewolf-bite: "&cSharp teeth has bitten your skin, yet... You feel more powerful."
    
cure:
    cure-potion: "&eTastes like lemon. The infection has been cured."
    wolfsbane-potion: "&cAgainst your will, a &4WolfsBane Potion &chas negated your Werewolf powers..."
    auto-cure: "&f{werewolf}&d's Werewolf infection subsided..."
    
clan:
    no-clan: "&cYou are not part of a Werewolf clan."
    invalid-arg: "&cYou must supply a page number."
    killed-alpha: "&f{killer} &ehas killed &f{alpha} &eand became the new &3{clan} Alpha&e!"
    no-alpha: "&f{alpha} &6has been cured and is no longer the {clan} Alpha."
    hunted-alpha: "&f{killer} &6hunted down the {clan} Alpha. &f{alpha} &6is no longer worthy!"
    new-alpha: "&f{alpha} &ehas become the new &3{clan} Alpha&e!"
    alpha-pvp: "&cAlpha werewolves cannot cower that way!"
    
full-moon:
    tonight: "&3Tonight is the &bFull Moon&3..."
    tonight-count: "&6{werewolves} Werewolves will roam tonight..."
    transformed: "&eThe &bFull Moon &eempowers you! You have transformed!"
    morning: "&cThe &bFull Moon &chas set, along with your energy..."

transform:
    to-form: "&eYou show your true form!"
    from-form: "&aYou return back to human form."
    not-leveled: "&cYou can't control your form yet... You must be Lvl {level}."
    not-infected: "&cYou must be a Werewolf to transform."
    cant-transform: "&cYour powers are not working right now!"
    on-cooldown: "&cYou still feel weak... You can transform again in {minutes} minutes."
    full-moon: "&cYou cannot control your powers during a &bFull Moon&c!"
    
intent:
    to-intent: "&eYour intention to infect has been &aenabled&e."
    from-intent: "&eYour intention to infect has been &cdisabled&e."
    not-leveled: "&cYou can't control your intent yet. You must be Lvl {level}."
    not-infected: "&cYou must be a Werewolf to intend to infect."

track:
    scent-try: "&7*sniff*"
    scent-found: "&e*sniff* *sniff* You picked up &f{player}&e's scent..."
    to-track: "&a*sniff* &f{player}&a can be sensed in this direction..."
    from-track: "&eYou open your eyes and lose their trail."
    not-in-same-world: "&cYour target must be in the same world."
    not-found: "&cYour target cannot be found."
    no-target: "&cYou currently have no target."
    not-leveled: "&cYou don't know how to track scent yet. You must be Lvl {level}."
    not-infected: "&cYou must be a Werewolf to track the scent of players."
    not-in-form: "&cYou must be in wolf form to follow someone's scent."
    
howl:
    success: "*howl*"
    on-cooldown: "&cYou can howl again in {minutes} minutes."
    not-leveled: "&cYour still to weak to howl. You must be Lvl {level}."
    not-infected: "&cYou must be a Werewolf to howl."
    not-in-form: "&cYou must be in wolf form to howl."
    
growl:
    success: "*growl*"
    on-cooldown: "&cYou can growl again in {minutes} minutes."
    not-leveled: "&cYour still to weak to growl. You must be Lvl {level}."
    not-infected: "&cYou must be a Werewolf to growl."
    not-in-form: "&cYou must be in wolf form to growl."
    
invalid:
    no-permission: "&cYou do not have permission for this command."
    main-args: "&fUnknown command. Use &6/ww help &ffor a list of commands."
    admin-args: "&fUnknown command. Use &c/wwa help &ffor a list of commands."
    no-vampire: "&3Vampire &cplugin not found. Vampire infections will not be monitored."
    blocked-command: "&cWerewolves cannot use this command ever!"
    blocked-command-world: "&cWerewolves cannot use this command in wolf form!"
    blocked-command-alpha: "&cAlpha werewolves cannot use this command!"
    outdated-server: "&cMinecraft 1.7.10 and below is not fully supported! Please consider updating to at least Minecraft 1.8.8 or higher to fully use all features."
    no-skins: "&3SkinsRestorer &cplugin not found. Skins will not be applied until it is installed."
    no-regions: "&3WorldGuard &cplugin not found. Region checks will not be enabled."
    no-vault: "&3Vault &cplugin not found. Group permissions feature will not be enabled."
    no-groups: "&cYour permission plugin does not support groups. Group permissions feature is disabled."
    
hunting:
    disabled: "Hunting vampires and werewolves is disabled on this server."
    racial: 
        use: "&cOnly &bHumans &care able to use the {item}&c!"
        craft: "&cOnly &bHumans &care able to craft the {item}&c!"
        smelt: "&cOnly &bHumans &ccan purify {item}&c!"
    armor:
        cleansed: "&eYour armor has kept you cleansed."
        wrong-item: "&cYou can only purify armor that has been washed with &fQuartz &cand &eBlaze Powder&c."
        burned: "&cOh no! The purification failed and burned the armor to ashes."
    tracker: 
        actionbar: "&f{target} &3&l| &e{distance} Blocks &baway"
        found: "&bYour &f{item} &bis pointing toward &f{target}&b and is &e{distance} Blocks &baway."
        already-found: "&bYou are tracking &f{target}&b."
        failed: "&cNo targets found within this world."
        lost: "&cYour &f{item} &chas lost sight of {target}&c."
        clear-target: "&cYour &f{item} &chas been cleared."
        no-target: "&cYour &f{item} &ccurrently has no target."
    claims:
        trespassing: "&cYou're trespassing in &f{owner}&c's land. Your PVP has been forced on!"

admin:
    level:
        no-args: "&cYou must specify the Werewolf and what to level them to."
        success: "&eYou have set {player}&e to Lvl {level}."
        not-infected: "&f{player}&c is not a Werewolf!"
    infect:
        no-args: "&cYou must specify the who to infect and the clan type."
        success: "&eYou have infected &f{player}&e with the &3{clan} &eInfection."
        not-human: "&f{player}&c must be a human to be infected!"
        failed: "&cThe infection failed for &f{player}&c!"
    cure:
        no-args: "&cYou must specify an online Werewolf!"
        success: "&eYou have cured &f{player}&e from the Werewolf Infection."
        failed: "&cCuring &f{player}&c failed!"
        not-infected: "&f{player}&c is not a Werewolf!"
    transform:
        success: "&aYou have toggled the form of &f{werewolf}&a."
        not-infected: "&f{player}&c is not a Werewolf!"
        no-args: "&cYou must specify an online Werewolf."
        full-moon: "&cYou cannot transform Werewolves during a &bFull Moon&c."
    spawn:
        no-args: "&cYou must specify the item to spawn."
        console: "&cOnly players can run this command."
        success: "&aYou have spawned a(n) {item}&a."
    setalpha:
        success: "&aYou have set &f{werewolf}&a as the &3{clan} Alpha&a!"
        not-infected: "&f{player}&c is not a Werewolf!"
        no-args: "&cYou must specify an online Werewolf."
    setphase:
        no-args: "&cYou must specify the moon phase."
        console: "&cOnly players can run this command."
        success: "&aYou have set this world's moon phase to {phase}."
        blocked-world: "&cThis world has been blacklisted."
        no-moon: "&cThis world has no moon!"
    import:
        no-files: "&cNo files found to import. Make sure you have &bwerewolves.yml &cand &bclans.yml &cin your &bplugins/Werewolf/import &cfolder."
        complete: "&aLegacy Werewolf data has been imported. ;) Now you're free from buggy plugins."
    reload: "&aWerewolf has been reloaded."
    purge: "&7All broken player files have been cleared."
    skins-loaded: "&7Skins have been downloaded."

processing: "&7Your request is processing..."
```

</details>

<details>
    <summary>items.yml</summary>

```YAML
################################################################
###               Werewolf (Recoded) Items                   ###
################################################################

INFECTION_POTION:
  name: "&6Werewolf Infection Potion"
  lore:
    - ""
    - "  &7This bottle radiates with the  "
    - "  &7scent of fleas...  "
    - ""
    - "  &cDrinking this will make you  "
    - "  &cthe beast... You will become  "
    - "  &3Witherfang&c...  "
    - ""
    
CURE_POTION:
  name: "&dWerewolf Cure Potion"
  lore:
    - ""
    - "  &7This bottle glows a white  "
    - "  &7light...  "
    - ""
    - "  &aDrinking this will make you  "
    - "  &ahuman once more...  "
    - ""
    
WOLFSBANE_POTION:
  name: "&4WolfsBane Potion"
  lore:
    - ""
    - "  &7This bottle trembles with  "
    - "  &7energy...  "
    - ""
    - "  &eThrowing this on a Werewolf  "
    - "  &ein Wolf Form will force them  "
    - "  &einto their human form."
    - ""
    
ASH:
  name: "&fAsh"
  lore:
    - ""
    - "  &7The failed remains of armor.  "
    - ""
    
SILVER_SWORD:
  name: "&cSilver Sword"
  lore:
    - ""
    - "  &7The purified metal from your  "
    - "  &7iron sword has become silver.  "
    - "  &7This blade pierces through the  "
    - "  &7defense of any Werewolf.  "
    - ""
    
VAMPIRE_TRACKER:
  name: "&4Vampire Tracker"
  lore:
    - ""
    - "  &fRight click &7on the tracker  "
    - "  &7to keep track of the nearest  "
    - "  &cVampire &7at night.  "
    - ""
    - "  &fLeft click &7to clear your  "
    - "  &7target.  "
    - ""
    
WEREWOLF_TRACKER:
  name: "&6Werewolf Tracker"
  lore:
    - ""
    - "  &fRight click &7on the tracker  "
    - "  &7to keep track of the nearest  "
    - "  &7&6Werewolf&7 in wolf form.  "
    - ""
    - "  &fLeft click &7to clear your  "
    - "  &7target.  "
    - ""
    
SALT:
  name: "&fSalt"
  lore:
    - ""
    - "  &fRight click &7on ground to  "
    - "  &7place it. It disintegrates  "
    - "  &7after 30 Minutes.  "
    - ""
    - "  &cVampires &7cannot cross  "
    - "  &7over &fSalt&7.  "
    - ""
    
WASHED_HELMET:
  name: "&6Washed Diamond Helmet"
  lore:
    - ""
    - "  &7Cleansed with salt and  "
    - "  &7fire, the impurities of  "
    - "  &7this helmet are washed.  "
    - ""
    - "  &ePurity &6&l> &a+?%  "
    - "  &bDefense &6&l> &a+!% "
    - ""
    
WASHED_CHESTPLATE:
  name: "&6Washed Diamond Chestplate"
  lore:
    - ""
    - "  &7Cleansed with salt and  "
    - "  &7fire, the impurities of  "
    - "  &7this chestplate are  "
    - "  &7washed.  "
    - ""
    - "  &ePurity &6&l> &a+?%  "
    - "  &bDefense &6&l> &a+!% "
    - ""
    
WASHED_LEGGINGS:
  name: "&6Washed Diamond Leggings"
  lore:
    - ""
    - "  &7Cleansed with salt and  "
    - "  &7fire, the impurities of  "
    - "  &7these leggings are washed.  "
    - ""
    - "  &ePurity &6&l> &a+?%  "
    - "  &bDefense &6&l> &a+!% "
    - ""
    
WASHED_BOOTS:
  name: "&6Washed Diamond Boots"
  lore:
    - ""
    - "  &7Cleansed with salt and  "
    - "  &7fire, the impurities of  "
    - "  &7these boots are washed.  "
    - ""
    - "  &ePurity &6&l> &a+?%  "
    - "  &bDefense &6&l> &a+!% "
    - ""
    
PURIFIED_HELMET:
  name: "&ePurified Diamond Helmet"
  lore:
    - ""
    - "  &7The final step: purge.  "
    - "  &7The last of the former  "
    - "  &7helmet are now purified.  "
    - ""
    - "  &ePurity &6&l> &a+?%  "
    - "  &bDefense &6&l> &a+!% "
    - ""
    
PURIFIED_CHESTPLATE:
  name: "&ePurified Diamond Chestplate"
  lore:
    - ""
    - "  &7The final step: purge.  "
    - "  &7The last of the former  "
    - "  &7chestplate are now purified.  "
    - ""
    - "  &ePurity &6&l> &a+?%  "
    - "  &bDefense &6&l> &a+!% "
    - ""
    
PURIFIED_LEGGINGS:
  name: "&ePurified Diamond Leggings"
  lore:
    - ""
    - "  &7The final step: purge.  "
    - "  &7The last of the former  "
    - "  &7leggings are now purified.  "
    - ""
    - "  &ePurity &6&l> &a+?%  "
    - "  &bDefense &6&l> &a+!% "
    - ""
  
PURIFIED_BOOTS:
  name: "&ePurified Diamond Boots"
  lore:
    - ""
    - "  &7The final step: purge.  "
    - "  &7The last of the former  "
    - "  &7boots are now purified.  "
    - ""
    - "  &ePurity &6&l> &a+?%  "
    - "  &bDefense &6&l> &a+!% "
    - ""
```

</details>

# Permissions
| Description                        | Permission               |
|------------------------------------|--------------------------|
| To become infected.                | werewolf.becomeinfected  |
| To infect others.                  | werewolf.infectothers    |
| To drink cure potion.              | werewolf.drinkcurepotion |
| To be immune to WolfsBane Potions. | werewolf.immunewolfsbane |

# Commands
## Player Commands
| Command           | Description                   | Permission         |
|-------------------|-------------------------------|--------------------|
| /ww               | Shows the Werewolf menu.      | werewolf.werewolf  |
| /ww help [number] | Shows all commands.           | werewolf.help      |
| /ww clan          | Shows clan information.       | werewolf.clan      |
| /ww list          | Shows list of clan members.   | werewolf.list      |
| /ww transform     | Toggle your wolf form.        | werewolf.transform |
| /ww intent        | Toggle your intent to infect. | werewolf.intent    |
| /ww track         | Toggle your tracking senses.  | werewolf.track     |
| /ww growl         | Growl like a werewolf.        | werewolf.growl     |
| /ww howl          | Howl like a werewolf.         | werewolf.howl      |

## Admin Commands
| Command                        | Description                                                           | Permission              |
|--------------------------------|-----------------------------------------------------------------------|-------------------------|
| /wwa                           | Shows the clan statistics.                                            | werewolfadmin.werewolf  |
| /wwa help [number]             | Shows admin commands.                                                 | werewolfadmin.help      |
| /wwa spawn <item>              | Spawns werewolf item.                                                 | werewolfadmin.spawn     |
| /wwa infect <player> <clan>    | Infects player with specific clan.                                    | werewolfadmin.infect    |
| /wwa transform <player>        | Force toggle a player's transformation.                               | werewolfadmin.transform |
| /wwa setlevel <player> <level> | Sets a player's werewolf level.                                       | werewolfadmin.setlevel  |
| /wwa addlevel <player> <level> | Adds onto a player's werewolf level.                                  | werewolfadmin.addlevel  |
| /wwa setalpha <player>         | Promotes player to alpha werewolf.                                    | werewolfadmin.setalpha  |
| /wwa cure <player>             | Cures player of the infection.                                        | werewolfadmin.cure      |
| /wwa import                    | Imports data from original Werewolf. (Use with caution, still buggy.) | werewolfadmin.import    |

# Werewolf Clans
| Clan       | Infection Method | Potion Effects |
|------------|------------------|----------------|
| Witherfang | Infection Potion | Haste + Speed  |
| Silvermane | Wolf Bite        | Regeneration   |
| Bloodmoon  | Werewolf Bite    | Strength       |

# Werewolf Alphas
Every Werewolf Clan has an Alpha. The highest level Werewolf of that Clan becomes the Alpha.
Once an Alpha present, the only way to become the next Alpha of the Clan is to kill them while being of that Clan.
Alphas deal double damage and have double defense from a normal Werewolf.

# Moon Cycles
When you look up at the sky during the night, the moon is a heavy impact on every werewolf.
Each cycle is tracked with the /ww command, and at the beginning of every (8 days) week, a full moon occurs.

During a full moon, no werewolf will be able to control themselves.
As soon as moonlight is cast on them, they show their true forms and level up, growing a little stronger each full moon cycle and raising their level.

Werewolves drop all equipment on transformation, so remember to be mindful of where you are around the full moon.
Werewolves also can only eat meat, and get hurt by gold and silver weapons. 
If a werewolf holds a silver sword, they will be hurt greatly.

# Maturity
Each level gained, makes the werewolf stronger. 
Depending on the level set in the **config.yml** file, the werewolf will learn a new ability.

- **Intent To Infect**
> When activated with /ww intent, your chances of infecting another player with the werewolf infection is greatly increased when attacking them.

- **No Armor Drop**
> During transformations, instead of dropping armor, it is simply moved to a free slot in the inventory.

- **Scent Tracking**
> Once activated with /ww track, the werewolf will move slowly with their eyes closed (given by the Blindness effect) 
> and will begin to see a trail that leads to the target they have previously marked by right-clicking on them whilst in wolf form.

- **Full Transform**
> At this point, the werewolf has fully learned how to control its form.
> Using /ww transform, the werewolf can transform at any time.
> Werewolves under this level will be forced back into human form if outside during the day.
> (There is a 20-minute cooldown under default settings.)

- **Gold Immunity**
> Normally, gold swords deal double damage to werewolves. However, high-leveled werewolves become immune to this.