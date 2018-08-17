package com.shuman.transfers;

import com.shuman.transfers.controller.HelloWorld;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ResourceLoader extends Application{

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // register root resource
        classes.add(HelloWorld.class);
        return classes;
    }
}