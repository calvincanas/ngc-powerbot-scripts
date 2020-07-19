package ngc.misc_bucket_filler;


import ngc._resources.models.BaseAction;
import ngc._resources.enums.ITEM_IDS;
import ngc._resources.enums.OBJECT_IDS;
import ngc._resources.tools.CommonAreas;
import ngc._resources.tools.RsLookup;
import org.powerbot.script.Area;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import static org.powerbot.script.Condition.sleep;

public class WalkToFountainAction extends BaseAction<ClientContext> {
    private CommonAreas areas = new CommonAreas();
    private RsLookup lookup = new RsLookup();


    private Area fountainArea = areas.getVarrockFountain();
    public static final Tile[] path = {new Tile(3166, 3487, 0), new Tile(3170, 3487, 0), new Tile(3174, 3488, 0), new Tile(3178, 3488, 0), new Tile(3182, 3488, 0), new Tile(3186, 3488, 0), new Tile(3190, 3488, 0), new Tile(3192, 3484, 0), new Tile(3192, 3480, 0), new Tile(3192, 3476, 0), new Tile(3194, 3472, 0)};
    public WalkToFountainAction(ClientContext ctx) {
        super(ctx, "To Fountain");
    }

    @Override
    public boolean activate() {
        boolean fountainVisible = ctx.objects.select().id(lookup.getId(OBJECT_IDS.Fountain_5125)).select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.inViewport();
            }
        }).poll().valid();
        boolean allEmptyBuckets = ctx.inventory.select().id(lookup.getId(ITEM_IDS.EmptyBucket_1925)).count() == 28;
        return !fountainVisible && allEmptyBuckets;
    }

    @Override
    public void execute() {
        path[path.length - 1] = fountainArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
