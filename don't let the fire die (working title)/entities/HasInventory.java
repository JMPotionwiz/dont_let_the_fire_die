package entities;

import items.ItemSlot;
import items.Item;

public interface HasInventory { //Entities that can hold items
    ItemSlot items[] = null;
    public Item getItem(int slot);
    public int getItemCount(int slot);
    public void setItem(Item i, int c, int slot);
    public void setItemCount(int c, int slot);
    public int totalSlots();
}
