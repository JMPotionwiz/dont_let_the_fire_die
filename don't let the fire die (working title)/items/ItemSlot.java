package items;
public class ItemSlot {
    public Item item = null;
    public int count = 0;
    public ItemSlot() {}
    public ItemSlot(Item i, int c) {
        this.item = i;
        this.count = c;
    }
}