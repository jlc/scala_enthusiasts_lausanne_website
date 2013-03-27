package controllers

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import akka.pattern.ask
import akka.util.Timeout

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.libs.EventSource
import play.api.libs.json.{Json, JsValue}
import play.api.libs.iteratee.{Concurrent, Enumeratee, Enumerator, Iteratee}

import models.{User, UsersDao}

import controllers.ClientMessages._
import controllers.ClientsActorMessages._

object Streamer extends Controller with ControllerHelper {

  implicit val timeout: Timeout = 5 seconds

  /*
   * EventSource() implicit parameters are defined in ClientMessages
   */

  def otherUserAgent = Action { implicit request =>
    loggedAs { user =>
      Async {
        (ClientsActor() ? JoinUAInfoBroadcast()).mapTo[Enumerator[UserAgentInfo]].map {
          case enumerator =>
            Ok.stream(enumerator &> EventSource()).withHeaders(
              "Cache-Control" -> "no-cache",
              "Connection" -> "keep-alive"
            ).as("text/event-stream")
        }
      }
    }
  }

  def individualMessage = Action { implicit request =>
    loggedAs { user =>
      Async {
        (ClientsActor() ? RegisterClient(user.uuid)).mapTo[Enumerator[IndividualMessage]].map {
          case enumerator =>
            Ok.stream(enumerator &> EventSource()).withHeaders(
              "Cache-Control" -> "no-cache",
              "Connection" -> "keep-alive"
            ).as("text/event-stream")
        }
      }
    }
  }

}
