package com.yao.netty.six;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import thrift.Person;
import thrift.PersonService;

public class ThriftClient {

    public static void main(String[] args) {
        TTransport transport = new TFramedTransport(new TSocket("localhost",8899),600);
        TProtocol protocol = new TCompactProtocol(transport);
        PersonService.Client client = new PersonService.Client(protocol);
        try{
            transport.open();
            Person person = client.getPersonByUsername("张三");

            System.out.println(person.getUsername());
            System.out.println(person.getAge());
            System.out.println(person.isMarried());
            System.out.println("=======================");


            Person person2 = new Person();
            person2.setUsername("lisi");
            person2.setAge(22);
            person.setMarried(false);
            client.addPerson(person);

        }catch (Exception ex){
            throw new RuntimeException(ex.getMessage(),ex);
        }finally {

        }
    }
}
