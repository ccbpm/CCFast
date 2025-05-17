package bp.difference.redis;

import bp.tools.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ReadMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.config.Config;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnProperty(name="spring.data.redis.repositories.enabled", havingValue="true", matchIfMissing=false)
public class RedisConfig
{
    @Autowired
    Environment env;


    private int timeout = 2000;
    private int scanInterval = 60000;
    private static String ADDRESS_PREFIX = "redis://";

    /**
     * 单机模式
     */
    @Bean
    public RedissonClient initBean() {
        String host =  env.getProperty("spring.redis.host");
        String port =  env.getProperty("spring.redis.port");
        String password =  env.getProperty("spring.redis.password");
        int database = Integer.parseInt(env.getProperty("spring.redis.database"));
        String sentinel = env.getProperty("spring.redis.sentinel.nodes");
        String cluster = env.getProperty("spring.redis.cluster");
        String master = env.getProperty("spring.redis.master");
        // 哨兵模式
        if (StringUtils.isNotBlank(sentinel)) {
            bp.da.Log.DebugWriteInfo("redis is sentinel mode");
            return redissonSentinel(sentinel,master,password);
        }
        // 集群模式
        if (StringUtils.isNotBlank(cluster)) {
            bp.da.Log.DebugWriteInfo("redis is cluster mode");
            return redissonCluster(cluster,password);
        }
        // 单机模式
        if (StringUtils.isNotBlank(host)) {
            bp.da.Log.DebugWriteInfo("redis is single mode");
            return redissonSingle(host,port,password,database);
        }

        bp.da.Log.DebugWriteInfo("redisson config can not support this redis mode");
        return null;
    }

    /**
     * 单机模式
     */
    private RedissonClient redissonSingle(String host,String port,String password,int database) {
        Config config = new Config();
        String address = ADDRESS_PREFIX+host+":"+port;
        //设置
        config
                //这是用的集群server
                .useSingleServer()
                .setAddress(address)
                .setTimeout(timeout)
                .setDatabase(database)
                .setPassword(password);
        return Redisson.create(config);
    }


    /**
     * 哨兵模式
     */
   private RedissonClient redissonSentinel(String sentinel,String masterName,String password ) {

        String[] nodes = sentinel.split(",");
        //redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
        for(int i=0;i<nodes.length;i++){
            nodes[i] = ADDRESS_PREFIX+nodes[i];
        }
        Config config = new Config();
        //设置
        config.useSentinelServers()
                .setMasterName(masterName)
                .setPassword(password)
                .setTimeout(timeout)
                .addSentinelAddress(nodes)
                // 在Redisson启动期间启用sentinels列表检查,默认为true,这里我们设置为false,不检查
                .setCheckSentinelsList(false);
        return Redisson.create(config);
    }

    /**
     * 集群模式
     */
    private RedissonClient redissonCluster(String cluster,String password) {
        String[] nodes = cluster.split(",");
        //redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
        for(int i=0;i<nodes.length;i++){
            nodes[i] = ADDRESS_PREFIX+nodes[i];
        }
        Config config = new Config();
        //设置
        config.useClusterServers()
                //设置集群状态扫描时间
                .setScanInterval(scanInterval)
                .addNodeAddress(nodes)
                .setPassword(password)
                .setReadMode(ReadMode.MASTER);;
        return Redisson.create(config);
    }
}
