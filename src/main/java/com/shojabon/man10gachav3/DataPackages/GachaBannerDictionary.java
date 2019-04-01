package com.shojabon.man10gachav3.DataPackages;


import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by sho on 2018/07/12.
 */
public class GachaBannerDictionary {
    HashMap<Integer, ItemStack> banner = new HashMap<>();
    HashMap<String, ItemStack> symbol = new HashMap<>();

    public GachaBannerDictionary(){
        banner.put(0, new SBannerItemStack((short) 15).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNLEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(1, new SBannerItemStack((short) 15).pattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_TOP_LEFT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_CENTER)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(2, new SBannerItemStack((short) 15).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNLEFT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(3, new SBannerItemStack((short) 15).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(4,new SBannerItemStack((short)15).pattern(new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(5,new SBannerItemStack((short)15).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNRIGHT)).pattern(new Pattern(DyeColor.WHITE, PatternType.CURLY_BORDER)).pattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_LEFT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(6, new SBannerItemStack((short) 0).pattern(new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_MIRROR)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(7, new SBannerItemStack((short) 15).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.DIAGONAL_RIGHT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNLEFT)).pattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(8, new SBannerItemStack((short) 15).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());
        banner.put(9, new SBannerItemStack((short) 0).pattern(new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT)).pattern(new Pattern(DyeColor.WHITE, PatternType.BORDER)).build());

        symbol.put("plus",  new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS)).pattern(new Pattern(DyeColor.BLUE, PatternType.BORDER)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).build());
        symbol.put("minus",  new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.BORDER)).build());
        symbol.put("dot", new SBannerItemStack((short)4).pattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE)).build());
        symbol.put("back", new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§c§l§n戻る").build());

    }

    public ItemStack getItem(int id){
        return banner.get(id);
    }

    public ItemStack getSymbol(String id){
        return symbol.get(id);
    }
}
