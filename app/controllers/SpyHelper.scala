package controllers

import java.util.UUID

import play.api.mvc.{Request, AnyContent}

import play.api.Logger

trait SpyHelper {

  def informOtherClient(implicit request: Request[AnyContent]) = {
    import controllers.UserAgentSpy._
    val userAgent = UserAgentSpy()
    val remoteAddress = request.remoteAddress

    ClientsActor() ! ClientsActorMessages.PageViewedBy(userAgent)
  }

}
