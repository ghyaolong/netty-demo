package com.yao.netty.six;

import org.apache.thrift.TException;
import thrift.DataException;
import thrift.Person;
import thrift.PersonService;

public class PersonServiceImpl implements PersonService.Iface {
    @Override
    public Person getPersonByUsername(String username) throws DataException, TException {
        System.out.println("Got Client from :"+username);


        Person person = new Person();
        person.setAge(20);
        person.setMarried(true);
        person.setUsername("张三");
        return person;
    }

    @Override
    public void addPerson(Person person) throws DataException, TException {
        System.out.println("Got Client from :"+ person);
        System.out.println(person.getAge());
        System.out.println(person.getUsername());
        System.out.println(person.isMarried());
    }
}
