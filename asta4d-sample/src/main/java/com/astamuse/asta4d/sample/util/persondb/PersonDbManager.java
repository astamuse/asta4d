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

import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.sample.handler.form.PersonForm;

public class PersonDbManager extends AbstractDbManager<Person> {
    private static PersonDbManager instance = new PersonDbManager();

    private PersonDbManager() {
        super();
    }

    public static PersonDbManager instance() {
        return instance;
    }

    protected List<Person> initEntityList() {
        //@formatter:off
        Object[][] data = new Object[][]{
            {nextId(), "Alice", 20, PersonForm.SEX.Female, PersonForm.BloodType.AB, 
                new PersonForm.Language[]{PersonForm.Language.Chinese, PersonForm.Language.English}},
                
            {nextId(), "Bob", 25, PersonForm.SEX.Male, PersonForm.BloodType.O, 
                new PersonForm.Language[]{PersonForm.Language.Japanese, PersonForm.Language.English}},
            
        };
        //@formatter:on
        List<Person> list = new LinkedList<>();
        for (Object[] d : data) {
            int idx = 0;
            Person p = new Person();
            p.setId((Integer) d[idx++]);
            p.setName((String) d[idx++]);
            p.setAge((Integer) d[idx++]);
            p.setSex((Person.SEX) d[idx++]);
            p.setBloodType((Person.BloodType) d[idx++]);
            p.setLanguage((Person.Language[]) d[idx++]);
            list.add(p);
        }
        return list;
    }

}
