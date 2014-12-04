/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.sample.util.persondb;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.ObjectUtils;

import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public abstract class AbstractDbManager<T extends IdentifiableEntity> {

    private AtomicInteger idGenerator = new AtomicInteger();

    private Set<T> entityList = new HashSet<>();

    protected AbstractDbManager() {
        entityList.addAll(initEntityList());
    }

    protected abstract List<T> initEntityList();

    protected Integer nextId() {
        return idGenerator.incrementAndGet();
    }

    public synchronized List<T> findAll() {
        return new ArrayList<>(entityList);
    }

    @SuppressWarnings("unchecked")
    public synchronized List<T> find(final String field, final Object v) {
        List<T> list = new ArrayList<>(entityList);
        CollectionUtils.filter(list, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                try {
                    return ObjectUtils.equals(v, PropertyUtils.getProperty(object, field));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return ListConvertUtil.transform(list, new RowConvertor<T, T>() {
            @Override
            public T convert(int rowIndex, T obj) {
                try {
                    return (T) obj.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public synchronized T find(final int id) {
        T entity = (T) CollectionUtils.find(entityList, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((T) object).getId() == id;
            }
        });
        try {
            return (T) entity.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void add(T entity) {
        if (entityList.size() >= 10) {
            throw new RuntimeException("too many data");
        }
        entity.setId(nextId());
        try {
            entityList.add((T) entity.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void update(final T entity) {
        remove(entity);
        try {
            entityList.add((T) entity.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void remove(final T entity) {
        T existingEntity = find(entity.getId());
        if (existingEntity == null) {
            throw new IllegalArgumentException("entity does not exist:" + entity.getId());
        }
        entityList.remove(existingEntity);

    }
}
