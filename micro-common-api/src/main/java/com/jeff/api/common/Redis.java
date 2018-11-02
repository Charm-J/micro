package com.jeff.api.common;

import com.jeff.api.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
public class Redis {
    private final Logger logger = LoggerFactory.getLogger(Redis.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("redis 设置超时 key = {} time = {} error={} ", key, time, e);
            return false;
        }
    }

    /**
     * 指定缓存失效时间（带时间单位）
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public boolean expire(String key, final long timeout, final TimeUnit unit) {
        try {
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, unit);
            }
            return true;
        } catch (Exception e) {
            logger.error("redis 设置超时 key = {} timeout = {} error={} ", key, timeout, e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        long time = -1L;
        try {
            time = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("redis 获取过期时间 key = {}  ", key);
        }
        return time;

    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("redis 判断key是否存在 key = {} error={} ", key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        String v = null;
        try {
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            redisTemplate.opsForValue().set(key, v);
            return true;
        } catch (Exception e) {
            logger.error("redis set error key = {}  value = {} error={} ", key, v);
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            String v;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            if (time > 0) {
                redisTemplate.opsForValue().set(key, v, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            String v;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            redisTemplate.opsForHash().put(key, item, v);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            String v;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            redisTemplate.opsForHash().put(key, item, v);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * hash 自增
     *
     * @param key   键
     * @param value 值
     * @param d     增长步长
     * @return 成功个数
     */
    public long hInc(String key, Object value, long d) {
        try {
            String v;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            return redisTemplate.opsForHash().increment(key, v, d);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===============================query=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<String> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 右边移除元素
     *
     * @param key
     * @return
     */
    public String rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存 从列头插入元素
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            // redisTemplate.opsForList().rightPush(key, value);
            String v = null;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            redisTemplate.opsForList().leftPush(key, v);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存 从列尾插入元素
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lrSet(String key, Object value) {
        try {
            String v = null;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            redisTemplate.opsForList().rightPush(key, v);
            // redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            String v = null;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            redisTemplate.opsForList().set(key, index, v);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取List列表
     *
     * @param key
     * @param start long，开始索引
     * @param end   long， 结束索引
     * @return List<String>
     */
    public List<String> lrange(String key, long start, long end) {
        try {
            List<String> list = redisTemplate.opsForList().range(key, start, end - 1);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // ----------------------------------zset---------------------------------------------//

    /**
     * 将数据放入zset缓存
     *
     * @param key   键
     * @param value 值 可以是多个
     * @return 成功个数
     */
    public Boolean szSet(String key, Object value, Double score) {
        try {
            String v;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            return redisTemplate.opsForZSet().add(key, v, score);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 分数按高到低
     *
     * @param key 键
     * @param l1  值 可以是多个
     * @return 成功个数
     */
    public Set<String> szReverse(String key, Long l1, Long l2) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, l1, l2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查看值的索引
     *
     * @param key
     * @param o
     * @return
     */
    public Long szSetRank(String key, Object o) {
        return redisTemplate.opsForZSet().rank(key, o);
    }

    /**
     * 查看值的索引（逆序）
     *
     * @param key
     * @param o
     * @return
     */
    public Long szSetReverseRank(String key, Object o) {
        return redisTemplate.opsForZSet().reverseRank(key, o);
    }

    /**
     * 分数按高到低-带分数
     *
     * @param key 键
     * @param l1  值 可以是多个
     * @return 成功个数
     */
    public Set<TypedTuple<String>> szReverseScores(String key, Long l1, Long l2) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, l1, l2);
    }

    /**
     * 分数按低到高-带分数
     *
     * @param key 键
     * @param l1  值 可以是多个
     * @return 成功个数
     */
    public Set<TypedTuple<String>> szScores(String key, Long l1, Long l2) {
        return redisTemplate.opsForZSet().rangeWithScores(key, l1, l2);
    }


    /**
     * 取得大小
     *
     * @param key 键
     * @return 成功个数
     */
    public Long szGetSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 自增
     *
     * @param key   键
     * @param value 值 可以是多个
     * @return 成功个数
     */
    public Double szInc(String key, Object value, Double d) {
        try {
            String v = null;
            if (value instanceof String) {
                v = (String) value;
            } else {
                v = JsonUtil.serialize(value);
            }
            return redisTemplate.opsForZSet().incrementScore(key, v, d);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }


    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Set<Object> hallKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }


    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, String value) {
        try {

            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 删除
     *
     * @param key 键
     * @param v   值 可以是多个
     * @return 成功个数
     */
    public Long szRemove(String key, String v) {
        try {
            return redisTemplate.opsForZSet().remove(key, v);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

}
