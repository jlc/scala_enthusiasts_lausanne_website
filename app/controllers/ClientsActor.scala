package controllers

import scala.collection.{mutable, immutable}
import scala.concurrent.duration._

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}

import play.Logger
import play.api.Play.current
import play.api.libs.{EventSource, Comet}
import play.api.libs.json.{Json, JsValue}
import play.api.libs.iteratee.{Concurrent, Enumeratee, Enumerator, Iteratee, Input}
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
    val eventName = "otherUserAgentInfo"
    def json =
      Json.obj(
        "family" -> family, "version" -> version, "typeName" -> typeName, "producer" -> producer,
        "osName" -> osName, "osVersion" -> osVersion, "location" -> location
      )
  }

  case class IndividualMessage(msg: String) extends Message {
    val eventName = "individualMessage"
    def json = Json.obj("message" -> msg)
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

  case class JoinUAInfoBroadcast()

  case class RegisterClient()

  case class DeregisterClient(uuid: UUID)
}

class ClientsActor extends Actor {
  import ClientMessages._
  import ClientsActorMessages._

  val (uaInfoEnumerator, uaInfoChannel) = Concurrent.broadcast[UserAgentInfo]

  var lastInfo: Option[UserAgentInfo] = None

  val clients = new mutable.HashMap[UUID, Concurrent.Channel[IndividualMessage]]

  def receive = {

    case PageViewedBy(uaInfo) => {
      lastInfo = Some(uaInfo)

      uaInfoChannel.push(uaInfo)

      // Test unicast connection with clients
      clients.map { case (uuid, channel) =>
          channel.push(IndividualMessage("Producer: " + uaInfo.producer + ", type: " + uaInfo.typeName ))
      }
    }

    case JoinUAInfoBroadcast() => {
      sender ! uaInfoEnumerator
    }

    case RegisterClient() => {
      // NOTE: Experiment here a unicast connection with clients

      val uuid = UUID.randomUUID()

      def onStart(channel: Concurrent.Channel[IndividualMessage]) = {
        Logger.debug("ClientsActor.receive: RegisterClient: onStart: " + uuid)
        clients += (uuid -> channel)
      }

      def onComplete = {
        Logger.debug("ClientsActor.receive: RegisterClient: onComplete: " + uuid)
        self ! DeregisterClient(uuid)
      }

      def onError(e: String, in: Input[IndividualMessage]) = {
        Logger.error("ClientsActor.receive: RegisterClient: onError: " + e)
        self ! DeregisterClient(uuid)
      }

      val individualEnumerator = Concurrent.unicast[IndividualMessage](onStart, onComplete, onError)

      sender ! individualEnumerator
    }

    case DeregisterClient(uuid) => {
      Logger.debug("ClientsActor.receive: DeregisterClient: uuid: " + uuid)
      clients -= uuid
    }

  }

}

object ClientsActor {
  lazy val actor = Akka.system.actorOf(Props(new ClientsActor), "clients-actor")

  def apply() = actor
}
