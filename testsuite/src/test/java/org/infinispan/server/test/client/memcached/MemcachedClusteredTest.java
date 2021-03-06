package org.infinispan.server.test.client.memcached;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.arquillian.core.RemoteInfinispanServer;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

/**
 * Tests for the Memcached client. Clustered test cases.
 *
 * @author Martin Gencur
 */
@RunWith(Arquillian.class)
public class MemcachedClusteredTest extends AbstractClusteredMemcachedTest {

    @InfinispanResource("container1")
    RemoteInfinispanServer server1;

    @InfinispanResource("container2")
    RemoteInfinispanServer server2;

    @Override
    protected List<RemoteInfinispanServer> getServers() {
        List<RemoteInfinispanServer> servers = new ArrayList<RemoteInfinispanServer>();
        servers.add(server1);
        servers.add(server2);
        return Collections.unmodifiableList(servers);
    }

}
