package controllers

import play.api._
import play.api.mvc._

object Templates extends Controller with LangHelper {

  def addressMap = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.addressMap()(clientLanguage))
  }

  def addressContact = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.addressContact()(clientLanguage))
  }

  def sponsorsCrossingTech = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.sponsorsCrossingTech()(clientLanguage))
  }

  def sponsorsEpfl = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.sponsorsEpfl()(clientLanguage))
  }

  def meetingsAgenda = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.meetingsAgenda()(clientLanguage))
  }

  def meetingsSessions = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.meetingsSessions()(clientLanguage))
  }

  def meetingsSpeakers = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.meetingsSpeakers()(clientLanguage))
  }

  def adminEditIntroduction = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.adminEditIntroduction()(clientLanguage))
  }

  def adminEditAnnouncement = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.adminEditAnnouncement()(clientLanguage))
  }

  def adminEditSessions = DiscretGuardedAction { implicit request => user =>
    Ok(views.html.templates.adminEditSessions()(clientLanguage))
  }

}
