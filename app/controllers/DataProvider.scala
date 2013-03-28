package controllers

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import java.util.{Date => JDate}

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

import models.{MiscContent, EnthusiastSession, ContentDao, MCType, Group}

object DataProvider extends Controller with ControllerHelper {

  def getIntroduction = Action { implicit request =>
    loggedAs { user =>
      Ok(getMiscContent(MCType.Introduction))
    }
  }
  def saveIntroduction = Action { implicit request =>
    ensureMembership(Group.God()) { user =>
      Ok(saveMiscContent(MCType.Introduction, nonEmptyText))
    }
  }

  def getAnnouncement = Action { implicit request =>
    loggedAs { user =>
      Ok(getMiscContent(MCType.Announcement))
    }
  }
  def saveAnnouncement = Action { implicit request =>
    ensureMembership(Group.God()) { user =>
      Ok(saveMiscContent(MCType.Announcement, text))
    }
  }

  private def getMiscContent(mctype: MCType): JsValue = {
    ContentDao.getMiscContent(mctype).
      map(_.toJson).
      getOrElse(Json.obj())
  }

  private def saveMiscContent(mctype: MCType, mapping: Mapping[String])(implicit request: Request[_]): JsValue = {
    val textForm = Form(
      tuple(
        "title" -> mapping,
        "content" -> mapping
      )
    )

    textForm.bindFromRequest.fold(
      formWithError => {
        Logger.debug("saveMiscContent: form with error: " + formWithError)
        Json.obj("return" -> false)
      },
      form => {
        val (title, content) = form
        val mc = MiscContent(mctype, new JDate, title, content)
        ContentDao.saveMiscContent(mc)
        mc.toJson
      }
    )
  }
}
