import lombok.Builder;
import lombok.Singular;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

@Builder(toBuilder = true, builderClassName = "BookBuilder", buildMethodName = "create")
public class CustomBook {

    private String title, author, displayName;
    private BookMeta.Generation generation;
    @Builder.Default
    private boolean unbreakable = false;
    @Singular("addPage")
    private List<BaseComponent[]> pages;
    @Singular("addLore")
    private List<String> lore;

    public ItemStack generate() {
        ItemStack stack = new ItemStack(Material.BOOK, 1);
        BookMeta meta = (BookMeta) stack.getItemMeta();
        if (title != null) meta.setTitle(title);
        if (author != null) meta.setAuthor(author);
        if (displayName != null) meta.setDisplayName(displayName);
        if (generation != null) meta.setGeneration(generation);
        if (lore != null) meta.setLore(lore);
        if (pages != null) meta.spigot().setPages(pages);
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return stack;
    }

}
