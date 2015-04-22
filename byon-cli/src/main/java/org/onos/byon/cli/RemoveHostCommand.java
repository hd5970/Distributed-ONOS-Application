package org.onos.byon.cli;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onos.byon.NetworkService;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.HostId;

/**
 * Created by hd5970 on 4/22/15.
 */
@Command(scope = "byon", name = "remove-host", description = "Remove a host from a network")
public class RemoveHostCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "network", description = "Network name",
            required = true, multiValued = false)
    String network = null;

    @Argument(index = 1, name = "hostId", description = "Host Id",
            required = true, multiValued = false)
    String hostId = null;


    @Override
    protected void execute() {
        NetworkService networkService = get(NetworkService.class);

        networkService.removeHost(network, HostId.hostId(hostId));

        print("Removed host %s from %s", hostId, network);

    }
}