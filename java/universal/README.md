# Configuration.java how it works!

Simply import it into your project and start using it!

Too show what you can do here are some examples:

Examples below are only for the Json variant but every other variant works exactly the same they got the same methods and all.
There are some restrictions due to the way some filetypes are build for example. PROPERTY(.properties) since it can only store one entry per line!

But if a variant can't use a method it won't be available so u can use everything there is free how you want to!

___

## Store things in the configuration

```java

        String path = System.getProperty("user.dir") + "\\output";
        
        //Initialize the Configuration
        Configuration.Json json = new Configuration.Json(path, "test");
        
        //Add Content
        json.setBoolean("isThisEasyToUse", true);
        json.setInt("skillNeededToUse", 0);
        Date today = new Date();
        json.setDate("whenToStartUsingIt", today);
        
        //Save To File
        json.save();

```
## Read things from the configuration

```java

        String path = System.getProperty("user.dir") + "\\output";

        //Initialize the Configuration
        Configuration.Json json = new Configuration.Json(path, "test");

        //Load From File
        json.load();

        //Read Content
        boolean isThisEasyToUse = json.getBoolean("isThisEasyToUse");
        int skillNeededToUse = json.getInteger("skillNeededToUse");
        Date startedUsingThis = json.getDate("whenToStartUsingIt");

```

## Update things in the configuration

```java

        String path = System.getProperty("user.dir") + "\\output";

        //Initialize the Configuration
        Configuration.Json json = new Configuration.Json(path, "test");

        //Load From File
        json.load();

        //Update/Add Content
        if(!json.exists("costsForThisInHours")) {
            json.setDouble("costsForThisInHours", 4.5);
        } else {
            System.out.println("It seems you already saved that!");
        }
        
        json.save();


```
