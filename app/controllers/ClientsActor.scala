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

  case class UserAgentInfo(
    family: String, version: String, typeName: String, producer: String,
    osName: String, osVersion: String, location: String)
      extends Message {
    val eventName = "otheruseragentinfo"
    def json =
      Json.obj(
        "family" -> family, "version" -> version, "typeName" -> typeName, "producer" -> producer,
        "osName" -> osName, "osVersion" -> osVersion, "location" -> location
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
  case class PageViewedBy(clientInfo: ClientMessages.UserAgentInfo)

  case class RegisterClient()
}

class ClientsActor extends Actor {
  import ClientMessages._
  import ClientsActorMessages._

  val (newClientEnumerator, newClientChannel) = Concurrent.broadcast[UserAgentInfo]

  var lastInfo: Option[UserAgentInfo] = None

  def receive = {

    case PageViewedBy(uaInfo) => {
      lastInfo = Some(uaInfo)

      newClientChannel.push(uaInfo)
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
