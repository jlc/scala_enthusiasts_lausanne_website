package controllers

import play.api._
import play.api.mvc._

object Templates extends Controller with ControllerHelper {

  def ngAppAddressMap = Action { implicit request =>
    Ok(views.html.templates.ngapp.addressMap()(clientLanguage))
  }

  def ngAppAddressContact = Action { implicit request =>
    Ok(views.html.templates.ngapp.addressContact()(clientLanguage))
  }

  def ngAppSponsorsCrossingTech = Action { implicit request =>
    Ok(views.html.templates.ngapp.sponsorsCrossingTech()(clientLanguage))
  }

  def ngAppSponsorsEpfl = Action { implicit request =>
    Ok(views.html.templates.ngapp.sponsorsEpfl()(clientLanguage))
  }

}
