package net.jayugg.end_aspected.block.parent;

public interface IVeinNetworkNode extends IVeinNetworkElement {
    @Override
    default boolean isNode() {
        return true;
    }
}
