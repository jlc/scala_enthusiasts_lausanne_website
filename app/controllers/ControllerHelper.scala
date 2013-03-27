package controllers

import java.util.UUID

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

import models.{User, UsersDao}

trait ControllerHelper {

  def loggedAs(f: (User) => Result)(implicit request: Request[_]): Result = {
    request.session.get(SessionKey.UserUUID).map { uuidStr =>
      val uuid = UUID.fromString(uuidStr)
      val user = UsersDao.getUser(uuid).getOrElse(User.anonymous(uuid))

      Logger.info("loggedAs: "+user)
      f(user)
    }.getOrElse {
      val user = User.anonymous()

      Logger.debug("loggedAs: new anonymous: " + user)
      f(user).withSession(SessionKey.UserUUID -> user.uuid.toString)
    }
  }

  def clientLanguage(implicit request: Request[_]) =
    request.session.get(SessionKey.Lang) map { Lang(_) } getOrElse { Lang.preferred(request.acceptLanguages) }

  def informOtherClient(implicit request: Request[_]) = {
    import controllers.UserAgentSpy._
    val userAgent = UserAgentSpy()
    val remoteAddress = request.remoteAddress

    ClientsActor() ! ClientsActorMessages.PageViewedBy(userAgent)
  }

}

