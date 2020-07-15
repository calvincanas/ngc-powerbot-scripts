package ngc._resources.functions;

import ngc._resources.Items;
import ngc._resources.models.LootItem;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static org.powerbot.script.Condition.sleep;

public class CommonFunctions {

    public static int getCombatSkill(ClientContext ctx) {
        switch( ctx.combat.style().name().toLowerCase() ) {
            case "accurate":
                return Constants.SKILLS_ATTACK;
            case "aggressive":
                return Constants.SKILLS_STRENGTH;
            case "defensive":
                return Constants.SKILLS_DEFENSE;
            default:
                return Constants.SKILLS_RANGE;
        }
    }

    public static int promptForCombatStyle(ClientContext ctx) {
        String selectedStyle = (String) JOptionPane.showInputDialog(null, "Combat Type", "Select Style", QUESTION_MESSAGE, null, new String[] {"Melee", "Ranged", "Magic"}, "Melee");

        if( selectedStyle.equals("Melee") ) {
            switch( ctx.combat.style().name().toLowerCase() ) {
                case "accurate":
                    return Constants.SKILLS_ATTACK;
                case "aggressive":
                    return Constants.SKILLS_STRENGTH;
                case "defensive":
                    return Constants.SKILLS_DEFENSE;
            }
        }

        if( selectedStyle.equals("Magic") ) {
            return Constants.SKILLS_MAGIC;
        }

        if( selectedStyle.equals("Ranged") ) {
            return Constants.SKILLS_RANGE;
        }

        // Error
        return -1;
    }

    public static int promptForArrowType() {
        String arrowType = (String) JOptionPane.showInputDialog(null, "Arrow Type", "Select Ammo", QUESTION_MESSAGE, null, new String[] {"Bronze Arrow", "Iron Arrow", "Iron Bolt", "Steel Arrow", "Mithril Arrow", "Adamant Arrow", "Rune Arrow"}, "Iron");

        switch( arrowType ) {
            case "Bronze Arrow":
                return Items.BRONZE_ARROW_882;
            case "Iron Arrow":
                return Items.IRON_ARROW_884;
            case "Iron Bolt":
                return Items.IRON_BOLTS_9140;
            case "Steel Arrow":
                return Items.STEEL_ARROW_886;
            case "Mithril Arrow":
                return Items.MITHRIL_ARROW_888;
            case "Adamant Arrow":
                return Items.ADAMANT_ARROW_890;
            case "Rune Arrow":
                return Items.RUNE_ARROW_892;
        }

        return 0;
    }

    public static boolean promptForBuryBones() {
        // Bury Bones
        int buryBones = JOptionPane.showOptionDialog(null,
                "Loot & bury bones?",
                "Train Prayer?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[] {"Yes", "No"},
                "Yes");

        return buryBones == 0;
    }

    public static String promptForSelection(String title, String message, String[] options) {
        return (String) JOptionPane.showInputDialog(null,
                message,
                title,
                QUESTION_MESSAGE,
                null,
                options,
                options[0]);

    }

    public static String promptForSelection(String title, String message, String optOne, String optTwo) {
        int option = JOptionPane.showOptionDialog(null,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[] {optOne, optTwo},
                optOne);

        if( option == 0 ) {
            return optOne;
        }

        return optTwo;

    }

    public static int promptForQuantity(String title) {
        return Integer.parseInt(JOptionPane.showInputDialog(null,
                title));
    }

    public static int promptForNumber(String title) {
        return Integer.parseInt(JOptionPane.showInputDialog(null,
                title));
    }

    public static String promptForName(String title) {
        return (JOptionPane.showInputDialog(null,
                title));
    }

    public static void reloadCannon(ClientContext ctx) {
        GameObject cannon = ctx.objects.select().id(6).nearest().poll();

        if( cannon.inViewport() ) {
            cannon.interact(("Fire"), cannon.name());
            sleep(3000);
        }
    }
    public static void fixCannon(ClientContext ctx) {
        GameObject cannon = ctx.objects.select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.name().toLowerCase().contains("broken");
            }
        }).nearest().poll();

        if( cannon.inViewport() ) {
            cannon.interact(("Repair"));
            sleep(3000);
        }
    }
    public static void walkToSafespot(ClientContext ctx, Tile safespot) {
        if( safespot != null ) {
            int x = safespot.matrix(ctx).centerPoint().x;
            int y = safespot.matrix(ctx).centerPoint().y;
            int r = Random.nextInt(-10, 10);

            if( safespot.distanceTo(ctx.players.local()) > 0 ) {
                if( safespot.matrix(ctx).inViewport() ) {
                    ctx.input.move(new Point((x + r), (y + r)));

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.menu != null && ctx.menu.items() != null && ctx.menu.items().length > 0;
                        }
                    }, 50, 50);


                    if( ctx.menu.items()[0].equalsIgnoreCase("walk here") ) {
                        safespot.matrix(ctx).click();
                    } else {
                        safespot.matrix(ctx).interact("Walk here");
                    }
                } else {
                    ctx.movement.step(safespot);
                    sleep();
                }

            }
        }
    }

    public static void pickUpCannon( ClientContext ctx) {
        GameObject cannon = ctx.objects.select().id(6).poll();

        if( cannon.inViewport() ) {
            cannon.interact(("Pick-up"), cannon.name());
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().id(Items.CANNON_BASE_6).count() == 1;
                }
            }, 250, 20);
        }
    }

    public static void teleportAway( ClientContext ctx) {
        Item sceptre = ctx.inventory.select().id(Items.SKULL_SCEPTRE_I_21276).poll();

        if( sceptre.valid() ) {
            sceptre.interact("Invoke", sceptre.name());
            sleep(3000);
        } else {
            // Teletab
            Item tab = ctx.inventory.select().select(new Filter<Item>() {
                @Override
                public boolean accept(Item item) {
                    return item.name().toLowerCase().contains("teleport");
                }
            }).poll();

            if( tab.valid() ) {
                tab.click();
            } else {
                // Glory Teleport
                ctx.game.tab(Game.Tab.EQUIPMENT);
                Item ammy = ctx.equipment.itemAt(Equipment.Slot.NECK);
                if( ammy.name().toLowerCase().contains("glory") ) {
                    ammy.interact("Edgeville");
                    sleep(4000);
                }
            }
        }
    }

    public static void slayerRingTeleport(ClientContext ctx, int destinationSelection) {
        int[] slayerRings = new int[] {Items.SLAYER_RING_8_11866, Items.SLAYER_RING_7_11867, Items.SLAYER_RING_6_11868, Items.SLAYER_RING_5_11869, Items.SLAYER_RING_4_11870, Items.SLAYER_RING_3_11871, Items.SLAYER_RING_2_11872, Items.SLAYER_RING_1_11873};

        Item i = ctx.inventory.select().id(slayerRings).first().poll();

        if( i.valid() ) {
            i.interact("Rub", i.name());

            // Wait for prompt
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(219, 1, 1).visible();
                }
            }, 100, 20);

            // Click teleport
            ctx.widgets.component(219, 1, 1).click();
            sleep();

            // Wait for destination selection
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(219, 1, 1).visible();
                }
            }, 100, 20);

            // Select Destination
            ctx.widgets.component(219, 1, destinationSelection).click();
        }
    }


    public static void usePotion(ClientContext ctx, int[] potionIds) {
        Item pot = ctx.inventory.select().id(potionIds).first().poll();

       // System.out.println("--------POT-------");
       // System.out.println(pot.name());

        if( pot.valid() ) {
            pot.interact("Drink", pot.name());
            sleep();

            if( pot.name().contains("Prayer") && ctx.prayer.prayersActive() ) {
                ctx.prayer.quickPrayer(true);
            }
        }
    }

    public static void dropItem(ClientContext ctx, int itemId) {
        Item item = ctx.inventory.select().id(itemId).first().poll();

        if( item.valid() ) {
            ctx.input.send("{VK_SHIFT down}");
            item.click();
            sleep();
            ctx.input.send("{VK_SHIFT up}");

        }
    }

    public static boolean isValidLoot(ClientContext ctx, GroundItem groundItem, LootItem i, int maxDistanceToLoot, boolean ignoreB2P) {
        return (!ctx.inventory.isFull() || (groundItem.stackable() && ctx.inventory.select().id(groundItem.id()).count() > 0))
                && groundItem.stackSize() >= i.getMinStackSize()
                && groundItem.tile().matrix(ctx).reachable()
/*
                && (i.getMaxInventoryCount() < 0 || ((!groundItem.stackable() && ctx.inventory.select().id(groundItem.id()).count() < i.getMaxInventoryCount()) || (groundItem.stackable() && ctx.inventory.select().id(groundItem.id()).poll().stackSize() < i.getMaxInventoryCount())))
*/
                && groundItem.inViewport() && (maxDistanceToLoot < 0 || groundItem.tile().distanceTo(ctx.players.local()) <= maxDistanceToLoot);
    }

    public static void moveMouseOffscreen(ClientContext ctx, boolean leftSide) {
        int x;
        if( leftSide ) {
            x = -10;
        } else {
            // Right side (for inventory actions)
            x = 1000;
        }

        int y = Random.nextInt(0, 550);

        ctx.input.move(new Point(x, y));
    }

    public static int[] allFoodIds() {
        return new int[] {
                Items.TUNA_361,
                Items.SALMON_329,
                Items.TROUT_333,
                Items.LOBSTER_379,
                Items.SWORDFISH_373,
                Items.MONKFISH_7946,
                Items.BASS_365,
                Items.SHARK_385,
                Items.MEAT_PIZZA_2293,
                2295, // Half Meat Pizza
                Items.PEACH_6883,
                Items.CAKE_1891,
                1893, // 2/3 Cake
                Items.SLICE_OF_CAKE_1895,
                Items.ANGLERFISH_13441,
                Items.JUG_OF_WINE_1993
        };
    }

    public static int[] guthansEquipment() {
        return new int[] {
                Items.GUTHANS_HELM_4724,
                Items.GUTHANS_HELM_100_4904,
                Items.GUTHANS_HELM_75_4905,
                Items.GUTHANS_HELM_50_4906,
                Items.GUTHANS_HELM_25_4907,
                Items.GUTHANS_PLATEBODY_4728,
                Items.GUTHANS_PLATEBODY_100_4916,
                Items.GUTHANS_PLATEBODY_75_4917,
                Items.GUTHANS_PLATEBODY_50_4918,
                Items.GUTHANS_PLATEBODY_25_4919,
                Items.GUTHANS_CHAINSKIRT_4730,
                Items.GUTHANS_CHAINSKIRT_100_4922,
                Items.GUTHANS_CHAINSKIRT_75_4923,
                Items.GUTHANS_CHAINSKIRT_50_4924,
                Items.GUTHANS_CHAINSKIRT_25_4925,
                Items.GUTHANS_WARSPEAR_4726,
                Items.GUTHANS_WARSPEAR_100_4910,
                Items.GUTHANS_WARSPEAR_75_4911,
                Items.GUTHANS_WARSPEAR_50_4912,
                Items.GUTHANS_WARSPEAR_25_4913,
                Items.BERSERKER_RING_I_11773
        };
    }

    // Slayer Tower Data
    public static final Tile[] pathToSlayerStairsLevelOne = {new Tile(3423, 3538, 0), new Tile(3419, 3538, 0), new Tile(3415, 3538, 0), new Tile(3412, 3542, 0), new Tile(3415, 3545, 0), new Tile(3419, 3545, 0), new Tile(3423, 3545, 0), new Tile(3427, 3545, 0), new Tile(3427, 3549, 0), new Tile(3425, 3553, 0), new Tile(3421, 3555, 0), new Tile(3418, 3558, 0), new Tile(3415, 3561, 0), new Tile(3413, 3565, 0), new Tile(3416, 3568, 0), new Tile(3420, 3569, 0), new Tile(3423, 3572, 0), new Tile(3427, 3573, 0), new Tile(3431, 3573, 0), new Tile(3435, 3573, 0), new Tile(3439, 3573, 0), new Tile(3443, 3573, 0), new Tile(3446, 3570, 0), new Tile(3446, 3566, 0), new Tile(3446, 3562, 0), new Tile(3442, 3560, 0), new Tile(3438, 3559, 0), new Tile(3436, 3555, 0), new Tile(3436, 3551, 0), new Tile(3437, 3547, 0), new Tile(3437, 3543, 0)};
    public static final int slayerStairsLevelOneId = 2114;
    public static final Tile[] pathToSlayerDoorLevelTwo = {new Tile(3433, 3537, 1), new Tile(3437, 3535, 1), new Tile(3441, 3535, 1), new Tile(3445, 3536, 1), new Tile(3447, 3540, 1), new Tile(3447, 3544, 1), new Tile(3447, 3548, 1), new Tile(3447, 3552, 1), new Tile(3444, 3555, 1), new Tile(3441, 3558, 1), new Tile(3439, 3562, 1), new Tile(3436, 3566, 1), new Tile(3433, 3570, 1), new Tile(3430, 3573, 1), new Tile(3426, 3573, 1), new Tile(3422, 3573, 1), new Tile(3418, 3573, 1), new Tile(3414, 3571, 1), new Tile(3413, 3567, 1), new Tile(3413, 3563, 1), new Tile(3417, 3562, 1), new Tile(3421, 3562, 1), new Tile(3424, 3559, 1), new Tile(3427, 3556, 1)};

    public static ArrayList<Npc> getNearbyNpcList(ClientContext ctx) {
        ArrayList npcs = new ArrayList();

        for( Npc n : ctx.npcs.select().viewable() ) {
            npcs.add(n);
        }

        return new ArrayList<>(new HashSet<>(npcs));


    }

    public static double xpPerHour(ClientContext ctx, int combatSkill, double startXP, long runtime) {
        // Pull XP for combat style
        int currentXP = ctx.skills.experience(combatSkill);
        double xpGained = currentXP - startXP;
        return (((1000 * 60 * 60) / runtime) * xpGained);
    }
}
