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

import models.{MiscContent, EnthusiastSession, ContentDao, MCType}

import controllers.ClientMessages._
import controllers.ClientsActorMessages._

object DataProvider extends Controller with ControllerHelper {

  def getIntroduction = Action { implicit request =>
    Ok(
      ContentDao.getMiscContent(MCType.Introduction).
        map(_.toJson).
        getOrElse(Json.obj())
    )
  }

  // TODO: guard agains unauthorized accesses
  def saveIntroduction = Action { implicit request =>
    Logger.debug("saveAdminIntroduction")
    val textForm = Form(
      tuple(
        "title" -> nonEmptyText,
        "content" -> nonEmptyText
      )
    )

    textForm.bindFromRequest.fold(
      formWithError => {
        Logger.debug("form with error: " + formWithError)
        Ok(Json.obj("return" -> false))
      },
      form => {
        val (title, content) = form
        val mc = MiscContent(MCType.Introduction, new JDate, title, content)
        Logger.debug("saveAdminIntroduction: form ok, saving: " + mc)
        ContentDao.saveMiscContent(mc)
        Ok(mc.toJson)
      }
    )
  }
}
