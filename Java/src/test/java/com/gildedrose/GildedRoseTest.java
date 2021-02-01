package com.gildedrose;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GildedRoseTest {

    public static final int SELL_IN = 10;
    public static final int QUALITY = 20;

    Item item;
    GildedRose app;

    @BeforeEach
    void setup() {
        item = new Item("demo", SELL_IN, QUALITY);
        final Item[] items = {item};
        app = new GildedRose(items);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test_toString() {
        assertEquals(item.toString(), "demo, " + SELL_IN + ", " + QUALITY);
    }

    @Test
    void test_backstage_item() {
        // "Backstage passes"（后台通行证）与"Aged Brie"（陈年布利奶酪）类似，
        // 其品质`Quality`会随着时间推移而提高；
        item.name = GildedRose.BACKSTAGE_PASSES_TO_A_TAFKAL_80_ETC_CONCERT;
        item.sellIn = 20;
        for (int i = 0; i < 10; i++) {
            app.updateQuality();
            assertEquals(item.sellIn, 20 - i - 1);
            assertEquals(item.quality, QUALITY + i + 1);
        }
    }

    @Test
    void test_backstage_item_lt_10() {
        // "Backstage passes"（后台通行证）与"Aged Brie"（陈年布利奶酪）类似，
        // 当还剩10天或更少的时候，品质`Quality`每天提高2；
        item.name = GildedRose.BACKSTAGE_PASSES_TO_A_TAFKAL_80_ETC_CONCERT;
        item.sellIn = 20;
        for (int i = 0; i < 10; i++) {
            app.updateQuality();
        }
        final int quality = item.quality;
        for (int i = 0; i < 5; i++) {
            app.updateQuality();
            assertEquals(item.quality, Math.min(quality + 2 * (i + 1), 50));
        }
    }

    @Test
    void test_backstage_item_lt_5() {
        // "Backstage passes"（后台通行证）与"Aged Brie"（陈年布利奶酪）类似，
        // 当还剩5天或更少的时候，品质`Quality`每天提高3；
        item.name = GildedRose.BACKSTAGE_PASSES_TO_A_TAFKAL_80_ETC_CONCERT;
        item.sellIn = 20;
        item.quality = 1;
        for (int i = 0; i < 15; i++) {
            app.updateQuality();
        }
        final int quality = item.quality;
        for (int i = 0; i < 5; i++) {
            app.updateQuality();
            assertEquals(item.quality, Math.min(quality + 3 * (i + 1), 50));
        }
    }

    @Test
    void test_backstage_item_outdated() {
        // "Backstage passes"（后台通行证）与"Aged Brie"（陈年布利奶酪）类似，
        // 但一旦过期，品质就会降为0
        item.name = GildedRose.BACKSTAGE_PASSES_TO_A_TAFKAL_80_ETC_CONCERT;
        item.sellIn = 20;
        for (int i = 0; i < 20; i++) {
            app.updateQuality();
        }
        app.updateQuality();
        assertEquals(item.quality, 0);
    }

    @Test
    void test_sulfuras_item() {
        // 传奇物品"Sulfuras"（萨弗拉斯—炎魔拉格纳罗斯之手）永不过期，也不会降低品质`Quality`
        item.name = GildedRose.SULFURAS_HAND_OF_RAGNAROS;
        for (int i = 0; i < 100; i++) {
            app.updateQuality();
            assertEquals(item.quality, QUALITY);
            assertEquals(item.sellIn, SELL_IN);
        }
    }

    @Test
    void test_aged_brie_item() {
        item.name = GildedRose.AGED_BRIE;
        // "Aged Brie"（陈年布利奶酪）的品质`Quality`会随着时间推移而提高
        for (int i = 0; i < 10; i++) {
            app.updateQuality();
            assertEquals(item.sellIn, SELL_IN - i - 1);
            assertEquals(item.quality, QUALITY + i + 1);
            assertTrue(item.quality <= 50);
        }
        for (int i = 0; i < 100; i++) {
            app.updateQuality();
        }
        assertEquals(item.quality, 50);
    }

    @Test
    void test_aged_brie_bounds() {
        item.name = GildedRose.AGED_BRIE;
        for (int i = 0; i < SELL_IN; i++) {
            app.updateQuality();
        }
        final int quality = item.quality;
        // "Aged Brie"（陈年布利奶酪）的品质`Quality`会随着时间推移而提高
        for (int i = 0; i < 100; i++) {
            app.updateQuality();
            assertEquals(item.sellIn, -(i + 1));
            assertTrue(item.quality <= 50 && item.quality >= 0);
            assertEquals(Math.min(quality + 2 * (i + 1), 50), item.quality);
        }
    }

    @Test
    void normal_item() {
        // 每天结束时，系统会降低每种物品的这两个数值
        item.name = "normal";
        for (int i = 0; i < SELL_IN; i++) {
            app.updateQuality();
            assertEquals(SELL_IN - i - 1, item.sellIn);
            assertEquals(QUALITY - i - 1, item.quality);
        }
    }

    @Test
    void normal_item_outdated() {
        // 一旦销售期限过期，品质`Quality`会以双倍速度加速下降
        item.name = "normal";
        for (int i = 0; i < SELL_IN; i++) {
            app.updateQuality();
        }
        int quality = item.quality;
        for (int i = 0; i < 5; i++) {
            app.updateQuality();
            assertTrue(item.sellIn < 0);
            assertEquals(quality - 2 * (i + 1), item.quality);
        }
    }

    @Test
    void normal_item_bounds() {
        // 物品的品质`Quality`永远不会为负值
        // 物品的品质`Quality`永远不会超过50
        item.name = "normal";
        for (int i = 0; i < 100; i++) {
            app.updateQuality();
            assertTrue(item.quality >= 0 && item.quality <= 50);
        }
    }
}
