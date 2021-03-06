package scripts.combat_lumby;


import shared.templates.AbstractAction;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import static org.powerbot.script.Condition.sleep;

public class LootAction extends AbstractAction<ClientContext> {
    private final int[] ITEM_IDS = {1440, 562, 557};


    public LootAction(ClientContext ctx) {
        super(ctx, "Looting");
    }

    @Override
    public boolean activate() {
        return !ctx.players.local().inCombat();
    }

    @Override
    public void execute() {
        GameObject groundItem = ctx.objects.select().id(ITEM_IDS).nearest().poll();
        if( groundItem.inViewport() )
            groundItem.interact("Take");
        sleep();
    }
}
