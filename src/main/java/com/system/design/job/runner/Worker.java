package com.system.design.job.runner;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class Worker extends AbstractActor {

  static Props props() {
    return Props.create(Worker.class, Worker::new);
  }

  @Override
  public Receive createReceive() {
    return ReceiveBuilder.create()
        .matchEquals("tick", message -> System.out.println(this.toString()))
        .build();
  }
}
