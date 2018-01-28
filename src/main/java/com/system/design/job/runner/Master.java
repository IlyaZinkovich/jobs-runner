package com.system.design.job.runner;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.Router;
import java.util.Random;
import scala.concurrent.duration.Duration;

public class Master extends AbstractActor {

  private Router router;

  private long rate = 1000;
  private Cancellable tick;

  private Cancellable schedule() {
    return getContext().system().scheduler().schedule(Duration.Zero(),
        Duration.create(rate, MILLISECONDS),
        getSelf(), "tick", getContext().dispatcher(), getSelf());
  }

  static Props props(Router router) {
    return Props.create(Master.class, () -> new Master(router));
  }

  public Master(Router router) {
    this.router = router;
  }

  @Override
  public Receive createReceive() {
    return new ReceiveBuilder()
        .matchEquals("tick", message -> {
          if (router.routees().nonEmpty()) {
            router.route("tick", getSelf());
          }
        })
        .matchEquals("worker", message -> {
          final ActorRef worker = getContext()
              .actorOf(Worker.props(), format("worker-%s", new Random().nextInt(100)));
          router = router.addRoutee(new ActorRefRoutee(worker));
        })
        .matchEquals("start", message -> tick = schedule())
        .matchEquals("stop", message -> tick.cancel())
        .matchEquals("resume", message -> tick = schedule())
        .matchEquals("throttle", message -> {
          tick.cancel();
          rate = rate * 2;
          tick = schedule();
        })
        .build();
  }
}
