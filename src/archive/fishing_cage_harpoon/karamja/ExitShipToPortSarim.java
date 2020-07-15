package ngc.fishing_cage_harpoon.karamja;

import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class ExitShipToPortSarim extends BaseAction<ClientContext> {
    private int PLANK_ID;

    public ExitShipToPortSarim(ClientContext ctx, int plankId) {
        super(ctx, "Exit Ship");
        this.PLANK_ID = plankId;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 28 && ctx.objects.select().id(PLANK_ID).poll().valid();
    }

    @Override
    public void execute() {
        ctx.objects.select().id(PLANK_ID).poll().interact("Cross");
        sleep();
    }
}