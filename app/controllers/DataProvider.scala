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

import play.api.i18n.Messages

import models.{MiscContent, EnthusiastSession, ContentDao, Group, UsersDao}
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
        "speaker" -> optional(text),
        "content" -> nonEmptyText
      )
    )

    textForm.bindFromRequest.fold(
      formWithError => {
        Logger.debug("saveSession: form with error: " + formWithError)
        Ok(Json.obj("return" -> false))
      },
      form => {
        val (title, isoDate, speakerOpt, content) = form
        val uuid = if (suuid == "new") UUID.randomUUID else UUID.fromString(suuid)
        val date = javax.xml.bind.DatatypeConverter.parseDateTime(isoDate).getTime
        val speaker = speakerOpt.getOrElse("")

        val eSession = EnthusiastSession(uuid, clientLanguage, date, title, speaker, content)
        ContentDao.saveEnthusiastSession(eSession)
        val json = Json.toJson(eSession)
        Logger.debug("saveSession: uuid: " + suuid + " - json: " + json)
        Ok(json)
      }
    )
  }

  def savePassword = DiscretGuardedAction.restrictedTo(Group.God()) { implicit request => user =>
    val textForm = Form(
      tuple(
        "oldPassword" -> nonEmptyText,
        "newPassword" -> nonEmptyText,
        "newPasswordBis" -> nonEmptyText
      )
    )

    textForm.bindFromRequest.fold(
      formWithError => {
        Logger.debug("savePassword: form with error: " + formWithError)
        Ok(Json.obj("return" -> false))
      },
      form => {
        val (oldPassword, newPassword, newPasswordBis) = (form._1.trim, form._2.trim, form._3.trim)

        // check new password
        if (newPassword != newPasswordBis) {
          Logger.debug("savePassword: " + user.uuid + " new password does not match")
          Ok(Json.obj("say" -> "dontmatch"))
        }
        // check old password
        else if (user.password != oldPassword) {
          Logger.warn("savePassword: " + user.uuid + " old password not valid")
          Ok(Json.obj("say" -> "wrongoldpassword"))
        }
        else {
          Logger.debug("savePassword: " + user.uuid + " saving new password")
          val updatedUser = user.withPassword(newPassword)
          UsersDao.saveUser(updatedUser)
          Ok(Json.obj("say" -> "ok"))
        }
      }
    )
  }

  def getText(id: String) = DiscretGuardedAction { implicit request => user =>
    val fullId = "client." + id
    val text = Messages(fullId)(clientLanguage)
    Ok(Json.obj("text" -> text))
  }

  def searchText(regex: String) = DiscretGuardedAction { implicit request => user =>

    val lang =
      if (Messages messages(current) get (clientLanguage.code) map (_.isEmpty) getOrElse (true) ) "default"
      else clientLanguage.code

    val stripClient = "^client\\.(.*)".r

    val results =
      Messages.messages(current)(lang).map {
        case (fullKey, value) =>
          stripClient findFirstIn fullKey match {
            case Some(stripClient(key)) =>
              // if regex contains something and is found first in key, then keep the pair (key, value),
              // otherwise, keep all pair (key, value)
              regex.r findFirstIn key map (m => (key -> value))
            case None => None
          }
      }.flatten.toMap

    Ok(Json.toJson(results))
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
