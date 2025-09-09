package com.tsAdmin.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransportAssignment<K,V> 
{
    private final List<Pair<K,V>> entries = new ArrayList<>();

    public TransportAssignment(){}

    /** 副本构造(浅拷贝)
     * 对于不可变类型安全
     * 对于可变类型无用
     */
    public TransportAssignment(List<Pair<K,V>> entries)
    {
        for(Pair<K,V> pair:entries)
        {
            this.entries.add(new Pair<>(pair.key, pair.value));
        }
    }

    public void put(K key,V value)
    {
        entries.add(new Pair<>(key,value));
    }

    public void removeByKey(K key)
    {
        for(Pair<K,V> pair:entries)
        {
            if(pair.key==key){ entries.remove(pair);break; } 
        }
    }
    
    public  V getValueByKey(K key)
    {
        for(Pair<K,V> pair:entries)
        {
            if(pair.key==key){ return pair.value; }
        }
        return null;//返回null说明逻辑出错
    }

    /** 返回一个只读List */
    public List<Pair<K,V>> entries()
    {
        return Collections.unmodifiableList(entries);
    }

    public static class Pair<K,V>
    {
        public final K key;
        public final V value;

        public Pair(K key,V value)
        {
            this.key=key;this.value=value;
        }
    }
}
