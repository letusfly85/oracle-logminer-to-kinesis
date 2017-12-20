package io.wonder.soft

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import io.wonder.soft.actor.{AuditTraceActor, KinesisActor}

import scala.concurrent.ExecutionContextExecutor

trait AuditTraceService {

  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val auditTraceActor: ActorRef
  val kinesisActor: ActorRef

  implicit val timeout = Timeout(5000, TimeUnit.MILLISECONDS)

  def logger: LoggingAdapter

  val routes =
    path("v1" / "status") {
      (get) {
        logger.info("/v1/status")

        complete("ok")
      }
    }
}

object AuditTraceService extends App with AuditTraceService {
  override implicit val system: ActorSystem  = ActorSystem(getClass.getPackage.getName)
  override implicit val executor: ExecutionContextExecutor = system.dispatcher
  override implicit val materializer: Materializer = ActorMaterializer()

  override val logger = Logging(system, getClass)

  override val auditTraceActor: ActorRef = system.actorOf(Props(classOf[AuditTraceActor]), "audit-trace-actor")
  override val kinesisActor: ActorRef = system.actorOf(Props(classOf[KinesisActor]), "kinesis-actor")

  Http().bindAndHandle(routes, "0.0.0.0", 8080)
}
