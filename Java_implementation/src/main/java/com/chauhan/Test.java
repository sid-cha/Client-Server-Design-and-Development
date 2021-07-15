package com.chauhan;

import com.chauhan.siddharth.service.MyResource;

public class Test {
    public static void main(String[] args) {
        MyResource resource = new MyResource();

        //System.out.println(resource.insertDepartment("sc2816", "CS", "10", "Rochester").getEntity());
        System.out.println(resource.getAllDepartment("sc2816").getEntity());
        //System.out.println(resource.insertDepartment("sc2816", "Food", "F10", "denmark").getEntity());
    }
}
