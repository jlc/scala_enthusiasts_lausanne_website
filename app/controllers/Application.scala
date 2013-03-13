package controllers

import play.api._
import play.api.mvc._

import play.api.i18n.Lang
import play.api.Play.current

object Application extends Controller {
  
  def authenticated(f: (String) => Result)(implicit request: Request[_]): Option[Result] = {
    request.session.get("well_known_user").map { user => f(user) }
  }
      
  def index() = Action { implicit request =>
    //println("Languages: " + request.acceptLanguages.map(_.code).mkString(","))
    val lang = Lang.get("en") getOrElse Lang.preferred(request.acceptLanguages)

    authenticated { user =>      
      Ok(views.html.index()(lang))
    } getOrElse {
      Ok(views.html.index()(lang))
    }
  }

  def meetings = Action {
    Ok(views.html.meetings())
  }

  def address = Action {
    Ok(views.html.address())
  }

  def sponsors = Action {
    Ok(views.html.sponsors())
  }
  
}
