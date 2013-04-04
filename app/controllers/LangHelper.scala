package controllers

import play.Logger
import play.api.mvc.{Request, AnyContent}
import play.api.i18n.Lang
import play.api.Play.current

trait LangHelper {

  def clientLanguage(implicit request: Request[AnyContent]): Lang =
    request.session.get(SessionKey.Lang) map { Lang(_) } getOrElse { Lang.preferred(request.acceptLanguages) }
}
