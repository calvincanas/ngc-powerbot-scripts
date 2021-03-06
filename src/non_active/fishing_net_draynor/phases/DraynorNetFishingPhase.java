package scripts.fishing_net_draynor.phases;

import org.powerbot.script.rt4.ClientContext;
import scripts.fishing_net_draynor.actions.NetFishingAction;
import shared.templates.StructuredPhase;

public class DraynorNetFishingPhase extends StructuredPhase {

    public DraynorNetFishingPhase(ClientContext ctx, String name) {
        super(ctx, name);

        NetFishingAction netFishingAction = new NetFishingAction(ctx, "Net");

        this.setInitialAction(netFishingAction);
    }

    @Override
    public boolean moveToNextPhase() {
        return ctx.inventory.isFull();
    }
}
