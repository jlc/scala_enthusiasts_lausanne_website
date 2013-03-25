package controllers

import scala.collection.{mutable, immutable}
import scala.concurrent._
import scala.concurrent.duration._

import akka.actor.{Actor, ActorRef, Props}

import play.Logger
import play.api.Play.current
import play.api.libs.{EventSource, Comet}
import play.api.libs.json.{Json, JsValue}
import play.api.libs.iteratee.{Concurrent, Enumeratee, Enumerator, Iteratee}
import play.api.libs.concurrent.Akka

object ClientMessages {

  abstract class Message {
    def id: Option[String] = None
    def eventName: String
    def json: JsValue
  }

  case class NewClient(info: String) extends Message {
    val eventName = "newclient"
    def json =
      Json.obj(
        "info" -> info
      )
  }

  /*
   * These implicits are used by EventSource() in order to format a message
   * NOTE: even when using a message of type JsValue, the default eventNameExtractor (in EventSource.scala)
   * does not extract the eventName.
   */
  implicit def encoder[T <: Message] = Comet.CometMessage[T](msg => msg.json.toString())
  implicit def eventNameExtractor[T <: Message] = EventSource.EventNameExtractor[T](msg => Some(msg.eventName))
  implicit def eventIdExtractor[T <: Message] = EventSource.EventIdExtractor[T](_.id)
}

object ClientsActorMessages {
  case class PageView(clientInfo: String)

  case class RegisterClient()
}

class ClientsActor extends Actor {
  import ClientMessages._
  import ClientsActorMessages._

  val (newClientEnumerator, newClientChannel) = Concurrent.broadcast[NewClient]

  var lastInfo = ""

  def receive = {

    case PageView(clientInfo) => {
      lastInfo = clientInfo

      newClientChannel.push(NewClient(clientInfo))
    }

    case RegisterClient() => {
      sender ! newClientEnumerator
    }

  }

}

object ClientsActor {
  lazy val actor = Akka.system.actorOf(Props(new ClientsActor), "clients-actor")

  def apply() = actor
}
