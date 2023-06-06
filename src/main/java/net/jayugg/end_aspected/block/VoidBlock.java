package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.block.parent.DropExperienceBlock;

public class VoidBlock extends DropExperienceBlock {
    public VoidBlock(Properties properties) {
        super(properties, () -> 1);
    }
}
