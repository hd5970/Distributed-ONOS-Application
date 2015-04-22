package org.onos.byon.cli;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onos.byon.NetworkService;
import org.onosproject.cli.AbstractShellCommand;

/**
 * Created by hd5970 on 4/22/15.
 */
@Command(scope = "byon", name = "remove-network", description = "Remove a network")
public class RemoveNetwork extends AbstractShellCommand {

    @Argument(index = 0, name = "network", description = "Network name",
            required = true, multiValued = false)
    String network = null;




    @Override
    protected void execute() {
        NetworkService networkService = get(NetworkService.class);

        networkService.deleteNetwork(network);

        print("Deleted network %s", network);

    }
}

