package controllers

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

import models.{User, UsersDao}

trait ControllerHelper {

  def loggedAs(f: (User) => Result)(implicit request: Request[_]): Result =
    request.session.get(SessionKey.UserId).map { email =>
      val user = UsersDao.getUser(email)

      Logger.info("loggedAs: user for '"+email+"': "+user)

      f(user)
    }.getOrElse {
      f(User.anonymous)
    }

  def clientLanguage(implicit request: Request[_]) =
    request.session.get(SessionKey.Lang) map { Lang(_) } getOrElse { Lang.preferred(request.acceptLanguages) }

}

