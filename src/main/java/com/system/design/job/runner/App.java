package com.system.design.job.runner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Router;

public class App {

  public static void main(String[] args) throws InterruptedException {
    final ActorSystem system = ActorSystem.create("jobs");
    Router router = new Router(new RoundRobinRoutingLogic());
    final ActorRef master = system.actorOf(Master.props(router), "master");
    router.addRoutee(master);
    master.tell("worker", ActorRef.noSender());
    master.tell("worker", ActorRef.noSender());
    master.tell("start", ActorRef.noSender());
    Thread.sleep(3000);
    master.tell("throttle", ActorRef.noSender());
    Thread.sleep(3000);
    master.tell("stop", ActorRef.noSender());
    Thread.sleep(3000);
    master.tell("resume", ActorRef.noSender());
    Thread.sleep(3000);
    master.tell("throttle", ActorRef.noSender());
    master.tell("stop", ActorRef.noSender());
  }
}
