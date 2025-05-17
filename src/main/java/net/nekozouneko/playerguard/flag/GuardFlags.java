package net.nekozouneko.playerguard.flag;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import net.nekozouneko.commons.lang.collect.Collections3;
import net.nekozouneko.playerguard.PGUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum GuardFlags {

    BREAK("break", null, Flags.BLOCK_BREAK),
    PLACE("place", null, Flags.BLOCK_PLACE),
    INTERACT("interact", true, Flags.USE, Flags.INTERACT, Flags.CHEST_ACCESS, Flags.USE_ANVIL),
    PVP("pvp", false, Flags.PVP),
    ENTITY_DAMAGE("entity-damage", true, Flags.DAMAGE_ANIMALS),
    ENTRY("entry", null, Flags.ENTRY, Flags.CHORUS_TELEPORT),
    PISTONS("pistons", true, Flags.PISTONS, Flags.USE_DRIPLEAF);

    public enum State {
        ALLOW,DENY,UNSET,SOME_CHANGED
    }

    private final Boolean defaultValue;
    private final StateFlag[] flags;
    private final String configId;

    GuardFlags(String configId, Boolean defaultValue, StateFlag... flags) {
        this.configId = configId;
        this.defaultValue = defaultValue;
        this.flags = flags;
    }

    public static State getState(ProtectedRegion pr, GuardFlags flag) {
        List<StateFlag.State> states = new ArrayList<>();
        for (StateFlag fl : flag.getFlags()) {
            states.add(pr.getFlag(fl));
        }

        if (Collections3.allValueEquals(states, StateFlag.State.ALLOW)) {
            return State.ALLOW;
        }
        else if (Collections3.allValueEquals(states, StateFlag.State.DENY)) {
            return State.DENY;
        }
        else if (Collections3.allValueEquals(states, null)) {
            return State.UNSET;
        }
        else return State.SOME_CHANGED;
    }

    public static void initRegionFlags(ProtectedRegion region) {
        for (GuardFlags gf : values()) {
            StateFlag.State state = PGUtil.boolToState(gf.getDefaultValue());
            for (StateFlag f : gf.getFlags()) {
                region.setFlag(f, state);
                region.setFlag(f.getRegionGroupFlag(), gf == PVP ? null : RegionGroup.NON_MEMBERS);
            }
        }
    }

    public static void initRegionFlag(ProtectedRegion region, GuardFlags flag) {
        StateFlag.State state = PGUtil.boolToState(flag.getDefaultValue());
        for (StateFlag f : flag.getFlags()) {
            region.setFlag(f, state);
            region.setFlag(f.getRegionGroupFlag(), flag == PVP ? null : RegionGroup.NON_MEMBERS);
        }
    }

}
