package org.infinispan.server.test.configs;

import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.arquillian.core.RemoteInfinispanServer;
import org.infinispan.arquillian.model.RemoteInfinispanCache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Test example-configuration file clustered-xsite.xml.
 * Create 2 clusters (=sites), LON (node0, node1) and NYC (node2), and check that data put to LON/node0 is backupped
 * in NYC and replicated in LON/node1. SFO site is not used.
 *
 * @author <a href="mailto:jmarkos@redhat.com">Jakub Markos</a>
 */
@RunWith(Arquillian.class)
public class XsiteConfigExampleTest {

    final String DEFAULT_CACHE_NAME = "default";
    final String CACHE_MANAGER_NAME = "clustered";

    final String CONTAINER1 = "container1";
    final String CONTAINER2 = "container2";
    final String CONTAINER3 = "container3";

    @InfinispanResource(CONTAINER1)
    RemoteInfinispanServer server1;

    @InfinispanResource(CONTAINER2)
    RemoteInfinispanServer server2;

    @InfinispanResource(CONTAINER3)
    RemoteInfinispanServer server3;

    RemoteCacheManager rcm1;
    RemoteCacheManager rcm2;
    RemoteCacheManager rcm3;

    @Before
    public void setUp() {
        rcm1 = new RemoteCacheManager(new ConfigurationBuilder().addServer()
                .host(server1.getHotrodEndpoint().getInetAddress().getHostName())
                .port(server1.getHotrodEndpoint().getPort())
                .build());
        rcm2 = new RemoteCacheManager(new ConfigurationBuilder().addServer()
                .host(server2.getHotrodEndpoint().getInetAddress().getHostName())
                .port(server2.getHotrodEndpoint().getPort())
                .build());
        rcm3 = new RemoteCacheManager(new ConfigurationBuilder().addServer()
                .host(server3.getHotrodEndpoint().getInetAddress().getHostName())
                .port(server3.getHotrodEndpoint().getPort())
                .build());
    }

    @Test
    public void testXsiteConfig() throws Exception {
        RemoteInfinispanCache ric1 = server1.getCacheManager(CACHE_MANAGER_NAME).getCache(DEFAULT_CACHE_NAME);
        RemoteInfinispanCache ric2 = server2.getCacheManager(CACHE_MANAGER_NAME).getCache(DEFAULT_CACHE_NAME);
        RemoteInfinispanCache ric3 = server3.getCacheManager(CACHE_MANAGER_NAME).getCache(DEFAULT_CACHE_NAME);
        RemoteCache<String, String> rc1 = rcm1.getCache(DEFAULT_CACHE_NAME);
        RemoteCache<String, String> rc2 = rcm2.getCache(DEFAULT_CACHE_NAME);
        RemoteCache<String, String> rc3 = rcm3.getCache(DEFAULT_CACHE_NAME);
        assertEquals(0, ric1.getNumberOfEntries());
        assertEquals(0, ric2.getNumberOfEntries());

        assertEquals(2, server1.getCacheManager(CACHE_MANAGER_NAME).getClusterSize());
        assertEquals(2, server2.getCacheManager(CACHE_MANAGER_NAME).getClusterSize());
        assertEquals(1, server3.getCacheManager(CACHE_MANAGER_NAME).getClusterSize());

        rc1.put("k1", "v1");
        rc1.put("k2", "v2");
        assertEquals(2, ric1.getNumberOfEntries());
        assertEquals(2, ric2.getNumberOfEntries());
        assertEquals(2, ric3.getNumberOfEntries());

        assertEquals(rc1.get("k1"), "v1");
        assertEquals(rc2.get("k1"), "v1");
        assertEquals(rc3.get("k1"), "v1");
        assertEquals(rc1.get("k2"), "v2");
        assertEquals(rc2.get("k2"), "v2");
        assertEquals(rc3.get("k2"), "v2");
    }
}
