package controllers

import scala.collection.mutable

import play.api.mvc.{Request, AnyContent}

import net.sf.uadetector.{UserAgent, UserAgentStringParser}
import net.sf.uadetector.service.UADetectorServiceFactory

object UserAgentSpy {

  private lazy val parser = 
    try { UADetectorServiceFactory.getCachingAndUpdatingParser() }
    catch { case e: Throwable => UADetectorServiceFactory.getResourceModuleParser() }

  private val cache = new mutable.HashMap[String, UserAgent]

  def apply()(implicit request: Request[AnyContent]): UserAgent = {
    val uaString = request.headers("User-Agent")
    cache.getOrElse(uaString, {
      val userAgent = parser.parse(uaString)
      cache += (uaString -> userAgent)
      userAgent
    })
  }

  implicit def toClientInfo(userAgent: UserAgent): ClientMessages.UserAgentInfo = {
    ClientMessages.UserAgentInfo(
      userAgent.getFamily().getName(),
      userAgent.getVersionNumber().toVersionString(),
      userAgent.getTypeName(),
      userAgent.getProducer(),
      userAgent.getOperatingSystem().getFamilyName(),
      userAgent.getOperatingSystem().getVersionNumber().toVersionString(),
      "location"
    )
  }
}
