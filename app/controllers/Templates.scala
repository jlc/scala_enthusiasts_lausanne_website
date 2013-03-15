package controllers

import play.api._
import play.api.mvc._

object Templates extends Controller with ControllerHelper {

  def ngAppAddressMap = Action { implicit request =>
    Ok(views.html.templates.addressMap()(clientLanguage))
  }

  def ngAppAddressContact = Action { implicit request =>
    Ok(views.html.templates.addressContact()(clientLanguage))
  }

  def ngAppSponsorsCrossingTech = Action { implicit request =>
    Ok(views.html.templates.sponsorsCrossingTech()(clientLanguage))
  }

  def ngAppSponsorsEpfl = Action { implicit request =>
    Ok(views.html.templates.sponsorsEpfl()(clientLanguage))
  }

}
