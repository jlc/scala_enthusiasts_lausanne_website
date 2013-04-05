package controllers

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import java.util.{Date => JDate, UUID}

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

import models.{MiscContent, EnthusiastSession, ContentDao, Group}
import models.ContentWrites._

object DataProvider extends Controller with LangHelper {

  def getIntroduction = DiscretGuardedAction { implicit request => user =>
    Ok(getMiscContent(MiscContent.Introduction, clientLanguage))
  }
  def saveIntroduction = DiscretGuardedAction.restrictedTo(Group.God()) { implicit request => user =>
    Ok(saveMiscContent(MiscContent.Introduction, clientLanguage, nonEmptyText))
  }

  def getAnnouncement = DiscretGuardedAction { implicit request => user =>
    Ok(getMiscContent(MiscContent.Announcement, clientLanguage))
  }
  def saveAnnouncement = DiscretGuardedAction.restrictedTo(Group.God()) { implicit request => user =>
    Ok(saveMiscContent(MiscContent.Announcement, clientLanguage, text))
  }

  def listSession = DiscretGuardedAction { implicit request => user =>
    Ok(
      Json.toJson(ContentDao.listEnthusiastSession(clientLanguage))
    )
  }

  def getSession(suuid: String) = DiscretGuardedAction { implicit request => user =>
    val uuid = UUID.fromString(suuid)
    Ok(
      ContentDao.getEnthusiastSession(uuid).
        map( es => Json.toJson(es)).
        getOrElse(Json.obj())
    )
  }

  def deleteSession(suuid: String) = DiscretGuardedAction.restrictedTo(Group.God()) { implicit request => user =>
    val uuid = UUID.fromString(suuid)
    ContentDao.deleteEnthusiastSession(uuid)
    Ok(suuid)
  }

  def saveSession(suuid: String) = DiscretGuardedAction.restrictedTo(Group.God()) { implicit request => user =>
    val textForm = Form(
      tuple(
        "title" -> nonEmptyText,
        "date" -> nonEmptyText,
        "speaker" -> text,
        "content" -> nonEmptyText
      )
    )

    textForm.bindFromRequest.fold(
      formWithError => {
        Logger.debug("saveSession: form with error: " + formWithError)
        Ok(Json.obj("return" -> false))
      },
      form => {
        val (title, isoDate, speaker, content) = form
        val uuid = if (suuid == "new") UUID.randomUUID else UUID.fromString(suuid)
        val date = javax.xml.bind.DatatypeConverter.parseDateTime(isoDate).getTime
        val eSession = EnthusiastSession(uuid, clientLanguage, date, title, speaker, content)
        ContentDao.saveEnthusiastSession(eSession)
        val json = Json.toJson(eSession)
        Logger.debug("saveSession: uuid: " + suuid + " - json: " + json)
        Ok(json)
      }
    )
  }

  private def getMiscContent(mctype: MiscContent.Type, lang: Lang): JsValue = {
    ContentDao.getMiscContent(mctype, lang).
      map(Json.toJson(_)).
      getOrElse(Json.obj())
  }

  private def saveMiscContent(mctype: MiscContent.Type, lang: Lang, mapping: Mapping[String])(implicit request: Request[AnyContent]): JsValue = {
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
        val mc = MiscContent(lang, mctype, new JDate, title, content)
        ContentDao.saveMiscContent(mc)
        Json.toJson(mc)
      }
    )
  }
}
