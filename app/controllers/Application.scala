package controllers

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

object Application extends Controller {
  
  private object SessionValue {
    val Lang = "lang"
    val User = "well_known_user"
  }

  private def authenticated(f: (String) => Result)(implicit request: Request[_]): Option[Result] = {
    request.session.get(SessionValue.User).map { user => f(user) }
  }

  private def clientLanguage(implicit request: Request[_]) =
    request.session.get(SessionValue.Lang) map { Lang(_) } getOrElse { Lang.preferred(request.acceptLanguages) }



  def index() = Action { implicit request =>
    Ok(views.html.index()(clientLanguage))
  }

  def meetings = Action { implicit request =>
    Ok(views.html.meetings()(clientLanguage))
  }

  def address = Action { implicit request =>
    Ok(views.html.address()(clientLanguage))
  }

  def sponsors = Action { implicit request =>
    Ok(views.html.sponsors()(clientLanguage))
  }
  
  def lang(l: String) = Action { implicit request =>
    def redirect = Redirect(routes.Application.index())

    Lang.get(l) map { lang =>
      Logger.debug("lang: Setting lang to: " + l)
      redirect.withSession(SessionValue.Lang -> l)
    } getOrElse {
      Logger.debug("lang: Removing previous setting of lang")
      redirect.withSession(session - SessionValue.Lang)
    }
  }

}
