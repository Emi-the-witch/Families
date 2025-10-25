package modchecker;


import game.GAME;
import game.GameSpec;
import lombok.NoArgsConstructor;
import script.SCRIPT;
import snake2d.LOG;
import util.info.INFO;

/**
 * Entry point for the mod.
 * Contains some basic information about the mod.
 * Used to set up your mod.
 *
 * See {@link SCRIPT} for some documentation.
 */
@NoArgsConstructor
@SuppressWarnings("unused") // used by the game via reflection
public final class ModCheckScript implements SCRIPT {
    public static GameSpec ss;
    /**
     * This info will be displayed when starting a new game and choosing a script
     */
    private final INFO info = new INFO("Families", "What's your mom's name?");

    @Override
    public CharSequence name() {
        return info.name;
    }

    @Override
    public CharSequence desc() {
        return info.desc;
    }

    /**
     * Called before an actual game is started or loaded
     */
    @Override
    public void initBeforeGameCreated() {
        GAME.saver().onBeforeLoad(path -> {
            /// #!# Add the GameSpec of the game before it starts loading variables.
            ///  This allows me to check if mod was on the save file and ignore variables that are specific to this mod.
            ss = GameSpec.get(path);
            for (String mod : ss.mods) {
                LOG.ln(mod);
            }

        });
    }


    /**
     * @return whether mod shall be selectable when starting a new game
     */
    @Override
    public boolean isSelectable() {
        return false;
    }

    /**
     * @return whether mod shall be loaded into existing saves or not
     */
    @Override
    public boolean forceInit() {
        return true;
    }

    /**
     * This actually creates the "instance" of your script.
     */
    @Override
    public SCRIPT_INSTANCE createInstance() {
        return new ModCheckInstance();
    }
}