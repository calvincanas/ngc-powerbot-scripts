package ngc.mining_rune_ess;

import ngc._resources.actions._template.BaseAction;
import ngc._resources.enums.NPC_IDS;
import ngc._resources.functions.RsLookup;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class AuburyTeleport extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();

    private int AuburyId = lookup.getId(NPC_IDS.Aubury_637);

    public AuburyTeleport(ClientContext ctx) {
        super(ctx, "Teleport");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 0 && ctx.npcs.select().id(AuburyId).poll().inViewport() && !ctx.players.local().interacting().valid();
    }

    @Override
    public void execute() {
        ctx.npcs.select().id(AuburyId).poll().interact("Teleport");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().tile().x() > 6000 || ctx.players.local().tile().y() > 6000;
            }
        }, 250, 10);
    }
}
