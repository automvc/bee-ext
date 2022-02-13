/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.cache.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.teasoft.bee.osql.Serializer;
import org.teasoft.honey.osql.core.DefaultBeeExtCache;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.JdkSerializer;
import org.teasoft.honey.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * @author Kingstar
 * @since  1.11
 */
public class BeeExtRedisCache extends DefaultBeeExtCache {

	private JedisPool jedisPool;
	private Jedis jedis;
	private Serializer serializer;

	private static final String field = "Bee";
	private static final byte[] fieldBytes = field.getBytes();
	
	private static final int timeout=HoneyConfig.getHoneyConfig().cache_levelTwoTimeout;

	public BeeExtRedisCache() {
		initRedis();
	}

	public void initRedis() {
		HoneyConfig config = HoneyConfig.getHoneyConfig();

		String host = config.cacheRedis_host;
		Integer port = config.cacheRedis_port;
		String password = config.cacheRedis_password;
		Integer connectionTimeout = config.cacheRedis_connectionTimeout;
		Integer soTimeout = config.cacheRedis_soTimeout;
		Integer database = config.cacheRedis_database;
		String clientName = config.cacheRedis_clientName;
		boolean ssl = config.cacheRedis_ssl;

		if (StringUtils.isBlank(host)) {
			host = Protocol.DEFAULT_HOST;
		}
		if (port == null) {
			port = Protocol.DEFAULT_PORT;
		}

		if (connectionTimeout == null) {
			connectionTimeout = Protocol.DEFAULT_TIMEOUT;
		}
		if (soTimeout == null) {
			soTimeout = Protocol.DEFAULT_TIMEOUT;
		}

		if (database == null) {
			database = Protocol.DEFAULT_DATABASE;
		}

		this.jedisPool = new JedisPool(new GenericObjectPoolConfig<Jedis>(), host, port,
				connectionTimeout, soTimeout, password, database, clientName, ssl);
	}

	@Override
	public Object getInExtCache(String key) {
		return getSerializer().unserialize(getJedis().hget(key.toString().getBytes(),fieldBytes));
	}

	@Override
	public void addInExtCache(String key, Object result) {
		Jedis jedis=getJedis();
		jedis.hset(key.toString().getBytes(),fieldBytes,getSerializer().serialize(result));
		jedis.expire(key.toString().getBytes(), timeout);
	}

	@Override
	public void clearInExtCache(String key) {
		getJedis().hdel(key,field);
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public Jedis getJedis() {
		JedisPool pool = getJedisPool();
		if (pool != null) return pool.getResource();
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	public Serializer getSerializer() {
		if (serializer == null) return new JdkSerializer();
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

}
