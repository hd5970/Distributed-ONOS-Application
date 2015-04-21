package org.onos.byon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.net.HostId;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.Intent;
import org.onosproject.store.AbstractStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hd5970 on 4/19/15.
 */
@Component(immediate = true, enabled = true)
@Service
public class SimpleNetworkStore
        extends AbstractStore<NetworkEvent, NetworkStoreDelegate>
        implements NetworkStore{
    private static Logger log = LoggerFactory.getLogger(SimpleNetworkStore.class);
    private final Map<String, Set<HostId>> networks = Maps.newHashMap();
    private final Map<String, Set<Intent>> intentsPerNet = Maps.newHashMap();
    @Override
    public void putNetwork(String network) {
        intentsPerNet.putIfAbsent(network, Sets.<Intent>newHashSet());
        //google 的Sets 类很有意思
        if (networks.putIfAbsent(network, Sets.<HostId>newHashSet()) == null){
            notifyDelegate(new NetworkEvent(NetworkEvent.Type.NETWORK_ADDED,network));
        }
    }

    @Override
    public void removeNetwork(String network) {
        if(intentsPerNet.remove(network) != null){
            notifyDelegate(new NetworkEvent(NetworkEvent.Type.NETWORK_REMOVED,network));
        }
        networks.remove(network);
    }

    @Override
    public Set<String> getNetworks() {
        return ImmutableSet.copyOf(networks.keySet());

    }

    @Override
    public Set<HostId> addHost(String network, HostId hostId) {
        Set<HostId> hosts = checkNotNull(networks.get(network),"Please create the network first");
        boolean added = hosts.add(hostId);
        if (added){
            notifyDelegate(new NetworkEvent(NetworkEvent.Type.NETWORK_UPDATED, network));
        }
        return added ? ImmutableSet.copyOf(hosts) : Collections.emptySet();
    }

    @Override
    public void removeHost(String network, HostId hostId) {
        Set<HostId> hosts = checkNotNull(networks.get(network),"Please create the network first");
        if (hosts.remove(hostId)){
            notifyDelegate(new NetworkEvent(NetworkEvent.Type.NETWORK_UPDATED, network));
        }
    }

    @Override
    public Set<HostId> getHosts(String network) {
        Set<HostId> hosts = checkNotNull(networks.get(network),"Please create the network first");
        return ImmutableSet.copyOf(hosts);
    }

    @Override
    public void addIntents(String network, Set<Intent> intents) {
        intents.forEach(intent -> checkArgument(intent instanceof HostToHostIntent,
                "Intent should be a host to host intent."));
        intentsPerNet.get(network).addAll(intents);
    }

    @Override
    public Set<Intent> removeIntents(String network, HostId hostId) {
        Set<Intent> intents = checkNotNull(intentsPerNet.get(network).stream().map(intent -> (HostToHostIntent)intent)
        .filter(intent -> intent.one().equals(hostId) || intent.two().equals(hostId)).collect(Collectors.toSet()));
        intentsPerNet.get(network).remove(intents);
        return intents;
    }

    @Override
    public Set<Intent> removeIntents(String network) {
        Collection<Intent> intents = checkNotNull(intentsPerNet.get(network));
        //get返回值为Variable，用Collection

        return ImmutableSet.copyOf(intents);

    }
}
