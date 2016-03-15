package lr.app;

import com.beust.jcommander.Parameter;
import lr.core.CmdArgs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luca on 11/03/16.
 */
public class CmdArgsApp extends CmdArgs{

    @Parameter(names = "-N", description = "number of servers")
    private int N = 5;

    @Parameter(names = "-n", description = "number of seeds servers")
    private int seeds = 3;

    @Parameter(names = "-gport", description = "the port for the gossip protocol")
    private int portG = 2000;

    @Parameter(names = "-mport", description = "the port for key management")
    private int portM = 2001;

    public int getSeeds() {
        return seeds;
    }

    public int getN() {
        return N;
    }

    public int getPortG() {
        return portG;
    }

    public int getPortM() {
        return portM;
    }
}
