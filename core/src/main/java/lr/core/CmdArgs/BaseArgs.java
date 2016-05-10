package lr.core.CmdArgs;

import com.beust.jcommander.Parameter;

/**
 * Created by luca on 10/03/16.
 */
public class BaseArgs {

    @Parameter(names = "-r", description = "define the number of replication")
    private int replica = 1;

    public int getReplica() {
        return replica;
    }

    @Parameter(names = "-help", help = true)
    private boolean help = false;

    public boolean isHelp() {
        return help;
    }
}
