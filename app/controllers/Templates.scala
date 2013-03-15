package controllers

import play.api._
import play.api.mvc._

object Templates extends Controller with ControllerHelper {

  def addressMap = Action { implicit request =>
    Ok(views.html.templates.addressMap()(clientLanguage))
  }

  def addressContact = Action { implicit request =>
    Ok(views.html.templates.addressContact()(clientLanguage))
  }

  def sponsorsCrossingTech = Action { implicit request =>
    Ok(views.html.templates.sponsorsCrossingTech()(clientLanguage))
  }

  def sponsorsEpfl = Action { implicit request =>
    Ok(views.html.templates.sponsorsEpfl()(clientLanguage))
  }

  def meetingsAgenda = Action { implicit request =>
    Ok(views.html.templates.meetingsAgenda()(clientLanguage))
  }

  def meetingsSessions = Action { implicit request =>
    Ok(views.html.templates.meetingsSessions()(clientLanguage))
  }

  def meetingsSpeakers = Action { implicit request =>
    Ok(views.html.templates.meetingsSpeakers()(clientLanguage))
  }

}
