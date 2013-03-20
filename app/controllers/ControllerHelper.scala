package controllers

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

trait ControllerHelper {

  def authenticated(f: (String) => Result)(implicit request: Request[_]): Option[Result] = {
    request.session.get(SessionKey.User).map { user => f(user) }
  }

  def clientLanguage(implicit request: Request[_]) =
    request.session.get(SessionKey.Lang) map { Lang(_) } getOrElse { Lang.preferred(request.acceptLanguages) }


}

