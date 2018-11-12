# Configuration.java how it works!

Simply import it into your project and start using it!
Too show what you can do here are some examples:

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

        //Read Content
        if(!json.exists("costsForThisInHours")) {
            json.setDouble("costsForThisInHours", 4.5);
        } else {
            System.out.println("It seems you already saved that!");
        }
        
        json.save();


```
