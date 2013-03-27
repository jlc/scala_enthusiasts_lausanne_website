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

    val fail = Json.stringify(Json.obj("return" -> false, "error" -> "Invalid authentication"))
    val success = Json.stringify(Json.obj("return" -> true))

    loginForm.bindFromRequest.fold(
      loginWithErrors => {
        Logger.debug("authenticate: form does not validate")
        Ok(fail).withSession(session - SessionKey.UserUUID)
      },
      login => {
        val (email, password) = login
        UsersDao.authenticate(email, password).map { user =>
          Ok(success).withSession(SessionKey.UserUUID -> user.uuid.toString)
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
