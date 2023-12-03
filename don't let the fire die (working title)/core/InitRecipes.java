package core;

import items.*;
import java.util.HashMap;
import java.util.ArrayList;

public class InitRecipes {
    public static HashMap<String, ArrayList<Recipe>> run() {
        HashMap<String, ArrayList<Recipe>> out = new HashMap<String, ArrayList<Recipe>>();
        String tmpName;
        ArrayList<Recipe> tmp;
        
        tmpName = "Basic";
        tmp = new ArrayList<Recipe>();
        tmp.add(new Recipe(new Item[] {new TwigItem(), new CharcoalItem()}, new Item[] {new UnlitTorchItem()}));
        tmp.add(new Recipe(new Item[] {new TwigItem(), new TwigItem(), new TwigItem(), new StoneItem(), new StoneItem()}, new Item[] {new StoneAxeItem()}));
        tmp.add(new Recipe(new Item[] {new TwigItem(), new TwigItem(), new LogItem(), new LogItem(), new LogItem()}, new Item[] {new WoodenClubItem()}));
        tmp.add(new Recipe(new Item[] {new TwigItem(), new TwigItem(), new TwigItem(), new TwigItem(), new StoneItem()}, new Item[] {new StoneSpearItem()}));
        tmp.add(new Recipe(new Item[] {new LogItem(), new LogItem(), new LogItem(), new LogItem(), new LogItem(), new LogItem()}, new Item[] {new ChestItem()}));
        
        out.put(tmpName, tmp);
        
        tmpName = "Campfire";
        tmp = new ArrayList<Recipe>();
        tmp.add(new Recipe(new Item[] {new LogItem()}, new Item[] {new CharcoalItem()}));
        //tmp.add(new Recipe(new Item[] {new LogItem(), new LogItem(), new LogItem(), new LogItem()}, new Item[] {new StoneAxeItem(),new StoneAxeItem()}));
        
        out.put(tmpName, tmp);
        
        
        return out;
    }
}
