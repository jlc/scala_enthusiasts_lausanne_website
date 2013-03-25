package controllers

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json.Json

import models.{User, UsersDao}

object Application extends Controller with ControllerHelper {

  def index() = Action { implicit request =>
    loggedAs { user =>
      informOtherClient
      Ok(views.html.index()(user, clientLanguage))
    }
  }

  def meetings = Action { implicit request =>
    loggedAs { user =>
      informOtherClient
      Ok(views.html.meetings()(user, clientLanguage))
    }
  }

  def address = Action { implicit request =>
    loggedAs { user =>
      informOtherClient
      Ok(views.html.address()(user, clientLanguage))
    }
  }

  def sponsors = Action { implicit request =>
    loggedAs { user =>
      informOtherClient
      Ok(views.html.sponsors()(user, clientLanguage))
    }
  }
  
  def lang(l: String) = Action { implicit request =>
    def redirect = Redirect(routes.Application.index())

    Lang.get(l) map { lang =>
      redirect.withSession(SessionKey.Lang -> l)
    } getOrElse {
      redirect.withSession(session - SessionKey.Lang)
    }
  }

  def admin = Action { implicit request =>
      informOtherClient
    Ok("")
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

    val fail = Json.stringify(Json.obj("return" -> false, "error" -> "Invalid authentication"))
    val success = Json.stringify(Json.obj("return" -> true))

    loginForm.bindFromRequest.fold(
      loginWithErrors => {
        Logger.debug("authenticate: form does not validate")
        Ok(fail).withSession(session - SessionKey.UserId)
      },
      login => {
        if (UsersDao.authenticate(login._1, login._2))
          Ok(success).withSession(SessionKey.UserId -> login._1)
        else
          Ok(fail).withSession(session - SessionKey.UserId)
      }
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.index()).withSession(session - SessionKey.UserId)
  }
}
