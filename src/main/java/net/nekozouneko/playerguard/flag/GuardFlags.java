package net.nekozouneko.playerguard.flag;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import net.nekozouneko.commons.lang.collect.Collections3;
import net.nekozouneko.playerguard.PGUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum GuardFlags {

    BREAK(false, Flags.BLOCK_BREAK),
    PLACE(false, Flags.BLOCK_PLACE),
    INTERACT(false, Flags.INTERACT, Flags.CHEST_ACCESS),
    PVP(false, Flags.PVP),
    ENTITY_DAMAGE(true, Flags.DAMAGE_ANIMALS);

    public enum State {
        ALLOW,DENY,SOME_CHANGED
    }

    private final Boolean defaultValue;
    private final StateFlag[] flags;

    GuardFlags(boolean defaultValue, StateFlag... flags) {
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
        else return State.SOME_CHANGED;
    }

    public static void initRegionFlags(ProtectedRegion region) {
        for (GuardFlags gf : values()) {
            StateFlag.State state = PGUtil.boolToState(gf.getDefaultValue());
            for (StateFlag f : gf.getFlags()) {
                region.setFlag(f, state);
            }
        }
    }

}
