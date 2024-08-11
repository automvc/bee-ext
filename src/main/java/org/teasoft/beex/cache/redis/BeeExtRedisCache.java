/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.teasoft.beex.cache.redis;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.teasoft.bee.osql.Serializer;
import org.teasoft.honey.osql.core.DefaultBeeExtCache;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.JdkSerializer;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * 扩展的Redis缓存.Bee Ext Redis Cache.
 * @author Kingstar
 * @since  1.11
 */
public class BeeExtRedisCache extends DefaultBeeExtCache {

	private JedisPool jedisPool;
	private Jedis jedis;
	private Serializer serializer;
	
	private static final long serialVersionUID = 1596710362359L;

	private static final String FIELD = "Bee";
	private static final byte[] FIELD_BYTES = FIELD.getBytes();
	
	private final ThreadLocalRandom random=ThreadLocalRandom.current();
	private int max=0;
	private int min=0;
	private int baseNum=0;

	public BeeExtRedisCache() {
		initRedis();
	}
	
	private int getTimeOUt() {
		HoneyConfig config = HoneyConfig.getHoneyConfig();
		int levelTwoTimeout = config.cache_levelTwoTimeout;
		Double randTimeoutRate = config.cache_randTimeoutRate;
		boolean randTimeoutAutoRefresh = config.cache_randTimeoutAutoRefresh;
		if (randTimeoutRate != null && randTimeoutRate > 0 && randTimeoutRate < 1) {
			if (randTimeoutAutoRefresh || baseNum == 0) {
				max = (int) (levelTwoTimeout * (1 + randTimeoutRate));
				min = (int) (levelTwoTimeout * (1 - randTimeoutRate));
				baseNum = max - min + 1;
			}
			return random.nextInt(baseNum) + min;
		}
		return levelTwoTimeout;
	}

	public void initRedis() {
		HoneyConfig config = HoneyConfig.getHoneyConfig();

		String host = config.cacheRedis_host;
		Integer port = config.cacheRedis_port;
		String p = config.cacheRedis_password;
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
				connectionTimeout, soTimeout, p, database, clientName, ssl);
	}
	
	@Override
	public void addInExtCache(String key, Object result) {
		Jedis jedis1 = getJedis();
		try {
			jedis1.hset(key.getBytes(), FIELD_BYTES, getSerializer().serialize(result));
			jedis1.expire(key.getBytes(), getTimeOUt());
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
		}
		finally {
			jedis1.close();
		}
	}

	@Override
	public Object getInExtCache(String key) {
		Object obj = null;
		Jedis jedis0 = getJedis();
		try {
			obj = getSerializer().unserialize(jedis0.hget(key.getBytes(), FIELD_BYTES));
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
		} finally {
			jedis0.close();
		}
		return obj;
	}

	@Override
	public void clearInExtCache(String key) {
		Jedis jedis2 = getJedis();
		try {
			jedis2.hdel(key, FIELD);
		} finally {
			jedis2.close();
		}
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
