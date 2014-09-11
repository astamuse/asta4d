package com.astamuse.asta4d.sample.util.persondb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.astamuse.asta4d.sample.handler.form.PersonForm;

public class PersonDbManager {
    private static PersonDbManager instance = new PersonDbManager();

    private PersonDbManager() {
        initPersonList();
    }

    public static PersonDbManager instance() {
        return instance;
    }

    private AtomicInteger idGenerator = new AtomicInteger(1);

    private Set<Person> personList = new HashSet<>();

    private void initPersonList() {
        //@formatter:off
        Object[][] data = new Object[][]{
            {idGenerator.getAndIncrement(), "Alice", 20, PersonForm.SEX.Female, PersonForm.BloodType.AB, 
                new PersonForm.Language[]{PersonForm.Language.Chinese, PersonForm.Language.English}},
                
            {idGenerator.getAndIncrement(), "Bob", 25, PersonForm.SEX.Male, PersonForm.BloodType.O, 
                new PersonForm.Language[]{PersonForm.Language.Japanese, PersonForm.Language.English}},
            
        };
        //@formatter:on
        for (Object[] d : data) {
            int idx = 0;
            Person p = new Person();
            p.setId((Integer) d[idx++]);
            p.setName((String) d[idx++]);
            p.setAge((Integer) d[idx++]);
            p.setSex((Person.SEX) d[idx++]);
            p.setBloodType((Person.BloodType) d[idx++]);
            p.setLanguage((Person.Language[]) d[idx++]);
            personList.add(p);
        }
    }

    public synchronized List<Person> findAll() {
        return new ArrayList<>(personList);
    }

    public synchronized Person find(final int id) {
        Person p = (Person) CollectionUtils.find(personList, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((Person) object).getId() == id;
            }
        });
        try {
            return p.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void add(Person person) {
        if (personList.size() >= 10) {
            throw new RuntimeException("too many data");
        }
        person.setId(idGenerator.getAndIncrement());
        try {
            personList.add(person.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void update(final Person person) {
        remove(person);
        try {
            personList.add(person.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void remove(final Person person) {
        Person existingPerson = find(person.getId());
        if (existingPerson == null) {
            throw new IllegalArgumentException("person does not exist");
        }
        personList.remove(existingPerson);

    }
}
