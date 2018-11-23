package de.zm4xi.chmod.spigot.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpigotFile {

	private File file;
	private FileConfiguration cfg;
	
	public SpigotFile(File datafolder, String name) {
		if (!datafolder.exists()) {
			datafolder.mkdir();
		}
		file = new File(datafolder.getPath(), name + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		cfg = YamlConfiguration.loadConfiguration(file);
		cfg.options().copyDefaults(true);
	}
	
	public FileConfiguration getConfig() {
		return cfg;
	}
	
	public File getFile() {
		return file;
	}
	
	public void reload() {
		cfg = YamlConfiguration.loadConfiguration(file);
	}
	
	public void addDefault(String path, Object value) {
		cfg.addDefault(path, value);
		save();
	}
	
	public boolean getBoolean(String path) {
		return cfg.getBoolean(path);
	}
	
	public void set(String path, Object value) {
		cfg.set(path, value);
		save();
	}
	
	public String getString(String path) {
		return cfg.getString(path);
	}
	
	public int getInt(String path) {
		return cfg.getInt(path);
	}
	
	public double getDouble(String path) {
		return cfg.getDouble(path);
	}
	
	public List<String> getStringList(String path) {
		return cfg.getStringList(path);
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItemStack(String path, String displayName) {
		
		ItemStack item = new ItemStack(Material.getMaterial(getInt(path + displayName + ".type")));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		
		item.setAmount(getInt(path + meta.getDisplayName() + ".amount"));
		meta.setLore(getStringList(path + meta.getDisplayName() + ".lore"));
		item.getData().setData((byte)getInt(path + meta.getDisplayName() + ".data"));
		for(String st : getStringList(path + meta.getDisplayName() + ".enchantments")) {
			meta.addEnchant(Enchantment.getById(Integer.parseInt(st.split(", ")[0])), Integer.parseInt(st.split(", ")[1]), true);
		}
		
		item.setItemMeta(meta);
		return item;
	}
	
	@SuppressWarnings("deprecation")
	public void setItemStack(String path, ItemStack item) {
		if (item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				
				List<String> list = new ArrayList<>();
				if (meta.hasEnchants()) {
					for(Enchantment ent : item.getEnchantments().keySet()) {
						list.add(ent.getId() + ", " + item.getEnchantments().get(ent));
					}
					set(path + meta.getDisplayName() + ".enchantments", list);
				}
				if (meta.hasLore())
					set(path + meta.getDisplayName() + ".lore", meta.getLore());
				set(path + meta.getDisplayName() + ".type", item.getTypeId());
				set(path + meta.getDisplayName() + ".amount", item.getAmount());
				set(path + meta.getDisplayName() + ".data", item.getData().getData());

		} else {
			return;
		}
		save();
		reload();
	}
	
	public void save() {
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
