package controllers

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json.Json

import models.{User, Group, UsersDao}

object Application extends Controller with LangHelper {

  def index = GuardedAction { implicit request => user =>
    Ok(views.html.index()(user, clientLanguage))
  }
  
  def lang(l: String) = Action { implicit request =>
    val uri: String = request.headers.get("Referer").getOrElse(routes.Application.index().toString)

    Lang.get(l) map { lang =>
      Redirect(uri).withSession(session + (SessionKey.Lang -> l))
    } getOrElse {
      Redirect(uri).withSession(session - SessionKey.Lang)
    }
  }

  // TODO: this should definitely be removed!
  def initialise = Action { implicit request =>
    UsersDao.initialise()
    Ok("done")
  }

  /*
   * Authentication
   */

  def authenticate = Action { implicit request =>
    val loginForm = Form(
      tuple(
        "email" -> email,
        "password" -> nonEmptyText
      )
    )

    val fail = Json.obj("return" -> false, "error" -> "Invalid authentication")
    val success = Json.obj("return" -> true)

    loginForm.bindFromRequest.fold(
      loginWithErrors => {
        Logger.debug("authenticate: form does not validate")
        Ok(fail).withSession(session - SessionKey.UserUUID)
      },
      credentials => {
        UsersDao.authenticate(credentials._1, credentials._2).map { user =>
          Ok(success).withSession(session + (SessionKey.UserUUID -> user.uuid.toString))
        }.getOrElse {
          Ok(fail).withSession(session - SessionKey.UserUUID)
        }
      }
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.index()).withSession(session - SessionKey.UserUUID)
  }
}
