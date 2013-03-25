package controllers

import scala.collection.mutable

import play.api.mvc.Request

import net.sf.uadetector.{UserAgent, UserAgentStringParser}
import net.sf.uadetector.service.UADetectorServiceFactory

object UserAgentSpy {

  private val parser = UADetectorServiceFactory.getCachingAndUpdatingParser();

  private val cache = new mutable.HashMap[String, UserAgent]

  def apply()(implicit request: Request[_]): UserAgent = {
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
