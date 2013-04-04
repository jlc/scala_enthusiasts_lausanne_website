package controllers

import java.util.UUID

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current
import play.api.i18n.Messages

import models.{User, Group, UsersDao}

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
      f(user).withSession(request.session + (SessionKey.UserUUID -> user.uuid.toString))
    }
  }

  def ensureMembership(group: Group)(f: (User) => Result)(implicit request: Request[_]): Result = {
    loggedAs { user =>
      if (user.group == group) f(user)
      else {
        Logger.error("ensureMembership: forbidden access for user "+user.uuid+", email: "+user.email+", address: "+request.remoteAddress)
        Forbidden(Messages("common.forbidden"))
      }
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

