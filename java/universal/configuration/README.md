# Configuration for GSONFile.java

How to use Configuration 

Put this in the Configuration.java and u are ready to go!


```java
    @Getter @Setter
    private ConfigurationHeadCategory lobby;

    @Getter @Setter
    private ConfigurationHeadCategory game;

    public Configuration() {
        //SIMPLE TEST PUT INSIDE WHATEVER U WANT
        ConfigurationSubCategory team = new ConfigurationSubCategory("team",
                new ConfigurationElement("item_slot", 0),
                new ConfigurationElement("inventory_name", "§eTeam Selection"),
                new ConfigurationElement("item_displayname", "§2§l» §e§lChoose a Team"),
                new ConfigurationElement("item_data", 0),
                new ConfigurationElement("item_lore", Arrays.asList("§8Select in which team u want to play")),
                new ConfigurationElement("item_material", "WRITTEN_BOOK"));
        ConfigurationSubCategory kit = new ConfigurationSubCategory("kit",
                new ConfigurationElement("item_slot", 4),
                new ConfigurationElement("inventory_name", "§aKit Selection"),
                new ConfigurationElement("item_displayname", "§2§l» §a§lChoose a Kit"),
                new ConfigurationElement("item_data", 0),
                new ConfigurationElement("item_lore", Arrays.asList("§8Select a Kit to start of with with some items")),
                new ConfigurationElement("item_material", "CHEST"));
        ConfigurationSubCategory help = new ConfigurationSubCategory("help",
                new ConfigurationElement("item_slot", 7),
                new ConfigurationElement("item_displayname", "§2§l» §6§lNeed Help?"),
                new ConfigurationElement("item_data", 0),
                new ConfigurationElement("item_lore", Arrays.asList("§8Never played this gamemode?\", \"§8Then §erightclick §8to read about it")),
                new ConfigurationElement("item_material", "CHEST"),
                new ConfigurationElement("item_content", Arrays.asList("This is a simple explanation of this gamemode! You start on a island alone and search for chest to loot some items to fight your way to the last man standing")));

        ConfigurationSubCategory time = new ConfigurationSubCategory("time",
                new ConfigurationElement("maxTimeInSec", 480),
                new ConfigurationElement("gameEndInSec", 10),
                new ConfigurationElement("gameStartInSec", 4));

        this.lobby = new ConfigurationHeadCategory("lobby", team, kit, help);
        this.game = new ConfigurationHeadCategory("game", time);

    }
    
```

```java
        //STEP 1
        Configuration configuration = new Configuration();

        TestFile testFile = new TestFile(System.getProperty("user.dir") + "?Testing?target?test.json");
        testFile.write(new Configuration());
        //STEP 2
        Configuration readConfig = testFile.get(Configuration.class);

        System.out.println(readConfig.getGame().getSubCategory("time").getElement("maxTimeInSec").getValueAs(Integer.class));
```
    
In **STEP 1** the config.json and its filepath will be created and then the default value of the Configuration from above will be written into it!
In **STEP 2** the content of the config.json gets read and put in a new Configuration instance after that u can use it how u designed the COnfiguration.java!
    
**BE AWARE:** U can make as many HeadCategory's and SubCategorys as u wish but u can only put SubCategorys into HeadCategorys nothing else!
I recommend if u only need one category simple use the ConfigurationCategory.java its designed to provide a simple way to categorize your configuration
