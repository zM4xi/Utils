import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ItemBuilder {

    @Getter(AccessLevel.PROTECTED)
    private Material material;
    @Getter(AccessLevel.PROTECTED)
    private int amount = 1;
    @Getter(AccessLevel.PROTECTED)
    private byte data = (byte) 0;
    @Getter(AccessLevel.PROTECTED)
    private short durability;
    @Getter(AccessLevel.PROTECTED)
    private List<String> lore = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED)
    private String displayName;
    @Getter(AccessLevel.PROTECTED)
    private boolean unbreakable = false;
    @Getter(AccessLevel.PROTECTED)
    private List<ItemFlag> itemFlags = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED)
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    @Getter(AccessLevel.PROTECTED)
    private Multimap<Attribute, AttributeModifier> attributeModifiers = ArrayListMultimap.create();

    public ItemBuilder(Material material) {
        this.material = material;
    }

    /**
     * Initializes the itemstack and sets all predefined values
     * @return {@link ItemStack} with all values
     */
    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        MaterialData materialData = itemStack.getData();
        itemMeta.setUnbreakable(unbreakable);
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemMeta.setAttributeModifiers(attributeModifiers);
        itemMeta.addItemFlags(itemFlags.toArray(ItemFlag[]::new));
        itemStack.setItemMeta(itemMeta);
        materialData.setData(data);
        itemStack.setData(materialData);
        itemStack.addUnsafeEnchantments(enchantments);
        itemStack.setAmount(amount);
        itemStack.setDurability(durability);
        return itemStack;
    }

    //region Setter

    public ItemBuilder setLore(String ... lines) {
        this.lore.addAll(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder setAttributes(Attribute attribute, AttributeModifier modifier) {
        this.attributeModifiers.put(attribute, modifier);
        return this;
    }

    public ItemBuilder setItemFlags(ItemFlag itemFlag, ItemFlag ... itemFlags) {
        this.itemFlags.add(itemFlag);
        if(itemFlags != null) {
            this.itemFlags.addAll(Arrays.asList(itemFlags));
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setData(byte data) {
        this.data = data;
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        this.durability = durability;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    //endregion

    public static class LeatherArmor extends ItemBuilder {

        @Getter
        private int red = 0, green = 0, blue = 0;

        public LeatherArmor(Material material) {
            super(material);
        }

        @Override
        public ItemStack build() {
            ItemStack itemStack = new ItemStack(getMaterial());
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            MaterialData materialData = itemStack.getData();
            itemMeta.setUnbreakable(isUnbreakable());
            itemMeta.setDisplayName(getDisplayName());
            itemMeta.setLore(getLore());
            itemMeta.setAttributeModifiers(getAttributeModifiers());
            itemMeta.addItemFlags(getItemFlags().toArray(ItemFlag[]::new));
            itemMeta.setColor(Color.fromRGB(red, green, blue));
            itemStack.setItemMeta(itemMeta);
            materialData.setData(getData());
            itemStack.setData(materialData);
            itemStack.addUnsafeEnchantments(getEnchantments());
            itemStack.setAmount(getAmount());
            itemStack.setDurability(getDurability());
            return itemStack;
        }

        //region Setter
        public LeatherArmor setRed(int red) {
            this.red = red;
            return this;
        }

        public LeatherArmor setGreen(int green) {
            this.green = green;
            return this;
        }

        public LeatherArmor setBlue(int blue) {
            this.blue = blue;
            return this;
        }

        public LeatherArmor setRGB(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            return this;
        }
        //endregion
    }

    public static class Potion extends ItemBuilder {

        @Getter
        private PotionType potionType;
        @Getter
        private boolean extended = false, upgraded = false;
        @Getter
        private int red = 0, green = 0, blue = 0;
        @Getter
        private HashMap<PotionEffect, Boolean> customEffects = new HashMap<>();

        public Potion(PotionType potionType) {
            super(Material.POTION);
            this.potionType = potionType;
        }

        @Override
        public ItemStack build() {
            ItemStack itemStack = new ItemStack(getMaterial());
            PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
            MaterialData materialData = itemStack.getData();
            itemMeta.setUnbreakable(isUnbreakable());
            itemMeta.setDisplayName(getDisplayName());
            itemMeta.setLore(getLore());
            itemMeta.setAttributeModifiers(getAttributeModifiers());
            itemMeta.addItemFlags(getItemFlags().toArray(ItemFlag[]::new));
            itemMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
            itemMeta.setMainEffect(potionType.getEffectType());
            for(PotionEffect effect : customEffects.keySet()) {
                itemMeta.addCustomEffect(effect, customEffects.get(effect));
            }
            itemStack.setItemMeta(itemMeta);
            materialData.setData(getData());
            itemStack.setData(materialData);
            itemStack.addUnsafeEnchantments(getEnchantments());
            itemStack.setAmount(getAmount());
            itemStack.setDurability(getDurability());
            return itemStack;
        }

        //region Setter

        public Potion addCustomEffect(PotionEffectType potionEffectType, int duration, int amplifier, boolean ambient, boolean particles, boolean icon, boolean overrideEffects) {
            customEffects.put(new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon), overrideEffects);
            return this;
        }

        public Potion setExtended(boolean extended) {
            this.extended = extended;
            return this;
        }

        public Potion setUpgraded(boolean upgraded) {
            this.upgraded = upgraded;
            return this;
        }

        public Potion setRed(int red) {
            this.red = red;
            return this;
        }

        public Potion setGreen(int green) {
            this.green = green;
            return this;
        }

        public Potion setBlue(int blue) {
            this.blue = blue;
            return this;
        }

        public Potion setRGB(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            return this;
        }
        //endregion

    }

    public static class PlayerSkull extends ItemBuilder {

        @Getter
        private UUID owningPlayer;

        public PlayerSkull(UUID uuid) {
            super(Material.SKELETON_SKULL);
            this.owningPlayer = uuid;
        }

        @Override
        public ItemStack build() {
            ItemStack itemStack = new ItemStack(getMaterial());
            SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
            MaterialData materialData = itemStack.getData();
            itemMeta.setUnbreakable(isUnbreakable());
            itemMeta.setDisplayName(getDisplayName());
            itemMeta.setLore(getLore());
            itemMeta.setAttributeModifiers(getAttributeModifiers());
            itemMeta.addItemFlags(getItemFlags().toArray(ItemFlag[]::new));
            itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(this.owningPlayer));
            itemStack.setItemMeta(itemMeta);
            materialData.setData(getData());
            itemStack.setData(materialData);
            itemStack.addUnsafeEnchantments(getEnchantments());
            itemStack.setAmount(getAmount());
            itemStack.setDurability(getDurability());
            return itemStack;
        }
    }

    public static class Book extends ItemBuilder {

        @Getter
        private String title = "", author = "";
        @Getter
        private BookMeta.Generation generation = BookMeta.Generation.ORIGINAL;
        @Getter
        private List<String> pages = new ArrayList<>();

        public Book(Material book) {
            super(book);
        }

        @Override
        public ItemStack build() {
            ItemStack itemStack = new ItemStack(getMaterial());
            BookMeta itemMeta = (BookMeta) itemStack.getItemMeta();
            MaterialData materialData = itemStack.getData();
            itemMeta.setUnbreakable(isUnbreakable());
            itemMeta.setDisplayName(getDisplayName());
            itemMeta.setLore(getLore());
            itemMeta.setAttributeModifiers(getAttributeModifiers());
            itemMeta.addItemFlags(getItemFlags().toArray(ItemFlag[]::new));
            itemMeta.setTitle(title);
            itemMeta.setAuthor(author);
            itemMeta.setGeneration(generation);
            itemMeta.setPages(pages);
            itemStack.setItemMeta(itemMeta);
            materialData.setData(getData());
            itemStack.setData(materialData);
            itemStack.addUnsafeEnchantments(getEnchantments());
            itemStack.setAmount(getAmount());
            itemStack.setDurability(getDurability());
            return itemStack;
        }

        //region Setter

        public Book setGeneration(BookMeta.Generation generation) {
            this.generation = generation;
            return this;
        }

        public Book setTitle(String title) {
            this.title = title;
            return this;
        }

        public Book setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Book addPages(String ... pages) {
            this.pages.addAll(Arrays.asList(pages));
            return this;
        }

        //endregion


    }

}
